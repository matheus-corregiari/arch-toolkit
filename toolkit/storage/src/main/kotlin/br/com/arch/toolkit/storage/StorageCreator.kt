package br.com.arch.toolkit.storage

import android.content.Context
import br.com.arch.toolkit.storage.impl.EncryptedSharedPrefStorage
import br.com.arch.toolkit.storage.impl.MemoryStorage
import br.com.arch.toolkit.storage.impl.SharedPrefStorage
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * The provided code defines an object named StorageCreator in Kotlin, which serves as a factory and manager for different types of storage mechanisms.
 *
 * ## Usage
 * Before using the encryptedSharedPref or sharedPref properties, you must call the init() method and provide an Android Context. This initializes the underlying storage instances.
 * The createKeyValueStorage() methods provide a convenient way to create different types of storage instances based on your needs.
 *
 * ## Example
 * ```kotlin
 * // Initialize the storage creator
 * StorageCreator.init(context)
 *
 * // Access the encrypted shared preferences storage
 * val encryptedStorage = StorageCreator.encryptedSharedPref
 *
 * // Create a memory storage instance
 * val memoryStorage = StorageCreator.createKeyValueStorage("temp", StorageType.MEMORY)
 * ```
 */
object StorageCreator {

    /**
     * A nullable property to hold an instance of EncryptedSharedPrefStorage. It's initialized to null and later assigned a value within the init() method.
     */
    private var _encryptedSharedPref: EncryptedSharedPrefStorage? = null

    /**
     * Similar to _encryptedSharedPref, it stores an instance of SharedPrefStorage.
     */
    private var _sharedPref: SharedPrefStorage? = null

    /**
     * A property representing a time duration, initialized to 300 milliseconds. It can be modified using the setDefaultThreshold() method.
     */
    internal var defaultThreshold: Duration = 300.milliseconds
        private set

    /**
     * A property representing a function that returns an instance of KeyValueStorage.
     * It's initialized to a function that returns an instance of encryptedSharedPref.
     */
    internal var defaultStorage: () -> KeyValueStorage = { encryptedSharedPref }
        private set

    /**
     * An instance of MemoryStorage with the name "default", likely used for temporary in-memory data storage.
     */
    val memory = MemoryStorage("default")

    /**
     * A computed property that provides access to the _encryptedSharedPref instance. It throws an exception if accessed before initialization.
     */
    val encryptedSharedPref: EncryptedSharedPrefStorage
        get() = requireNotNull(_encryptedSharedPref) {
            "Not initialized, Be aware to call init() before use"
        }

    /**
     * Similar to encryptedSharedPref, it provides access to the _sharedPref instance, ensuring it's initialized.
     */
    val sharedPref: EncryptedSharedPrefStorage
        get() = requireNotNull(_encryptedSharedPref) {
            "Not initialized, Be aware to call init() before use"
        }

    /**
     * This function initializes the _encryptedSharedPref and _sharedPref properties using the provided Android Context. It's crucial to call this method before accessing these storage instances.
     */
    fun init(context: Context) {
        _encryptedSharedPref = EncryptedSharedPrefStorage(context, "default")
        _sharedPref = SharedPrefStorage(context, "default")
    }

    /**
     * A factory method that creates and returns a storage instance based on the specified StorageType.
     * It supports only MemoryStorage
     */
    fun createKeyValueStorage(name: String, type: StorageType) =
        when (type) {
            StorageType.MEMORY -> MemoryStorage(name)
            StorageType.ENCRYPTED_SHARED_PREF -> error("To create this type of storage, you must provide a context")
            StorageType.SHARED_PREF -> error("To create this type of storage, you must provide a context")
        }

    /**
     * A factory method that creates and returns a storage instance based on the specified StorageType.
     * It supports MemoryStorage, EncryptedSharedPrefStorage, and SharedPrefStorage.
     */
    fun createKeyValueStorage(context: Context, name: String, type: StorageType) =
        when (type) {
            StorageType.MEMORY -> MemoryStorage(name)
            StorageType.ENCRYPTED_SHARED_PREF -> EncryptedSharedPrefStorage(context, name)
            StorageType.SHARED_PREF -> SharedPrefStorage(context, name)
        }

    /**
     * Allows modification of the defaultThreshold property.
     */
    fun setDefaultThreshold(threshold: Duration) {
        defaultThreshold = threshold
    }

    /**
     * Allows modification of the defaultStorage property.
     */
    fun setDefaultStorage(storage: () -> KeyValueStorage) {
        defaultStorage = storage
    }
}
