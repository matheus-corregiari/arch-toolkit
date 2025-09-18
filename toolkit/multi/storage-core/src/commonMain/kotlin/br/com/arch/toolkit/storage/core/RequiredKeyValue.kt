package br.com.arch.toolkit.storage.core

import br.com.arch.toolkit.storage.core.KeyValue.Companion.required
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.mapNotNull

/**
 * A [KeyValue] adapter that enforces non-null values.
 *
 * [RequiredKeyValue] wraps another [KeyValue] of nullable type and guarantees that
 * consumers will only interact with non-null values. A default provider can be supplied
 * to handle cases where no value has been set yet.
 *
 * ---
 *
 * ### Behavior
 * - **Read:** If the underlying [KeyValue] has no value, [default] is invoked (if provided).
 *   If both are missing, an [IllegalStateException] is thrown.
 * - **Write:** Null assignments are rejected with an [IllegalStateException].
 * - **Flow:** [get] filters out null values using [mapNotNull].
 *
 * ---
 *
 * ### Example: With explicit default
 * ```kotlin
 * val counter: KeyValue<Int> = storageProvider.int("counter")
 *     .required { 0 }
 *
 * // Always safe, defaults to 0 if unset
 * println("Counter is ${counter.instant()}")
 *
 * counter.set(5) // ✅ works
 * counter.set(null) // ❌ throws IllegalStateException
 * ```
 *
 * ### Example: Without default
 * ```kotlin
 * val userId: KeyValue<String> = storageProvider.string("user_id").required()
 *
 * // If no value is set yet, this will throw
 * try {
 *     println("User: ${userId.instant()}")
 * } catch (e: IllegalStateException) {
 *     println("Missing required value")
 * }
 * ```
 *
 * @param ResultData The non-null type guaranteed by this adapter.
 * @property keyValue The underlying nullable [KeyValue].
 * @property default Optional fallback provider invoked when no value is available.
 *
 * @see KeyValue.required Convenient factory method.
 */
@StorageApi
internal class RequiredKeyValue<ResultData> internal constructor(
    private val keyValue: KeyValue<ResultData?>,
    private val default: (() -> ResultData)?
) : KeyValue<ResultData>() {

    override var lastValue: ResultData
        get() = keyValue.lastValue
            ?: default?.invokeCatching()
            ?: error("Required KeyValue does not have a last value")
        set(value) = set(
            value = value ?: error("Required KeyValue cannot have a null value"),
        )

    override fun get() = keyValue.get().mapNotNull { it }

    override fun set(value: ResultData, scope: CoroutineScope) = keyValue.set(value, scope)

    private fun <R> (() -> R).invokeCatching() = runCatching { invoke() }.getOrNull()
}
