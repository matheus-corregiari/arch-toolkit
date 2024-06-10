package br.com.arch.toolkit.storage

/**
 * This code defines an interface called KeyValueLocalStorage in Kotlin, which outlines a contract for storing and retrieving key-value pairs in a local storage system. Let's break down its components:
 * <br>
 * ## Properties
 * - type: A property of type StorageType (not shown here) indicating the underlying storage mechanism (e.g., SharedPreferences, DataStore, etc.).
 * - name: A property of a String representing the name or identifier of this storage instance.
 * <br>
 * ## Methods
 * - get(key: String): Retrieves the value associated with the given key. Returns null if the key doesn't exist.
 * - get(key: String, default: T): Retrieves the value associated with the given key. Returns the provided default value if the key doesn't exist.
 * - set(key: String, value: T?): Stores the given value under the specified key. The value can be nullable.
 * - remove(key: String): Removes the key-value pair associated with the given key.
 * - remove(regex: Regex): Removes all key-value pairs whose keys match the provided regular expression regex.
 * - clear(): Removes all key-value pairs from the storage.
 * - contains(key: String): Returns true if the storage contains a value for the given key, and false otherwise.
 * - size(): Returns the number of key-value pairs currently stored.
 * - keys(): Returns a Set containing all the keys present in the storage.
 * <br>
 * ## Purpose
 * This interface provides a standardized way to interact with different local storage solutions in your Android application. By implementing this interface, you can create concrete storage classes (e.g., SharedPreferencesStorage, DataStoreStorage) that handle the actual storage logic while adhering to a common API.
 * <br>
 * This abstraction allows you to easily switch between storage implementations or use multiple storage mechanisms within your app without changing the code that interacts with the storage.
 */
interface KeyValueStorage {

    val type: StorageType

    val name: String

    fun <T> get(key: String): T?

    fun <T> get(key: String, default: T): T

    fun <T> set(key: String, value: T?)

    fun remove(key: String)

    fun remove(regex: Regex)

    fun clear()

    fun contains(key: String): Boolean

    fun size(): Int

    fun keys(): List<String>

}
