package br.com.arch.toolkit.storage.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.mapNotNull

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
            scope = null
        )

    override fun get() = keyValue.get().mapNotNull { it }

    override fun set(value: ResultData, scope: CoroutineScope?) = keyValue.set(
        value = value,
        scope = scope ?: this.scope
    )

    private fun <R> (() -> R).invokeCatching() = runCatching { invoke() }.getOrNull()
}
