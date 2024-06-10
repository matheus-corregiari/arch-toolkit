package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.KeyValueStorage
import br.com.arch.toolkit.storage.util.ThresholdData
import timber.log.Timber
import kotlin.time.Duration


open class BaseStorageDelegate<T> internal constructor(
    private val name: () -> String,
    private val default: (() -> T)?,
    private val storage: () -> KeyValueStorage,
    threshold: Duration,
) {

    protected val lastAccess = ThresholdData<T>(threshold)

    protected fun name() = name.runCatching { invoke().takeIf { it.isNotBlank() } }
        .onFailure { it.log("[Storage] Failed to get name for key value storage") }
        .getOrNull()

    protected fun storage() = storage.runCatching { invoke() }
        .onFailure { it.log("[Storage] Failed to get storage for key value storage") }
        .getOrNull()

    protected fun default() = default.runCatching { requireNotNull(this).invoke() }
        .onFailure { it.log("[Storage] Failed to get default value for ${name()}") }
        .getOrThrow()

    private fun Throwable.log(message: String) {
        Timber.tag("Storage Delegate").e(this, message)
    }

    protected fun log(message: String) {
        Timber.tag("Storage Delegate").i(message)
    }
}
