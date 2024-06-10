package br.com.arch.toolkit.storage

/**
 * This Kotlin code defines an enum class named StorageType.
 * In this case, StorageType represents different ways to store data in an Android app
 */
enum class StorageType {

    /**
     * This likely refers to storing data in the device's RAM, which is temporary and will be lost when the app closes.
     */
    MEMORY,

    /**
     * This likely refers to storing data in the device's RAM, which is temporary and will be lost when the app closes.
     */
    SHARED_PREF,

    /**
     * This suggests a more secure way of using SharedPreferences, likely employing encryption to protect the stored data.
     */
    ENCRYPTED_SHARED_PREF;
}
