package br.com.arch.toolkit.storage.keyValue

import br.com.arch.toolkit.storage.StorageType
import kotlin.collections.set

/**
 * Thread-safe in-memory [KeyValueStorage] implementation.
 *
 * Empty or invalid keys/values are treated as removals to keep parity with
 * the SharedPreferences-based implementations.
 */
class MemoryStorage internal constructor(override val name: String) : KeyValueStorage {

    override val type: StorageType = StorageType.MEMORY
    private val lock = Object()
    private val map: MutableMap<String, Any> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: String): T? = synchronized(lock) { map[key] as? T? }

    override fun <T : Any> set(key: String, value: T?) = when {
        /* Validate Value */
        value == null -> remove(key)
        value == null.toString() -> remove(key)
        (value as? String)?.isBlank() == true -> remove(key)

        /* Validate Key */
        key.isBlank() -> remove(key)
        key == null.toString() -> remove(key)

        /* If reaches here, the Key and the Value are good to go! */
        else -> synchronized(lock) { map[key] = value }
    }

    override fun remove(key: String) = synchronized(lock) { if (contains(key)) map.remove(key) }

    override fun clear() = synchronized(lock) { map.clear() }

    override fun contains(key: String): Boolean = map.containsKey(key)

    override fun size(): Int = map.count()

    override fun keys(): List<String> = map.keys.toList()
}
