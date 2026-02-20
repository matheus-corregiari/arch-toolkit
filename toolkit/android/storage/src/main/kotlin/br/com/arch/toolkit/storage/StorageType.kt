package br.com.arch.toolkit.storage

/** Supported key-value storage backends. */
enum class StorageType {

    /** Volatile in-memory storage; values are lost when process dies. */
    MEMORY,

    /** Android [android.content.SharedPreferences] persisted in app private storage. */
    SHARED_PREF,

    /** [android.content.SharedPreferences] protected with AndroidX Security encryption. */
    ENCRYPTED_SHARED_PREF
}
