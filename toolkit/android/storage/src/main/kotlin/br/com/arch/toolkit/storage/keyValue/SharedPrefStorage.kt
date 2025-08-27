@file:Suppress("DEPRECATION")

package br.com.arch.toolkit.storage.keyValue

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.storage.StorageType
import br.com.arch.toolkit.storage.util.edit
import br.com.arch.toolkit.storage.util.get
import br.com.arch.toolkit.storage.util.set

/**
 * This code defines a class called SharedPrefStorage that implements an interface called KeyValueStorage.
 * This class is designed to store key-value pairs using Android's SharedPreferences system.
 * Let's break down the code step by step:
 *
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
 *      - Stores a key-value pair. It performs validation on both the key and value before storing them.
 *      If the value is null or empty, it removes the key.
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
 * The synchronized(lock) blocks in several methods ensure that only one thread can access the
 * SharedPreferences at a time, preventing potential race conditions and data inconsistencies.
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
sealed class SharedPrefStorage(
    override val type: StorageType,
    private val sharedPref: SharedPreferences
) : KeyValueStorage {

    private val lock = Object()
    private val mirageStorage: KeyValueStorage by lazy { MemoryStorage(name) }

    init {
        sharedPref.registerOnSharedPreferenceChangeListener { _, key -> log("Key $key changed") }
    }

    class Regular(context: Context, override val name: String) : SharedPrefStorage(
        StorageType.SHARED_PREF,
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    )

    class Encrypted(context: Context, override val name: String) : SharedPrefStorage(
        StorageType.ENCRYPTED_SHARED_PREF,
        EncryptedSharedPreferences.create(
            name,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    )

    override fun <T : Any> get(key: String): T? = synchronized(lock) {
        mirageStorage[key] ?: runCatching { sharedPref.get<T>(key) }
            .onFailure { log(it, "Failure getting key $key") }
            .onSuccess {
                log("Key $key retrieved")
                mirageStorage[key] = it
            }.getOrThrow()
    }

    override fun <T : Any> set(key: String, value: T?) = when {
        /* Validate Value */
        value == null -> remove(key)
        value == null.toString() -> remove(key)
        (value as? String)?.isBlank() == true -> remove(key)

        /* Validate Key */
        key.isBlank() -> remove(key)
        key == null.toString() -> remove(key)

        /* If reaches here, the Key and the Value are good to go! */
        else -> synchronized(lock) {
            runCatching {
                sharedPref[key] = value
            }.onFailure {
                log(it, "Failure setting key $key to $value")
            }.onSuccess {
                log("Key $key set to $value")
                mirageStorage[key] = value
            }.getOrThrow()
        }
    }

    override fun remove(key: String) = synchronized(lock) {
        runCatching {
            if (contains(key)) {
                sharedPref.edit { remove(key) }
                log("Key $key removed")
            }
        }.onFailure { log(it, "Failure removing key $key") }
            .onSuccess { mirageStorage.remove(key) }
            .getOrThrow()
    }

    override fun clear() = synchronized(lock) {
        runCatching {
            sharedPref.edit { clear() }
        }.onFailure { log(it, "Failure clearing storage $name") }
            .onSuccess {
                log("Storage $name cleared")
                mirageStorage.clear()
            }.getOrThrow()
    }

    override fun contains(key: String): Boolean = run { sharedPref.contains(key) }

    override fun size(): Int = sharedPref.all.count()

    override fun keys(): List<String> = sharedPref.all.keys.toList()

    private fun log(error: Throwable, message: String) {
        Lumber.tag("[Storage $name]").error(error, message)
    }

    private fun log(message: String) {
        Lumber.tag("[Storage $name]").info(message)
    }
}
