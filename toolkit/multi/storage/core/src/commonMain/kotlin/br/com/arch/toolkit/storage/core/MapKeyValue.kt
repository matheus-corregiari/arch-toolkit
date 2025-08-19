package br.com.arch.toolkit.storage.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map

@StorageApi
internal class MapKeyValue<Current, Transformed> internal constructor(
    private val keyValue: KeyValue<Current>,
    private val mapTo: (Current) -> Transformed,
    private val mapBack: (Transformed) -> Current
) : KeyValue<Transformed>() {

    override var lastValue: Transformed = mapTo(keyValue.lastValue)

    override fun get() = keyValue.get().map { mapTo(it) }

    override fun set(value: Transformed, scope: CoroutineScope?) =
        keyValue.set(mapBack(value), scope ?: this.scope)
}
