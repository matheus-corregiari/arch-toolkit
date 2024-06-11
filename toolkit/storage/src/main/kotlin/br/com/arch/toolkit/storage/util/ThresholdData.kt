package br.com.arch.toolkit.storage.util

import kotlin.time.Duration

/**
 * The provided code defines a generic class named ThresholdData which appears to be designed for storing data with an expiration mechanism. Let's break down its functionality step by step.
 * <br>
 * ## Class Definition:
 * Generic Type: The class takes a generic type parameter T, allowing it to store data of any type.
 * Constructor: It has a constructor that accepts a Duration object, specifying the expiration time for the stored data.
 * <br>
 * ## Private Properties:
 * - storageName (String, nullable)
 *      - Likely used to identify a storage location or context associated with the data.
 * - data (T, nullable)
 *      - Holds the actual data of the generic type T.
 * - name (String, nullable)
 *      - Possibly a specific identifier or key for the data within the storage.
 * - timestamp (Long, nullable)
 *      - Stores the time (in milliseconds) when the data was last set.
 * <br>
 * ## Methods
 * - isExpired() Method:
 *      - Checks if the stored data has expired based on the following conditions:
 *      - All required properties (storageName, data, name, timestamp) must be non-null.
 *      - The time elapsed since the last timestamp must exceed the duration specified in the constructor.
 * - get() Method:
 *      - Takes storageName and name as parameters to retrieve the stored data.
 *      - Clears the stored data and returns null if any of the following conditions are met:
 *      - The data is expired (isExpired() returns true).
 *      - The provided storageName doesn't match the stored one.
 *      - The provided name doesn't match the stored one.
 * Otherwise, it returns the stored data. If data is null, it calls clear() and returns null.
 * - set() Method:
 *      - Takes storageName, name, and data as parameters to store new data.
 *      - Clears any existing data first.
 *      - If the provided data is not null, it updates the properties with the new values and sets the timestamp to the current time.
 * - clear() Method:
 *      - Resets all properties to null, effectively clearing the stored data and its associated information.
 * - ifNull() Extension Function:
 *      - A private extension function on nullable types.
 *      - If the receiver object is null, it executes the provided block of code.
 *      - Returns the receiver object itself (for chaining).
 * <br>
 * ## Purpose and Potential Use Cases:
 * This ThresholdData class seems useful for scenarios where you need to temporarily store data with an expiration time, such as:
 * Caching data fetched from a network or database.
 * Storing user input or preferences with a timeout.
 * Implementing rate limiting or throttling mechanisms.
 * <br>
 * ## Example Usage:
 * ```kotlin
 * val dataStore = ThresholdData<String>(Duration.ofMinutes(5))
 *
 * // Store data
 * dataStore.set("user_preferences", "theme", "dark")
 *
 * // Retrieve data
 * val theme = dataStore.get("user_preferences", "theme")
 *
 * // Check if data is expired
 * val isExpired = dataStore.isExpired()
 * ```
 */
class ThresholdData<T>(private val duration: Duration) {

    private var storageName: String? = null
    private var data: T? = null
    private var name: String? = null
    private var timestamp: Long? = null

    fun isExpired(): Boolean {
        this.storageName ?: return true
        this.data ?: return true
        this.name ?: return true
        val lastTimestamp = this.timestamp ?: return true
        val deltaTime = System.currentTimeMillis() - lastTimestamp

        return deltaTime > duration.inWholeMilliseconds
    }

    fun get(storageName: String, name: String): T? = when {
        isExpired() -> run {
            clear()
            null
        }
        storageName != this.storageName -> run {
            clear()
            null
        }
        name != this.name -> run {
            clear()
            null
        }
        else -> this.data.ifNull { clear() }
    }

    fun set(storageName: String, name: String, data: T?) {
        clear()
        if (data == null) return

        this.storageName = storageName
        this.data = data
        this.name = name
        this.timestamp = System.currentTimeMillis()
    }

    fun clear() {
        this.storageName = null
        this.data = null
        this.name = null
        this.timestamp = null
    }

    private fun <T> T?.ifNull(block: () -> Unit) = apply { if (this == null) block() }
}
