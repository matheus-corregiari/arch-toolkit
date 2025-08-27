@file:Suppress("UNCHECKED_CAST")

package br.com.arch.toolkit.storage.memory

import br.com.arch.toolkit.storage.core.KeyValue
import br.com.arch.toolkit.storage.core.KeyValue.Companion.required
import br.com.arch.toolkit.storage.core.StorageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.enums.EnumEntries

class MemoryStoreProvider(
    private val database: MutableMap<String, MutableStateFlow<*>>
) : StorageProvider() {

    override fun boolean(key: String): KeyValue<Boolean?> =
        MemoryKeyValue(flow = flow(key))

    override fun byteArray(key: String): KeyValue<ByteArray?> =
        MemoryKeyValue(flow = flow(key))

    override fun double(key: String): KeyValue<Double?> =
        MemoryKeyValue(flow = flow(key))

    override fun float(key: String): KeyValue<Float?> =
        MemoryKeyValue(flow = flow(key))

    override fun int(key: String): KeyValue<Int?> =
        MemoryKeyValue(flow = flow(key))

    override fun long(key: String): KeyValue<Long?> =
        MemoryKeyValue(flow = flow(key))

    override fun string(key: String): KeyValue<String?> =
        MemoryKeyValue(flow = flow(key))

    override fun <T : Enum<T>> enum(
        key: String,
        entries: EnumEntries<T>,
        default: T
    ): KeyValue<T> = MemoryKeyValue<T>(flow = flow(key)).required { default }

    override fun <T : Any> model(
        key: String,
        fromJson: (String) -> T,
        toJson: (T) -> String
    ): KeyValue<T?> = MemoryKeyValue(flow = flow(key))

    private fun <T> flow(key: String): MutableStateFlow<T?> =
        database.getOrPut(key) { MutableStateFlow<T?>(null) } as MutableStateFlow<T?>

    class MemoryKeyValue<T>(
        private val flow: MutableStateFlow<T?>
    ) : KeyValue<T?>() {
        override var lastValue: T?
            get() = flow.value
            set(value) = set(value, scope)

        override fun get(): Flow<T?> = flow.asStateFlow()

        override fun set(value: T?, scope: CoroutineScope?) {
            flow.value = value
        }
    }
}
