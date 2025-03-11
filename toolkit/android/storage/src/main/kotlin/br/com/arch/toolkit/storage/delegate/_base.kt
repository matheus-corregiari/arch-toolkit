package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.storage.keyValue.KeyValueStorage

internal fun (() -> String).get() = runCatching { invoke().takeIf { it.isNotBlank() } }
    .onFailure {
        Lumber.tag("Storage Delegate").error(it, "[Storage] Failed to get name for key value storage")
    }.getOrNull()

internal fun (() -> KeyValueStorage).get() = runCatching { invoke() }.onFailure {
    Lumber.tag("Storage Delegate").error(it, "[Storage] Failed to get storage for key value storage")
}.getOrNull()

internal fun <T : Any> (() -> T).get() = runCatching { invoke() }.onFailure {
    Lumber.tag("Storage Delegate").error(it, "[Storage] Failed to get default for key value storage")
}.getOrThrow()

sealed class StorageDelegate<T : Any> {

    protected fun Throwable.log(message: String) {
        Lumber.tag("Storage Delegate").error(this, message)
    }

    protected fun log(message: String) {
        Lumber.tag("Storage Delegate").info(message)
    }
}
