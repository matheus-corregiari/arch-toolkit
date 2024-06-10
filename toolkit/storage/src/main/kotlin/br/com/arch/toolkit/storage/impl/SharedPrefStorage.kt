package br.com.arch.toolkit.storage.impl

import android.content.Context
import android.content.SharedPreferences
import br.com.arch.toolkit.storage.KeyValueStorage
import br.com.arch.toolkit.storage.StorageType
import br.com.arch.toolkit.storage.util.edit
import br.com.arch.toolkit.storage.util.set
import timber.log.Timber

/**
 * This code defines a class called SharedPrefStorage that implements an interface called KeyValueStorage. This class is designed to store key-value pairs using Android's SharedPreferences system. Let's break down the code step by step:
 * <br>
 * ## Constructor and Properties
 * - internal constructor(context: Context, override val name: String)
 *      - The constructor takes a Context (providing access to Android system resources) and a name for the SharedPreferences file.
 * - override val type: StorageType = StorageType.SHARED_PREF
 *      - Indicates that this storage uses SharedPreferences.
 * - private val lock = Object()
 *      - A lock object used for synchronization to prevent concurrent access issues.
 * - private val sharedPref: SharedPreferences
 *      - An instance of SharedPreferences obtained using the provided context and name.
 * - The init block registers a listener to log changes to SharedPreferences.
 * <br>
 * ## Methods
 * - get<T>(key: String)
 *      - Retrieves a value associated with the given key, if it exists. It handles type casting and returns null if the key is not found.
 * - get<T>(key: String, default: T)
 *      - Similar to get(key), but returns the provided default value if the key is not found.
 * - set<T>(key: String, value: T?)
 *      - Stores a key-value pair. It performs validation on both the key and value before storing them. If the value is null or empty, it removes the key.
 * - remove(key: String)
 *      - Removes the key-value pair associated with the given key.
 * - remove(regex: Regex)
 *      - Removes all key-value pairs where the key matches the provided regular expression.
 * - clear()
 *      - Clears all key-value pairs from the SharedPreferences.
 * - contains(key: String)
 *      - Checks if a key exists in the SharedPreferences.
 * - size()
 *      - Returns the number of key-value pairs.
 * - keys()
 *      - Returns a list of all keys in the SharedPreferences.
 * <br>
 * ## Synchronization
 * The synchronized(lock) blocks in several methods ensure that only one thread can access the SharedPreferences at a time, preventing potential race conditions and data inconsistencies.
 * <br>
 * ## Example Usage
 * ```kotlin
 * val storage = SharedPrefStorage(context, "my_preferences")
 * storage.set("user_name", "John Doe")
 * val userName = storage.get<String>("user_name")
 * ```
 * <br>
 * ## Limitations of SharedPreferences
 * While SharedPreferences is a convenient way to store simple key-value data, it has limitations:
 * - It's not designed for large amounts of data.
 * - It doesn't support complex data structures directly.
 * - It can have performance issues if used excessively.
 * > For more complex data or larger datasets, consider using other storage options like Room database or DataStore.
 */
// TODO Make it work also with complex data (transforming in JSON to save it into the disk)
class SharedPrefStorage internal constructor(context: Context, override val name: String) :
    KeyValueStorage {

    override val type: StorageType = StorageType.SHARED_PREF
    private val lock = Object()

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    init {
        sharedPref.registerOnSharedPreferenceChangeListener { _, key ->
            Timber.tag("[Storage $name]").i("$key changed")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): T? = synchronized(lock) {
        when {
            contains(key) -> sharedPref.all[key] as? T?
            else -> null
        }
    }

    override fun <T> get(key: String, default: T): T = get<T>(key) ?: default

    override fun <T> set(key: String, value: T?) = when {
        /* Validate Value */
        value == null -> remove(key)
        value == null.toString() -> remove(key)
        (value as? String).isNullOrBlank() -> remove(key)

        /* Validate Key */
        key.isBlank() -> remove(key)
        key == null.toString() -> remove(key)

        /* If reaches here, the Key and the Value are good to go! */
        else -> synchronized(lock) { sharedPref[key] = value }
    }

    override fun remove(key: String) = synchronized(lock) {
        if (contains(key)) sharedPref.edit { remove(key) }
    }

    override fun remove(regex: Regex) = keys().filter { it.matches(regex) }.forEach { remove(it) }

    override fun clear() = synchronized(lock) { sharedPref.edit { clear() } }

    override fun contains(key: String): Boolean = sharedPref.contains(key)

    override fun size(): Int = sharedPref.all.count()

    override fun keys(): List<String> = sharedPref.all.keys.toList()
}
