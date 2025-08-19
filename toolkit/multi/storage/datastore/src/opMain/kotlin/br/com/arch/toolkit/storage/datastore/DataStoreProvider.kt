package br.com.arch.toolkit.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.arch.toolkit.storage.core.KeyValue
import br.com.arch.toolkit.storage.core.StorageProvider
import kotlin.enums.EnumEntries

class DataStoreProvider(private val store: DataStore<Preferences>) : StorageProvider() {

    override fun boolean(key: String): KeyValue<Boolean?> =
        PrimitiveDataStoreKV.BooleanKV(key = key, store = store)

    override fun byteArray(key: String): KeyValue<ByteArray?> =
        PrimitiveDataStoreKV.ByteArrayKV(key = key, store = store)

    override fun double(key: String): KeyValue<Double?> =
        PrimitiveDataStoreKV.DoubleKV(key = key, store = store)

    override fun float(key: String): KeyValue<Float?> =
        PrimitiveDataStoreKV.FloatKV(key = key, store = store)

    override fun int(key: String): KeyValue<Int?> =
        PrimitiveDataStoreKV.IntKV(key = key, store = store)

    override fun long(key: String): KeyValue<Long?> =
        PrimitiveDataStoreKV.LongKV(key = key, store = store)

    override fun string(key: String): KeyValue<String?> =
        PrimitiveDataStoreKV.StringKV(key = key, store = store)

    override fun <T : Enum<T>> enum(
        key: String,
        entries: EnumEntries<T>,
        default: T
    ): KeyValue<T> = EnumDataStoreKV(key, store, entries, default)

    override fun <T : Any> model(
        key: String,
        fromJson: (String) -> T,
        toJson: (T) -> String
    ): KeyValue<T?> = ModelDataStoreKV(key, store, fromJson, toJson)
}
