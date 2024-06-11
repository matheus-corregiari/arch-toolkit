@file:Suppress("unused", "Filename", "LongParameterList")

package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.StorageCreator
import br.com.arch.toolkit.storage.keyValue.KeyValueStorage
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.time.Duration

inline fun <reified T : Any> keyValueStorage(
    name: String,
    default: T,
    crossinline storage: () -> KeyValueStorage = { StorageCreator.defaultStorage() },
    threshold: Duration = StorageCreator.defaultThreshold
) = keyValueStorage(
    name = { name },
    default = { default },
    classToParse = T::class,
    storage = { storage() },
    threshold = threshold
)

fun <T : Any> keyValueStorage(
    name: String,
    default: T,
    classToParse: KClass<T>,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    threshold: Duration = StorageCreator.defaultThreshold
) = keyValueStorage(
    name = { name },
    default = { default },
    classToParse = classToParse,
    storage = storage,
    threshold = threshold
)

fun <T : Any> keyValueStorage(
    name: String,
    default: () -> T,
    classToParse: KClass<T>,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    threshold: Duration = StorageCreator.defaultThreshold
) = keyValueStorage(
    name = { name },
    classToParse = classToParse,
    default = default,
    storage = storage,
    threshold = threshold
)

fun <T : Any> keyValueStorage(
    name: () -> String,
    default: () -> T,
    classToParse: KClass<T>,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    threshold: Duration = StorageCreator.defaultThreshold
) = NonOptionalStorageDelegate(
    name = name,
    default = default,
    threshold = threshold,
    storage = storage,
    classToParse = classToParse,
)

class NonOptionalStorageDelegate<T : Any> internal constructor(
    name: () -> String,
    default: () -> T,
    storage: () -> KeyValueStorage,
    threshold: Duration,
    classToParse: KClass<T>
) : BaseStorageDelegate<T>(
    name = name,
    default = default,
    storage = storage
) {

    private var savedData: T? by keyValueStorage(
        name = name,
        storage = storage,
        classToParse = classToParse,
        threshold = threshold
    )

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        savedData ?: default().also {
            log("[Storage] Delivering default value for field '${property.name}': \n\t- ${name()} -> $it")
        }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        savedData = value
    }
}
