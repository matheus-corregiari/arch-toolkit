package br.com.arch.toolkit.storage.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import br.com.arch.toolkit.storage.KeyValueStorage
import br.com.arch.toolkit.storage.StorageType
import br.com.arch.toolkit.storage.util.edit
import br.com.arch.toolkit.storage.util.set
import timber.log.Timber

/**
 * Encrypted Shared Preferences Storage
 * > The provided code defines a Kotlin class named EncryptedSharedPrefStorage that implements a key-value storage mechanism using Android's EncryptedSharedPreferences.
 *
 * ## Purpose
 * The primary purpose of this class is to provide a secure way to store key-value data on an Android device. It leverages the EncryptedSharedPreferences library to encrypt the data before it is persisted to disk, protecting sensitive information from unauthorized access.
 *
 * ## In Summary
 * The EncryptedSharedPrefStorage class provides a robust and secure solution for storing key-value data in an Android application. It utilizes encryption to protect sensitive information and offers a convenient API for managing the stored data.
 *
 * ## Synchronization
 * Notice the use of synchronized(lock) blocks in several methods. This ensures thread safety, preventing concurrent access and potential data corruption when multiple threads interact with the shared preferences.
 *
 * ## Storage Operations
 * > The class provides several methods for interacting with the encrypted storage:
 * - get(key: String): Retrieves the value associated with the given key, if it exists.
 * - get(key: String, default: T): Retrieves the value associated with the given key, returning the provided default value if the key is not found.
 * - set(key: String, value: T?): Stores the given value under the specified key. Handles null values and empty keys appropriately.
 * - remove(key: String): Removes the key-value pair associated with the given key.
 * - remove(regex: Regex): Removes all key-value pairs whose keys match the provided regular expression.
 * - clear(): Clears all key-value pairs from the storage.
 * - contains(key: String): Checks if the storage contains a value associated with the given key.
 * - size(): Returns the number of key-value pairs in the storage.
 * - keys(): Returns a list of all keys present in the storage.
 */
// TODO Make it work also with complex data (transforming in JSON to save it into the disk)
class EncryptedSharedPrefStorage internal constructor(context: Context, override val name: String) :
    KeyValueStorage {

    override val type: StorageType = StorageType.ENCRYPTED_SHARED_PREF
    private val lock = Object()

    private val sharedPref: SharedPreferences

    init {
        val keyScheme = EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
        val valueScheme = EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPref = EncryptedSharedPreferences.create(
            /* fileName = */ name,
            /* masterKeyAlias = */ masterKey,
            /* context = */ context,
            /* prefKeyEncryptionScheme = */ keyScheme,
            /* prefValueEncryptionScheme = */ valueScheme
        )
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
