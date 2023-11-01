@file:Suppress("TooManyFunctions")

package br.com.arch.toolkit.flow

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Constructor for initializing with a value
 *
 * @param value The initial value for this MutableResponseLiveData
 *
 * @return An instance of ResponseFlow<T> with a default value set
 */
class MutableResponseFlow<T>(value: DataResult<T> = dataResultNone()) :
    ResponseFlow<T>(value), MutableStateFlow<DataResult<T>> {

    override fun scope(scope: CoroutineScope) =
        super.scope(scope) as MutableResponseFlow<T>

    override fun transformDispatcher(dispatcher: CoroutineDispatcher) =
        super.transformDispatcher(dispatcher) as MutableResponseFlow<T>

    // region Override standard flow methods
    override var value: DataResult<T>
        get() = super.value
        public set(value) {
            super.value = value
        }

    override val subscriptionCount: StateFlow<Int> get() = innerFlow.subscriptionCount

    override fun compareAndSet(expect: DataResult<T>, update: DataResult<T>) =
        innerFlow.compareAndSet(expect, update)

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() = innerFlow.resetReplayCache()

    override fun tryEmit(value: DataResult<T>) = innerFlow.tryEmit(value)
    override suspend fun emit(value: DataResult<T>) = innerFlow.emit(value)
    // endregion

    // region Custom Emitters
    suspend fun emitSuccess() = emit(dataResultSuccess(null))
    suspend fun tryEmitSuccess() = tryEmit(dataResultSuccess(null))

    suspend fun emitData(value: T) = emit(dataResultSuccess(value))
    suspend fun tryEmitData(value: T) = tryEmit(dataResultSuccess(value))

    suspend fun emitLoading(value: T? = null, throwable: Throwable? = null) =
        emit(dataResultLoading(value, throwable))

    suspend fun tryEmitLoading(value: T? = null, throwable: Throwable? = null) =
        tryEmit(dataResultLoading(value, throwable))

    suspend fun emitError(throwable: Throwable, value: T? = null) =
        emit(dataResultError(throwable, value))

    suspend fun tryEmitError(throwable: Throwable, value: T? = null) =
        tryEmit(dataResultError(throwable, value))

    suspend fun emitNone() = emit(dataResultNone())
    suspend fun tryEmitNone() = tryEmit(dataResultNone())
    // endregion
}