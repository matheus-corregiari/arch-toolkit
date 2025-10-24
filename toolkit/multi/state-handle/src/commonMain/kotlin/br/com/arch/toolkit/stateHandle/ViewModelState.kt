package br.com.arch.toolkit.stateHandle

import androidx.lifecycle.SavedStateHandle
import br.com.arch.toolkit.flow.ResponseMutableStateFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.splinter.extension.invokeCatching
import br.com.arch.toolkit.stateHandle.StateValue.Companion.default
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

sealed class ViewModelState<T : Any>(
    val name: String,
    val json: Json,
    val serializer: KSerializer<T>?,
    protected val stateHandle: SavedStateHandle,
    val scope: CoroutineScope
) {
    private var jsonData: String? by stateHandle.value("$name-primitive")
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

    protected val dataFlow = stateHandle.getStateFlow<T?>(
        key = name,
        initialValue = null
    )
    protected val jsonDataFlow = stateHandle.getStateFlow<String?>(
        key = "$name-primitive",
        initialValue = null
    )

    open fun get(): T? = data
    open fun set(value: T?) {
        scope.launch {
            data = value
            if (value == null) jsonData = null
        }
    }

    open fun set(value: T?, distinct: Boolean) {
        if (distinct && value != get()) {
            set(value)
        } else if (!distinct) {
            set(value)
        }
    }

    fun invalidate() = set(null)

    class Regular<T : Any>(
        name: String,
        json: Json,
        serializer: KSerializer<T>?,
        stateHandle: SavedStateHandle,
        scope: CoroutineScope
    ) : ViewModelState<T>(name, json, serializer, stateHandle, scope) {
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

    class Result<T : Any>(
        name: String,
        json: Json,
        serializer: KSerializer<T>?,
        stateHandle: SavedStateHandle,
        scope: CoroutineScope
    ) : ViewModelState<T>(name, json, serializer, stateHandle, scope) {

        private val resultFlow = ResponseMutableStateFlow<T>()
        fun flow(): Flow<DataResult<T>> = resultFlow.asStateFlow()

        fun set(value: DataResult<T>?) = set(value = value?.data)

        private var job: Job? = null
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
