package br.com.arch.toolkit.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.storage.core.KeyValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
internal sealed class DataStoreKeyValue<Result>(
    private val key: Preferences.Key<Result>,
    private val store: DataStore<Preferences>
) : KeyValue<Result?>() {

    override var lastValue: Result? = null
    private val job = AtomicReference<Job>(Job().apply { complete() })

    override fun get(): Flow<Result?> = store.data.map { pref -> get(pref) }

    override fun set(value: Result?, scope: CoroutineScope?) {
        job.load().cancel()
        job.store((scope ?: this.scope).launch { store.edit { pref -> set(pref, value) } })
    }

    protected open suspend fun get(preferences: Preferences): Result? =
        runCatching { preferences[key] }
            .onFailure { Lumber.tag("DataStore - get").error(it) }
            .onSuccess { lastValue = it }
            .getOrNull()

    protected open suspend fun set(preferences: MutablePreferences, value: Result?) {
        runCatching {
            if (value == null) {
                preferences.remove(key)
            } else {
                preferences[key] = value
            }
        }.onSuccess { lastValue = value }.onFailure { Lumber.tag("DataStore - set").error(it) }
    }

    internal class BooleanKV(key: String, store: DataStore<Preferences>) :
        DataStoreKeyValue<Boolean>(key = booleanPreferencesKey(key), store = store)

    internal class ByteArrayKV(key: String, store: DataStore<Preferences>) :
        DataStoreKeyValue<ByteArray>(key = byteArrayPreferencesKey(key), store = store)

    internal class DoubleKV(key: String, store: DataStore<Preferences>) :
        DataStoreKeyValue<Double>(key = doublePreferencesKey(key), store = store)

    internal class FloatKV(key: String, store: DataStore<Preferences>) :
        DataStoreKeyValue<Float>(key = floatPreferencesKey(key), store = store)

    internal class IntKV(key: String, store: DataStore<Preferences>) :
        DataStoreKeyValue<Int>(key = intPreferencesKey(key), store = store)

    internal class LongKV(key: String, store: DataStore<Preferences>) :
        DataStoreKeyValue<Long>(key = longPreferencesKey(key), store = store)

    internal class StringKV(key: String, store: DataStore<Preferences>) :
        DataStoreKeyValue<String>(key = stringPreferencesKey(key), store = store)
}
