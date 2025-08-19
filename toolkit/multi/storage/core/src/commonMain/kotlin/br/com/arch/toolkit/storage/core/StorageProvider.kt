package br.com.arch.toolkit.storage.core

import kotlinx.serialization.json.Json
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries

@StorageApi
@Suppress("TooManyFunctions")
abstract class StorageProvider {

    @StorageApi
    abstract fun boolean(key: String): KeyValue<Boolean?>

    @StorageApi
    abstract fun byteArray(key: String): KeyValue<ByteArray?>

    @StorageApi
    abstract fun double(key: String): KeyValue<Double?>

    @StorageApi
    abstract fun float(key: String): KeyValue<Float?>

    @StorageApi
    abstract fun int(key: String): KeyValue<Int?>

    @StorageApi
    abstract fun long(key: String): KeyValue<Long?>

    @StorageApi
    abstract fun string(key: String): KeyValue<String?>

    @StorageApi
    abstract fun <T : Enum<T>> enum(key: String, entries: EnumEntries<T>, default: T): KeyValue<T>

    @StorageApi
    abstract fun <T : Any> model(
        key: String,
        fromJson: (String) -> T,
        toJson: (T) -> String
    ): KeyValue<T?>

    @StorageApi
    inline fun <reified T : Any> model(
        key: String,
        json: Json = defaultJson
    ) = model<T>(key, json::decodeFromString, json::encodeToString)

    @StorageApi
    inline fun <reified T : Enum<T>> enum(
        key: String,
        default: T
    ) = enum(key, enumEntries<T>(), default)

    companion object Defaults {
        var defaultJson: Json
            private set

        init {
            defaultJson = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                prettyPrint = true
            }
        }

        fun json(json: Json) = apply { defaultJson = json }
    }
}
