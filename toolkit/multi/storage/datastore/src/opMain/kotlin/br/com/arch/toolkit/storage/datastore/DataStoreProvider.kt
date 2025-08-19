package br.com.arch.toolkit.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.arch.toolkit.storage.core.KeyValue
import br.com.arch.toolkit.storage.core.KeyValue.Companion.map
import br.com.arch.toolkit.storage.core.KeyValue.Companion.required
import br.com.arch.toolkit.storage.core.StorageProvider
import kotlin.enums.EnumEntries

class DataStoreProvider(private val store: DataStore<Preferences>) : StorageProvider() {

    override fun boolean(key: String): KeyValue<Boolean?> =
        DataStoreKeyValue.BooleanKV(key = key, store = store)

    override fun byteArray(key: String): KeyValue<ByteArray?> =
        DataStoreKeyValue.ByteArrayKV(key = key, store = store)

    override fun double(key: String): KeyValue<Double?> =
        DataStoreKeyValue.DoubleKV(key = key, store = store)

    override fun float(key: String): KeyValue<Float?> =
        DataStoreKeyValue.FloatKV(key = key, store = store)

    override fun int(key: String): KeyValue<Int?> =
        DataStoreKeyValue.IntKV(key = key, store = store)

    override fun long(key: String): KeyValue<Long?> =
        DataStoreKeyValue.LongKV(key = key, store = store)

    override fun string(key: String): KeyValue<String?> =
        DataStoreKeyValue.StringKV(key = key, store = store)

    override fun <T : Enum<T>> enum(
        key: String,
        entries: EnumEntries<T>,
        default: T
    ): KeyValue<T> = DataStoreKeyValue.StringKV(key = key, store = store).map(
        mapTo = { value -> entries.find { it.name.equals(value, true) } },
        mapBack = { it?.name }
    ).required { default }

    override fun <T : Any> model(
        key: String,
        fromJson: (String) -> T,
        toJson: (T) -> String
    ): KeyValue<T?> = DataStoreKeyValue.StringKV(key = key, store = store).map(
        mapTo = { value -> value?.let(fromJson) },
        mapBack = { value -> value?.let(toJson) }
    )
}
