@file:OptIn(ExperimentalAtomicApi::class)

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
import kotlin.enums.EnumEntries

internal sealed class DataStoreKeyValue<Saved, Result>(
    protected val key: Preferences.Key<Saved>,
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
        runCatching { preferences[key]?.let(::mapSavedIntoResult) }
            .onFailure { Lumber.tag("DataStore").error(it) }
            .onSuccess { lastValue = it }
            .getOrNull()

    protected open suspend fun set(preferences: MutablePreferences, value: Result?) {
        if (value == null) {
            runCatching { preferences.remove(key) }.onSuccess { lastValue = null }
        } else {
            runCatching { mapResultIntoSaved(value) }.onSuccess { data ->
                data?.let {
                    lastValue = value
                    preferences[key] = data
                }
            }
        }
    }

    abstract fun mapSavedIntoResult(saved: Saved?): Result?
    abstract fun mapResultIntoSaved(result: Result?): Saved?
}

internal sealed class PrimitiveDataStoreKV<T>(
    key: Preferences.Key<T>,
    store: DataStore<Preferences>
) : DataStoreKeyValue<T, T>(key, store) {
    override fun mapResultIntoSaved(result: T?): T? = result
    override fun mapSavedIntoResult(saved: T?): T? = saved

    class BooleanKV(
        key: String,
        store: DataStore<Preferences>
    ) : PrimitiveDataStoreKV<Boolean>(key = booleanPreferencesKey(key), store = store)

    class ByteArrayKV(
        key: String,
        store: DataStore<Preferences>
    ) : PrimitiveDataStoreKV<ByteArray>(key = byteArrayPreferencesKey(key), store = store)

    class DoubleKV(
        key: String,
        store: DataStore<Preferences>
    ) : PrimitiveDataStoreKV<Double>(key = doublePreferencesKey(key), store = store)

    class FloatKV(
        key: String,
        store: DataStore<Preferences>
    ) : PrimitiveDataStoreKV<Float>(key = floatPreferencesKey(key), store = store)

    class IntKV(
        key: String,
        store: DataStore<Preferences>
    ) : PrimitiveDataStoreKV<Int>(key = intPreferencesKey(key), store = store)

    class LongKV(
        key: String,
        store: DataStore<Preferences>
    ) : PrimitiveDataStoreKV<Long>(key = longPreferencesKey(key), store = store)

    class StringKV(
        key: String,
        store: DataStore<Preferences>
    ) : PrimitiveDataStoreKV<String>(key = stringPreferencesKey(key), store = store)
}

internal class EnumDataStoreKV<T : Enum<T>> private constructor(
    key: String,
    store: DataStore<Preferences>,
    private val enumValues: EnumEntries<T>
) : DataStoreKeyValue<String, T>(stringPreferencesKey(key), store) {
    override fun mapSavedIntoResult(saved: String?): T? {
        val requestedName = saved?.normalized() ?: return null
        return enumValues.find { enum -> enum.name.normalized() == requestedName }
    }

    override fun mapResultIntoSaved(result: T?) = result?.name

    private fun String.normalized() = lowercase().replace("_-".toRegex(), "")

    companion object {
        operator fun <T : Enum<T>> invoke(
            key: String,
            store: DataStore<Preferences>,
            entries: EnumEntries<T>,
            default: T
        ): KeyValue<T> = EnumDataStoreKV(key, store, entries).required { default }
    }
}

internal class ModelDataStoreKV<T : Any>(
    key: String,
    store: DataStore<Preferences>,
    private val fromJson: (String) -> T,
    private val toJson: (T) -> String,
) : DataStoreKeyValue<String, T>(stringPreferencesKey(key), store) {
    override fun mapSavedIntoResult(saved: String?): T? = saved?.let(fromJson)
    override fun mapResultIntoSaved(result: T?) = result?.let(toJson)

    companion object {
        @Suppress("LongParameterList")
        operator fun <T : Any> invoke(
            key: String,
            store: DataStore<Preferences>,
            fromJson: (String) -> T,
            toJson: (T) -> String,
            default: T
        ): KeyValue<T> = ModelDataStoreKV(key, store, fromJson, toJson).required { default }
    }
}
