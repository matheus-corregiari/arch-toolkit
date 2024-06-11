@file:Suppress("ReturnCount", "Filename")

package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.StorageCreator
import br.com.arch.toolkit.storage.keyValue.KeyValueStorage
import br.com.arch.toolkit.storage.util.ThresholdData
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.time.Duration

inline fun <reified T : Any> keyValueStorage(
    name: String,
    crossinline storage: () -> KeyValueStorage = { StorageCreator.defaultStorage() },
    threshold: Duration = StorageCreator.defaultThreshold
) = keyValueStorage(
    name = { name },
    storage = { storage() },
    threshold = threshold,
    classToParse = T::class
)

fun <T : Any> keyValueStorage(
    name: String,
    classToParse: KClass<T>,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    threshold: Duration = StorageCreator.defaultThreshold
) = keyValueStorage(
    name = { name },
    storage = storage,
    threshold = threshold,
    classToParse = classToParse
)

fun <T : Any> keyValueStorage(
    name: () -> String,
    classToParse: KClass<T>,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    threshold: Duration = StorageCreator.defaultThreshold
) = OptionalStorageDelegate(
    name = name,
    storage = storage,
    threshold = threshold,
    classToParse = classToParse
)

class OptionalStorageDelegate<T : Any> internal constructor(
    name: () -> String,
    storage: () -> KeyValueStorage,
    threshold: Duration,
    private val classToParse: KClass<T>
) : BaseStorageDelegate<T>(
    name = name,
    default = null,
    storage = storage
) {

    private val lastAccess: ThresholdData<T> by lazy {
        val key = "threshold-${threshold.inWholeMilliseconds}-${name()}"
        StorageCreator.memory[key]
            ?: ThresholdData<T>(threshold).also { StorageCreator.memory[key] = it }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val name = name() ?: return null
        val storage = storage() ?: return null

        return lastAccess.get(storage.name, name)?.also {
            log("[Storage] Get key value storage from threshold: $name -> $it")
        } ?: storage.runCatching {
            get(name, classToParse)
        }.onSuccess {
            log("[Storage] Get key value storage: $name -> $it")
        }.onFailure {
            it.log("[Storage] Failed to get key value storage: $name")
        }.getOrNull()?.also { lastAccess.set(storage.name, name, it) }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        val name = name()
        val storage = storage()
        if (name == null || storage == null) {
            lastAccess.clear()
            return
        }

        when {
            value == null -> {
                storage.runCatching {
                    remove(name)
                }.onSuccess {
                    lastAccess.clear()
                    log("[Storage] Removed key value storage: $name")
                }.onFailure {
                    it.log("[Storage] Failed to remove key value storage: $name")
                }
            }

            else -> {
                storage.runCatching {
                    set(name, value, classToParse)
                }.onSuccess {
                    lastAccess.set(storage.name, name, value)
                    log("[Storage] Set key value storage: $name -> $value")
                }.onFailure {
                    it.log("[Storage] Failed to set key value storage: $name -> $value")
                }
            }
        }
    }
}
