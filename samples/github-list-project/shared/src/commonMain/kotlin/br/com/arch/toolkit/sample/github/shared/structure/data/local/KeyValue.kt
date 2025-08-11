@file:Suppress("UnrememberedMutableState")

package br.com.arch.toolkit.sample.github.shared.structure.data.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.enums.EnumEntries
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class KeyValue<K, T, R>(
    protected val key: Preferences.Key<K>,
    private val store: PrefsDataStore,
) {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null

    abstract var lastValue: R

    abstract fun retrieve(value: T): R
    abstract fun save(value: R): T

    fun instant(): R = runBlocking { current() } ?: lastValue
    suspend fun current(): R = get().lastOrNull() ?: lastValue

    protected abstract suspend fun get(preferences: Preferences): R
    protected abstract suspend fun set(preferences: MutablePreferences, value: R)

    fun get(): Flow<R> = store.data.map { pref -> get(pref).also { lastValue = it } }
    fun set(value: R, scope: CoroutineScope?) {
        job?.cancel()
        job = (scope ?: this.scope).launch {
            store.edit { set(preferences = it, value = value) }
        }
    }

    @Composable
    fun asMutableState(scope: CoroutineScope? = null): MutableState<R> {
        val current: R by get().collectAsState(lastValue)
        return mutableStateOf(
            value = current,
            policy = object : SnapshotMutationPolicy<R> {
                override fun equivalent(a: R, b: R) = (a == b).also { set(b, scope) }
            }
        )
    }

    @Composable
    fun <V> asMutableState(mapper: (R) -> V): MutableState<V> {
        val current: R by get().collectAsState(lastValue)
        return mutableStateOf(mapper(current))
    }

    fun delegate() = object : ReadWriteProperty<Any, R> {
        override operator fun getValue(thisRef: Any, property: KProperty<*>): R =
            runBlocking(scope.coroutineContext) { current() }

        override operator fun setValue(thisRef: Any, property: KProperty<*>, value: R) =
            set(value, scope)
    }
}

abstract class OptionalKeyValue<T, R>(
    key: Preferences.Key<T>,
    store: PrefsDataStore
) : KeyValue<T, T?, R?>(key, store) {
    override var lastValue: R? = null

    override suspend fun get(preferences: Preferences): R? =
        runCatching { retrieve(preferences[key]) }
            .onFailure { Lumber.tag("KeyValue").error(it) }
            .getOrNull()

    override suspend fun set(preferences: MutablePreferences, value: R?) {
        if (value == null) {
            preferences.remove(key)
        } else {
            val newValue = runCatching { save(value) }.getOrNull()
            if (newValue != null) {
                lastValue = value
                preferences[key] = newValue
            }
        }
    }
}

abstract class RequiredKeyValue<T : Any, R : Any>(
    key: Preferences.Key<T>,
    store: PrefsDataStore,
    private val default: R
) : KeyValue<T, T, R>(key, store) {
    override var lastValue: R = default

    override suspend fun get(preferences: Preferences): R =
        runCatching { preferences[key]?.let(::retrieve) ?: default }
            .onFailure { Lumber.tag("KeyValue").error(it) }
            .getOrDefault(default)

    override suspend fun set(preferences: MutablePreferences, value: R) {
        val newValue = runCatching { save(value) }.getOrNull()
        if (newValue != null) {
            lastValue = value
            preferences[key] = newValue
        }
    }
}

class ByteArrayKeyValue(
    key: String,
    store: PrefsDataStore,
    private val default: ByteArray? = null
) : OptionalKeyValue<ByteArray, ByteArray>(key = byteArrayPreferencesKey(key), store = store) {
    override fun retrieve(value: ByteArray?) = value ?: default
    override fun save(value: ByteArray?) = value
}

class BooleanKeyValue(
    key: String,
    store: PrefsDataStore,
    private val default: Boolean? = null
) : OptionalKeyValue<Boolean, Boolean>(key = booleanPreferencesKey(key), store = store) {
    override fun retrieve(value: Boolean?) = value ?: default
    override fun save(value: Boolean?) = value
}

class DoubleKeyValue(
    key: String,
    store: PrefsDataStore,
    private val default: Double? = null
) : OptionalKeyValue<Double, Double>(key = doublePreferencesKey(key), store = store) {
    override fun retrieve(value: Double?) = value ?: default
    override fun save(value: Double?) = value
}

class FloatKeyValue(
    key: String,
    store: PrefsDataStore,
    private val default: Float? = null
) : OptionalKeyValue<Float, Float>(key = floatPreferencesKey(key), store = store) {
    override fun retrieve(value: Float?) = value ?: default
    override fun save(value: Float?) = value
}

class IntKeyValue(
    key: String,
    store: PrefsDataStore,
    private val default: Int? = null
) : OptionalKeyValue<Int, Int>(key = intPreferencesKey(key), store = store) {
    override fun retrieve(value: Int?) = value ?: default
    override fun save(value: Int?) = value
}

class LongKeyValue(
    key: String,
    store: PrefsDataStore,
    private val default: Long? = null
) : OptionalKeyValue<Long, Long>(key = longPreferencesKey(key), store = store) {
    override fun retrieve(value: Long?) = value ?: default
    override fun save(value: Long?) = value
}

class StringKeyValue(
    key: String,
    store: PrefsDataStore,
    private val default: String? = null
) : OptionalKeyValue<String, String>(key = stringPreferencesKey(key), store = store) {
    override fun retrieve(value: String?) = (value ?: default)?.ifBlank { null }
    override fun save(value: String?) = value?.ifBlank { null }
}

class ObjectKeyValue<T : Any>(
    key: String,
    store: PrefsDataStore,
    private val fromStore: (String) -> T,
    private val toStore: (T?) -> String?,
    private val default: T? = null
) : OptionalKeyValue<String, T>(key = stringPreferencesKey(key), store = store) {
    override fun retrieve(value: String?): T? = value?.let(fromStore) ?: default
    override fun save(value: T?): String? = value?.let(toStore)

    companion object {
        inline operator fun <reified T : Any> invoke(
            key: String,
            store: PrefsDataStore,
            json: Json,
            default: T? = null
        ): ObjectKeyValue<T> = ObjectKeyValue(
            key = key,
            store = store,
            fromStore = json::decodeFromString,
            toStore = json::encodeToString,
            default = default
        )
    }
}

class EnumKeyValue<T : Enum<T>>(
    key: String,
    store: PrefsDataStore,
    private val all: EnumEntries<T>,
    private val default: T
) : RequiredKeyValue<String, T>(key = stringPreferencesKey(key), store = store, default = default) {
    override fun retrieve(value: String): T =
        all.find { it.name.contains(value, true) } ?: default

    override fun save(value: T): String = value.name
}
