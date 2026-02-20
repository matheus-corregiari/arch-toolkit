package br.com.arch.toolkit.storage.util

import kotlin.time.Duration

/**
 * In-memory cache entry that expires after [duration].
 *
 * The value is returned only when both keys (`storageName` and `name`) match the
 * last inserted entry and the configured threshold has not elapsed.
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
