package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.keyValue.KeyValueStorage
import timber.log.Timber

internal fun (() -> String).get() = runCatching { invoke().takeIf { it.isNotBlank() } }
    .onFailure {
        Timber.tag("Storage Delegate").e(it, "[Storage] Failed to get name for key value storage")
    }.getOrNull()

internal fun (() -> KeyValueStorage).get() = runCatching { invoke() }.onFailure {
    Timber.tag("Storage Delegate").e(it, "[Storage] Failed to get storage for key value storage")
}.getOrNull()

internal fun <T : Any> (() -> T).get() = runCatching { invoke() }.onFailure {
    Timber.tag("Storage Delegate").e(it, "[Storage] Failed to get default for key value storage")
}.getOrThrow()

sealed class StorageDelegate<T : Any> {

    protected fun Throwable.log(message: String) {
        Timber.tag("Storage Delegate").e(this, message)
    }

    protected fun log(message: String) {
        Timber.tag("Storage Delegate").i(message)
    }
}
