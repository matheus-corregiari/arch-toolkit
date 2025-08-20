package br.com.arch.toolkit.storage.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.lastOrNull
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

expect fun <T> KeyValue<T>.instant(): T

@StorageApi
abstract class KeyValue<ResultData> {

    protected var scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
        private set

    abstract var lastValue: ResultData
        protected set

    abstract fun get(): Flow<ResultData>
    abstract fun set(value: ResultData, scope: CoroutineScope?)

    fun scope(scope: CoroutineScope) = apply { this.scope = scope }
    suspend fun current(): ResultData = get().lastOrNull() ?: lastValue

    @Composable
    fun state(scope: CoroutineScope? = rememberCoroutineScope()): MutableState<ResultData> {
        val current: ResultData by get().collectAsState(lastValue)
        return mutableStateOf(
            value = current,
            policy = object : SnapshotMutationPolicy<ResultData> {
                override fun equivalent(a: ResultData, b: ResultData) =
                    (a == b).also { set(b, scope) }
            }
        )
    }

    fun delegate() = object : ReadWriteProperty<Any, ResultData> {
        override fun getValue(thisRef: Any, property: KProperty<*>): ResultData = instant()

        override fun setValue(thisRef: Any, property: KProperty<*>, value: ResultData) =
            set(value = value, scope = scope)
    }

    companion object {
        @StorageApi
        fun <T> KeyValue<T?>.required(): KeyValue<T> = RequiredKeyValue(
            keyValue = this,
            default = null
        )

        @StorageApi
        fun <T> KeyValue<T?>.required(default: () -> T): KeyValue<T> = RequiredKeyValue(
            keyValue = this,
            default = default
        )

        @StorageApi
        fun <T, R> KeyValue<T>.map(mapTo: (T) -> R, mapBack: (R) -> T): KeyValue<R> = MapKeyValue(
            keyValue = this,
            mapTo = mapTo,
            mapBack = mapBack
        )
    }
}
