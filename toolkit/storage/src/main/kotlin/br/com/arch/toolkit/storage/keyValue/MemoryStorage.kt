package br.com.arch.toolkit.storage.keyValue

import br.com.arch.toolkit.storage.StorageType
import kotlin.collections.set
import kotlin.reflect.KClass

/**
 * This code defines a class named MemoryStorage that implements the KeyValueStorage interface. Let's break down its functionality step by step.
 * Purpose
 * The MemoryStorage class provides an in-memory key-value store. It allows you to store and retrieve data using string keys, all within the application's memory. This is useful for temporary data that doesn't need to persist beyond the app's current session.
 * Implementation Details
 * Constructor: The constructor takes a name parameter and initializes the type property to StorageType.MEMORY. It also creates a private lock object for thread safety.
 * Storage Mechanism: A private mutable map (map) is used to store the key-value pairs.
 * <br>
 * ## Methods
 * - get()
 *      - The first get() method attempts to retrieve a value associated with the given key. It uses a type parameter T to allow for generic value retrieval. If the key exists, it returns the corresponding value cast to type T; otherwise, it returns null.
 *      - The second get() method provides a default value to return if the key is not found.
 * - set() Method:
 *      - This method stores a value associated with a given key.
 *      - It performs several validations before storing the value:
 *      - If the value is null, empty, or blank, it removes the key (if it exists).
 *      - If the key is blank or null, it also removes the key.
 *      - If both key and value are valid, it stores the key-value pair in the map.
 * - remove() Methods:
 *      - The first remove() method deletes the key-value pair associated with the given key.
 *      - The second remove() method deletes all key-value pairs where the key matches the provided regular expression (regex).
 * - clear() Method: This method clears all key-value pairs from the storage.
 * - contains() Method: This method checks if a key-value pair exists for the given key.
 * - size() Method: This method returns the number of key-value pairs in the storage.
 * - keys() Method: This method returns a list of all keys present in the storage.
 * <br>
 * ## Thread Safety
 * The synchronized blocks around critical sections (like get(), set(), remove(), and clear()) ensure that multiple threads can safely access the MemoryStorage instance without data corruption.
 * <br>
 * ## Example Usage
 * ```kotlin
 * val storage = MemoryStorage("my_storage")
 * storage.set("user_name", "John Doe")
 * val userName = storage.get<String>("user_name")
 * ```
 * <br>
 */
class MemoryStorage internal constructor(override val name: String) : KeyValueStorage {

    override val type: StorageType = StorageType.MEMORY
    private val lock = Object()
    private val map: MutableMap<String, Any> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: String): T? = synchronized(lock) {
        when {
            contains(key) -> map[key] as? T?
            else -> null
        }
    }

    override fun <T : Any> get(key: String, kClass: KClass<T>): T? = get(key)

    override fun <T : Any> set(key: String, value: T?) = when {
        /* Validate Value */
        value == null -> remove(key)
        value == null.toString() -> remove(key)
        (value as? String).isNullOrBlank() -> remove(key)

        /* Validate Key */
        key.isBlank() -> remove(key)
        key == null.toString() -> remove(key)

        /* If reaches here, the Key and the Value are good to go! */
        else -> synchronized(lock) { map[key] = value }
    }

    override fun <T : Any> set(key: String, value: T?, kClass: KClass<T>) = set(key, value)

    override fun remove(key: String) = synchronized(lock) {
        if (contains(key)) map.remove(key)
    }

    override fun clear() = synchronized(lock) { map.clear() }

    override fun contains(key: String): Boolean = map.containsKey(key)

    override fun size(): Int = map.count()

    override fun keys(): List<String> = map.keys.toList()
}
