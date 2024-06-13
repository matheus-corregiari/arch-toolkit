package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.keyValue.KeyValueStorage
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.time.Duration

data class NonOptionalStorageDelegate<T : Any> internal constructor(
    private val name: () -> String,
    private val storage: () -> KeyValueStorage,
    private val default: () -> T,
    private val threshold: Duration,
    private val classToParse: KClass<out T>
) : StorageDelegate<T>() {

    private var savedData: T? by keyValueStorage(classToParse, name)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = savedData
        ?: default.get().also {
            log("[Storage] Delivering default value for field '${property.name}': \n\t Value -> $it")
        }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        savedData = value
    }

    //region Storage Method modifiers
    fun storage(storage: KeyValueStorage) = storage { storage }
    fun storage(storage: () -> KeyValueStorage) = copy(storage = storage)
    //endregion

    //region Threshold Method modifiers
    fun threshold(threshold: Duration) = copy(threshold = threshold)
    //endregion
}
