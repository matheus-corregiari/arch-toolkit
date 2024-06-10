package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.KeyValueStorage
import br.com.arch.toolkit.storage.StorageCreator
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration

fun <T : Any> keyValueStorage(
    name: String,
    default: T,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    duration: Duration = StorageCreator.defaultThreshold
) = keyValueStorage(
    name = { name },
    default = { default },
    storage = storage,
    duration = duration
)

fun <T : Any> keyValueStorage(
    name: String,
    default: () -> T,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    duration: Duration = StorageCreator.defaultThreshold
) = keyValueStorage(
    name = { name },
    default = default,
    storage = storage,
    duration = duration
)

fun <T : Any> keyValueStorage(
    name: () -> String,
    default: () -> T,
    storage: () -> KeyValueStorage = StorageCreator.defaultStorage,
    duration: Duration = StorageCreator.defaultThreshold
) = PrimitiveStorageDelegate(
    name = name,
    default = default,
    threshold = duration,
    storage = storage
)

class PrimitiveStorageDelegate<T> internal constructor(
    name: () -> String,
    default: () -> T,
    storage: () -> KeyValueStorage,
    threshold: Duration,
) : BaseStorageDelegate<T>(
    name = name,
    default = default,
    storage = storage,
    threshold = threshold
), ReadWriteProperty<Any?, T> {

    private var savedData: T? by keyValueStorage(
        name = name,
        storage = storage,
        threshold = threshold
    )

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = savedData ?: default().also {
        log("Delivering default value for ${property.name}")
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        savedData = value
    }
}
