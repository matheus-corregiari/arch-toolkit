package br.com.arch.toolkit.storage.core

import kotlinx.serialization.json.Json

@StorageApi
abstract class StorageProvider(
    internal val json: Json = defaultJson
) {

    @StorageApi
    abstract fun boolean(key: String): KeyValue<Boolean?, Boolean?>

    @StorageApi
    abstract fun byteArray(key: String): KeyValue<ByteArray?, ByteArray?>

    @StorageApi
    abstract fun double(key: String): KeyValue<Double?, Double?>

    @StorageApi
    abstract fun float(key: String): KeyValue<Float?, Float?>

    @StorageApi
    abstract fun int(key: String): KeyValue<Int?, Int?>

    @StorageApi
    abstract fun long(key: String): KeyValue<Long?, Long?>

    @StorageApi
    abstract fun string(key: String): KeyValue<String?, String?>

    @StorageApi
    abstract fun <T : Enum<T>> enum(key: String): KeyValue<String, T>

    @StorageApi
    abstract fun <T : Any> model(key: String): KeyValue<String?, T?>

    companion object Defaults {
        val defaultJson: Json
            get() = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                prettyPrint = true
            }
    }
}
