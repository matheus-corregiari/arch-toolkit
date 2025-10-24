package br.com.arch.toolkit.stateHandle

import androidx.lifecycle.SavedStateHandle
import br.com.arch.toolkit.flow.ResponseMutableStateFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.splinter.extension.invokeCatching
import br.com.arch.toolkit.stateHandle.StateValue.Companion.default
import br.com.arch.toolkit.stateHandle.ViewModelState.Regular
import br.com.arch.toolkit.stateHandle.ViewModelState.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Typed, lifecycle-aware state holder backed by [SavedStateHandle].
 *
 * Provides:
 * - In-memory value (`data`) synced to `SavedStateHandle`.
 * - Optional JSON fallback when a value cannot be directly saved (e.g., non-parcelable).
 * - Reactive streams for UI consumption.
 *
 * ## Variants
 * - [Regular] – exposes `Flow<T?>` of the current value.
 * - [Result] – exposes `Flow<DataResult<T>>` for request/result patterns.
 *
 * ## When to use
 * - UI state that must survive configuration and process recreation.
 * - Small payloads you want to read/write and observe from a ViewModel.
 *
 * ## When **not** to use
 * - Long-term persistence (use your storage layer).
 * - Large graphs/blobs (serialize only minimal fields or store IDs).
 *
 * @param name Logical key for the value (also used to build the JSON shadow key).
 * @param json JSON engine used when falling back to string persistence.
 * @param serializer Optional serializer for `T` used by JSON fallback.
 * @param stateHandle Underlying Android saved state storage.
 * @param scope Coroutine scope tied to the ViewModel lifecycle.
 */
sealed class ViewModelState<T : Any>(
    val name: String,
    val json: Json,
    val serializer: KSerializer<T>?,
    protected val stateHandle: SavedStateHandle,
    val scope: CoroutineScope
) {
    // Shadow key for JSON string persistence when direct set/remove fails.
    private var jsonData: String? by stateHandle.value("$name-primitive")

    /**
     * Backing value synchronized with [stateHandle].
     *
     * - Write path: saves directly; on failure, persists JSON shadow (if serializer available).
     * - Read path: returns saved value or (when absent) tries to decode from JSON shadow.
     */
    protected var data: T? by stateHandle.value<T>(
        key = name,
        setError = { data, _ ->
            scope.launch {
                val serializer = serializer ?: return@launch
                jsonData = json.encodeToString(serializer, data)
            }
        }
    ).default {
        val dataAsString = jsonData ?: return@default null
        val serializer = serializer ?: return@default null
        json.decodeFromString(serializer, dataAsString)
    }

    /** Stream of the typed value from [SavedStateHandle]. */
    protected val dataFlow = stateHandle.getStateFlow<T?>(
        key = name,
        initialValue = null
    )

    /** Stream of the shadow JSON string. */
    protected val jsonDataFlow = stateHandle.getStateFlow<String?>(
        key = "$name-primitive",
        initialValue = null
    )

    /** Returns the current in-memory value. */
    open fun get(): T? = data

    /**
     * Sets a new value.
     *
     * - `null` clears both the typed entry and the JSON shadow.
     * - Non-null attempts direct persistence; on failure, stores JSON shadow (if possible).
     */
    open fun set(value: T?) {
        scope.launch {
            data = value
            if (value == null) jsonData = null
        }
    }

    /**
     * Sets a new value with optional distinctness check.
     *
     * @param distinct When `true`, writes only if `value` differs from current.
     */
    open fun set(value: T?, distinct: Boolean) {
        if (distinct && value != get()) {
            set(value)
        } else if (!distinct) {
            set(value)
        }
    }

    /** Clears the current value (equivalent to `set(null)`). */
    fun invalidate() = set(null)

    /**
     * Basic state holder exposing `Flow<T?>`.
     *
     * Combines direct `SavedStateHandle` updates and shadow JSON updates
     * into a single cold flow, shared on demand.
     */
    class Regular<T : Any>(
        name: String,
        json: Json,
        serializer: KSerializer<T>?,
        stateHandle: SavedStateHandle,
        scope: CoroutineScope
    ) : ViewModelState<T>(name, json, serializer, stateHandle, scope) {
        /**
         * Stream of the current value.
         *
         * Emits:
         * - Typed updates from the `SavedStateHandle`.
         * - Decoded values from the JSON shadow when present.
         *
         * Sharing: [SharingStarted.WhileSubscribed].
         */
        fun flow(): Flow<T?> = flow {
            coroutineScope { launch { dataFlow.collect(::emit) } }
            coroutineScope {
                launch {
                    jsonDataFlow.mapNotNull {
                        val dataAsString = it ?: return@mapNotNull null
                        val serializer = serializer ?: return@mapNotNull null
                        json.decodeFromString(serializer, dataAsString)
                    }.collect(::emit)
                }
            }
        }.shareIn(scope, SharingStarted.WhileSubscribed())
    }

    /**
     * Result-oriented state holder exposing `Flow<DataResult<T>>`.
     *
     * Keeps the last successful data in `SavedStateHandle` (with JSON fallback),
     * while emitting request/response states independently through an internal
     * [ResponseMutableStateFlow].
     */
    class Result<T : Any>(
        name: String,
        json: Json,
        serializer: KSerializer<T>?,
        stateHandle: SavedStateHandle,
        scope: CoroutineScope
    ) : ViewModelState<T>(name, json, serializer, stateHandle, scope) {

        private val resultFlow = ResponseMutableStateFlow<T>()
        private var job: Job? = null

        /** Reactive stream of [DataResult]. */
        fun flow(): Flow<DataResult<T>> = resultFlow.asStateFlow()

        /** Convenience: sets underlying data from a [DataResult]. */
        fun set(value: DataResult<T>?) = set(value = value?.data)

        /**
         * Loads data using the provided [func], emitting through [flow].
         *
         * Behavior:
         * - If current state is present and [evaluate] returns `true`, it **reuses** the state.
         * - Otherwise, clears current state, emits `None/Loading`, then collects [func].
         * - On success, persists the data (typed or JSON shadow fallback) and emits the result.
         *
         * @param evaluate Optional predicate to validate the current state before skipping load.
         * @param func Producer of a `Flow<DataResult<T>>` (e.g., repository request).
         */
        fun load(
            evaluate: suspend (T) -> Boolean = { true },
            func: suspend () -> Flow<DataResult<T>>
        ) {
            job?.cancel()
            job = scope.launch {
                val statedData = data
                val isCurrentStateValid = statedData?.let { state ->
                    evaluate.invokeCatching(state).getOrDefault(false)
                } == true

                if (statedData != null && isCurrentStateValid) {
                    return@launch
                } else {
                    invalidate()
                    resultFlow.emitNone()
                    func.invokeCatching().getOrNull()?.collect { result ->
                        if (result.isSuccess) set(result.data ?: statedData)
                        resultFlow.emit(result)
                    }
                }
            }
        }
    }
}
