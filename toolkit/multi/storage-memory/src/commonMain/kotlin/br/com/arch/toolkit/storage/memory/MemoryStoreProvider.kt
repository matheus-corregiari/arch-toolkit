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

/**
 * In-memory [StorageProvider] implementation.
 *
 * [MemoryStoreProvider] keeps all values inside a [MutableMap] backed by [MutableStateFlow].
 * It is ideal for **testing, prototyping, or ephemeral storage** where persistence
 * is not required. Once the process is killed, all data is lost.
 *
 * ---
 *
 * ### Behavior
 * - Each key is mapped to a [MutableStateFlow].
 * - Values are reactive: updates are immediately emitted to collectors.
 * - No persistence: values live only in memory.
 * - API-compatible with other [StorageProvider] implementations like DataStore.
 *
 * ---
 *
 * ### Example: Using Memory storage
 * ```kotlin
 * val memory = MemoryStoreProvider(mutableMapOf())
 *
 * val flag = memory.boolean("feature_enabled")
 * val counter = memory.int("counter").required { 0 }
 *
 * // Reactive
 * scope.launch {
 *     flag.get().collect { println("Feature enabled? $it") }
 * }
 *
 * // Update
 * flag.set(true)
 * counter.set(counter.instant() + 1)
 *
 * println("Counter = ${counter.instant()}")
 * ```
 *
 * @property database Internal map holding [MutableStateFlow]s per key.
 *
 * @see StorageProvider
 */
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

    /**
     * In-memory [KeyValue] backed by a [MutableStateFlow].
     *
     * This class provides reactive and synchronous access to values stored in memory.
     * It is the building block for all primitives, enums, and model keys returned
     * by [MemoryStoreProvider].
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * val memory = MemoryStoreProvider(mutableMapOf())
     * val name: KeyValue<String?> = memory.string("user_name")
     *
     * name.set("Alice")
     * println("Hello, ${name.instant()}")
     * ```
     *
     * @param T The value type held in memory.
     * @property flow The [MutableStateFlow] used to store and emit values.
     */
    class MemoryKeyValue<T>(
        private val flow: MutableStateFlow<T?>
    ) : KeyValue<T?>() {
        override var lastValue: T?
            get() = flow.value
            set(value) = set(value, scope)

        override fun get(): Flow<T?> = flow.asStateFlow()

        override fun set(value: T?, scope: CoroutineScope) {
            flow.value = value
        }
    }
}
