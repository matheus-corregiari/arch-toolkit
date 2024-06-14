package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.Storage
import br.com.arch.toolkit.storage.keyValue.KeyValueStorage
import br.com.arch.toolkit.storage.util.ThresholdData
import br.com.arch.toolkit.storage.util.isPrimitiveForSharedPref
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.time.Duration

data class OptionalStorageDelegate<T : Any> internal constructor(
    private val name: () -> String,
    private val storage: () -> KeyValueStorage,
    private val threshold: Duration,
    private val classToParse: KClass<out T>
) : StorageDelegate<T>() {

    private val lastAccess: ThresholdData<T> by lazy {
        val key = "threshold-${threshold.inWholeMilliseconds}-${name.get()}"
        Storage.KeyValue.memory[key]
            ?: ThresholdData<T>(threshold).also { Storage.KeyValue.memory[key] = it }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val name = name.get() ?: return null
        val storage = storage.get() ?: return null

        return lastAccess.get(storage.name, name)?.also {
            log("[Storage] Get key value storage from threshold: $name -> $it")
        } ?: storage.runCatching {
            when {
                /* If it is Int, Long, Double, Float, String or Boolean */
                classToParse.isPrimitiveForSharedPref() -> get(name)

                /* If it is a Enum type */
                classToParse.java.isEnum -> {
                    val stringValue = get<String>(name) ?: return null
                    val enumValue = classToParse.java.enumConstants
                        ?.filterIsInstance(java.lang.Enum::class.java)
                        ?.find { it.name().equals(stringValue, true) }
                        ?: return null
                    if (classToParse.java.isInstance(enumValue)) {
                        enumValue as? T
                    } else {
                        null
                    }
                }

                /* Call custom parser to parse complex data into string and then, retrieve it */
                else -> {
                    val json = get<String>(name)?.takeIf { it.isNotBlank() } ?: return null
                    val parser = Storage.Settings.complexDataParser
                        ?: error("Please set a ComplexDataParser into Storage.Settings.setComplexDataParser")
                    parser.fromJson(json, classToParse)
                }
            }
        }.onSuccess {
            log("[Storage] Get key value storage: $name -> $it")
        }.onFailure {
            it.log("[Storage] Failed to get key value storage: $name")
        }.getOrNull()?.also { lastAccess.set(storage.name, name, it) }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        val name = name.get()
        val storage = storage.get()
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
                    when {
                        /* If it is Int, Long, Double, Float, String or Boolean */
                        classToParse.isPrimitiveForSharedPref() -> set(name, value)

                        /* If it is a Enum type */
                        classToParse.java.isEnum -> set(name, (value as Enum<*>).name)

                        /* Call custom parser to transform data into json to save it */
                        else -> {
                            val parser = Storage.Settings.complexDataParser
                                ?: error("Please set a ComplexDataParser into Storage.Settings.setComplexDataParser")
                            set(name, parser.toJson(value).takeIf { it.isNotBlank() })
                        }
                    }
                }.onSuccess {
                    lastAccess.set(storage.name, name, value)
                    log("[Storage] Set key value storage: $name -> $value")
                }.onFailure {
                    it.log("[Storage] Failed to set key value storage: $name -> $value")
                }
            }
        }
    }

    //region Storage Method modifiers
    fun storage(storage: KeyValueStorage) = storage { storage }
    fun storage(storage: () -> KeyValueStorage) = copy(storage = storage)
    //endregion

    //region Threshold Method modifiers
    fun threshold(threshold: Duration) = copy(threshold = threshold)
    //endregion

    //region Required Method modifiers.
    fun required(default: T) = required { default }
    fun required(default: () -> T) = NonOptionalStorageDelegate<T>(
        name = name,
        default = default,
        storage = storage,
        threshold = threshold,
        classToParse = classToParse
    )
    //endregion
}
