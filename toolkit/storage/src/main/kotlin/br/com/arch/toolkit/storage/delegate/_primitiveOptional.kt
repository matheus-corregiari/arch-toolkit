package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.KeyValueStorage
import br.com.arch.toolkit.storage.StorageCreator
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration

/**
 * This delegate simplifies the process of persisting optional primitive values in a KeyValueStorage with an optional caching mechanism to improve performance. It abstracts the storage and retrieval logic, allowing developers to focus on using the property directly.
 * ## Example Usage
 * ```kotlin
 * var myOptionalInt by keyValueStorage<Int?>("my_int_key")
 *
 * // Settinga value
 * myOptionalInt = 42
 *
 * // Retrieving the value
 * val retrievedValue = myOptionalInt // retrievedValue will be 42
 * ```
 */
fun <T> keyValueStorage(
    name: String,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    threshold: Duration = StorageCreator.defaultThreshold
) = keyValueStorage<T>(
    name = { name },
    storage = storage,
    threshold = threshold
)

/**
 * This delegate simplifies the process of persisting optional primitive values in a KeyValueStorage with an optional caching mechanism to improve performance. It abstracts the storage and retrieval logic, allowing developers to focus on using the property directly.
 * ## Example Usage
 * ```kotlin
 * var myOptionalInt by keyValueStorage<Int?>(name = { "my_int_key" })
 *
 * // Settinga value
 * myOptionalInt = 42
 *
 * // Retrieving the value
 * val retrievedValue = myOptionalInt // retrievedValue will be 42
 * ```
 */
fun <T> keyValueStorage(
    name: () -> String,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    threshold: Duration = StorageCreator.defaultThreshold
) = OptionalPrimitiveStorageDelegate<T>(
    name = name,
    storage = storage,
    threshold = threshold
)

/**
 * The provided code defines a Kotlin property delegate named OptionalPrimitiveStorageDelegate that facilitates storing and retrieving optional primitive values using a KeyValueStorage.
 */
class OptionalPrimitiveStorageDelegate<T> internal constructor(
    name: () -> String,
    storage: () -> KeyValueStorage,
    threshold: Duration,
) : BaseStorageDelegate<T>(
    name = name,
    default = null,
    storage = storage,
    threshold = threshold
), ReadWriteProperty<Any?, T?> {

    /**
     * - Retrieves the value associated with the given property.
     * - If either the name or storage is unavailable, it returns null.
     * - It first checks the in-memory cache (lastAccess) based on the threshold. If found, it returns the cached value.
     * - Otherwise, it fetches the value from the KeyValueStorage, caches it, and returns it.
     */
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val name = name() ?: return null
        val storage = storage() ?: return null

        return lastAccess.get(storage.name, name)?.also {
            log("Get key value storage from threshold: $name -> $it")
        } ?: storage.get<T>(name)?.also {
            log("Get key value storage: $name -> $it")
            lastAccess.set(storage.name, name, it)
        }
    }

    /**
     * - Sets the value for the given property.
     * - If either the name or storage is unavailable, it clears the cache and returns.
     * - If the new value is null, it removes the entry from the KeyValueStorage and clears the cache.
     * - Otherwise, it stores the value in the KeyValueStorage, updates the cache, and logs the operation.
     */
    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        val name = name() ?: run { lastAccess.clear();return }
        val storage = storage() ?: run { lastAccess.clear();return }

        when {
            value == null -> {
                storage.remove(name)
                lastAccess.clear()
                log("Removed key value storage: $name")
            }

            else -> {
                storage.set<T>(name, value)
                lastAccess.set(storage.name, name, value)
                log("Set key value storage: $name -> $value")
            }
        }
    }
}
