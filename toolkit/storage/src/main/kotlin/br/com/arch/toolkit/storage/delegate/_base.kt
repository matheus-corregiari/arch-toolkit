package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.keyValue.KeyValueStorage
import timber.log.Timber

sealed class BaseStorageDelegate<T : Any>(
    private val name: () -> String,
    private val default: (() -> T)?,
    private val storage: () -> KeyValueStorage
) {

    protected fun name() = name.runCatching { invoke().takeIf { it.isNotBlank() } }
        .onFailure { it.log("[Storage] Failed to get name for key value storage") }
        .getOrNull()

    protected fun storage() = storage.runCatching { invoke() }
        .onFailure { it.log("[Storage] Failed to get storage for key value storage") }
        .getOrNull()

    protected fun default() = default.runCatching { requireNotNull(this).invoke() }
        .onFailure { it.log("[Storage] Failed to get default value for ${name()}") }
        .getOrThrow()

    protected fun Throwable.log(message: String) {
        Timber.tag("Storage Delegate").e(this, message)
    }

    protected fun log(message: String) {
        Timber.tag("Storage Delegate").i(message)
    }
}
