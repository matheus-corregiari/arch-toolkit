package br.com.arch.toolkit.storage.keyValue

import br.com.arch.toolkit.storage.StorageType

/** Contract for key-value persistence used by the storage delegates. */
interface KeyValueStorage {

    val type: StorageType

    val name: String

    operator fun <T : Any> get(key: String): T?
    operator fun <T : Any> get(key: String, default: T): T = get(key) ?: default

    operator fun <T : Any> set(key: String, value: T?)

    fun remove(key: String)
    fun remove(regex: Regex) = keys().filter { it.matches(regex) }.forEach { remove(it) }

    fun clear()

    fun contains(key: String): Boolean

    fun size(): Int

    fun keys(): List<String>
}
