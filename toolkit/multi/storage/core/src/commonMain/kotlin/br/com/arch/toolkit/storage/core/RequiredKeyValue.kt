package br.com.arch.toolkit.storage.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.mapNotNull

@StorageApi
internal class RequiredKeyValue<ResultData> internal constructor(
    private val keyValue: KeyValue<ResultData?>,
    private val default: (() -> ResultData)?
) : KeyValue<ResultData>() {

    override var lastValue: ResultData = keyValue.lastValue
        ?: default?.invokeCatching()
        ?: error("Required KeyValue does not have a last value")

    override fun get() = keyValue.get().mapNotNull { it }

    override fun set(value: ResultData, scope: CoroutineScope?) =
        keyValue.set(value, scope ?: this.scope)

    fun <R> (() -> R).invokeCatching() = runCatching { invoke() }.getOrNull()
}
