package br.com.arch.toolkit.storage.keyValue

import br.com.arch.toolkit.storage.StorageType
import kotlin.reflect.KClass

interface KeyValueStorage {

    val type: StorageType

    val name: String

    operator fun <T : Any> get(key: String): T?
    operator fun <T : Any> get(key: String, default: T): T = get(key) ?: default

    operator fun <T : Any> get(key: String, kClass: KClass<T>): T?
    operator fun <T : Any> get(key: String, kClass: KClass<T>, default: T): T =
        get(key, kClass) ?: default

    operator fun <T : Any> set(key: String, value: T?)
    operator fun <T : Any> set(key: String, value: T?, kClass: KClass<T>)

    fun remove(key: String)

    fun remove(regex: Regex) = keys().filter { it.matches(regex) }.forEach { remove(it) }

    fun clear()

    fun contains(key: String): Boolean

    fun size(): Int

    fun keys(): List<String>
}
