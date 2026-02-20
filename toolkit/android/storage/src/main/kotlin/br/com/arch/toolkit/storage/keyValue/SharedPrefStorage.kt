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
 * Base implementation of [KeyValueStorage] backed by [SharedPreferences].
 *
 * Values are mirrored in an internal [MemoryStorage] to reduce repeated disk
 * reads and to provide a lightweight cache layer.
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
