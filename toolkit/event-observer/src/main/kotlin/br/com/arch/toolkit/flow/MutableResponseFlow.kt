package br.com.arch.toolkit.flow

import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.common.dataResultNone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MutableResponseFlow<T> : ResponseFlow<T>, MutableStateFlow<DataResult<T>> {

    override var value: DataResult<T>
        get() = super.value
        set(value) {
            super.value = value
        }

    /**
     * Empty constructor when initializing with a value is not needed
     *
     * @return An empty ResponseFlow<T> instance
     */
    constructor() : this(dataResultNone())

    /**
     * Constructor for initializing with a value
     *
     * @param value The initial value for this MutableResponseLiveData
     *
     * @return An instance of ResponseFlow<T> with a default value set
     */
    constructor(value: DataResult<T>) : super(value)

    override val subscriptionCount: StateFlow<Int>
        get() = innerFlow.subscriptionCount

    override fun compareAndSet(expect: DataResult<T>, update: DataResult<T>) =
        innerFlow.compareAndSet(expect, update)

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() = innerFlow.resetReplayCache()

    override fun tryEmit(value: DataResult<T>) = innerFlow.tryEmit(value)

    override suspend fun emit(value: DataResult<T>) = innerFlow.emit(value)

    suspend fun emitData(value: T, throwable: Throwable? = null) =
        emit(DataResult(value, throwable, DataResultStatus.SUCCESS))

    suspend fun tryEmitData(value: T, throwable: Throwable? = null) =
        tryEmit(DataResult(value, throwable, DataResultStatus.SUCCESS))

    suspend fun emitLoading(value: T? = null, throwable: Throwable? = null) =
        emit(DataResult(value, throwable, DataResultStatus.LOADING))

    suspend fun tryEmitLoading(value: T? = null, throwable: Throwable? = null) =
        tryEmit(DataResult(value, throwable, DataResultStatus.LOADING))

    suspend fun emitError(value: T?, throwable: Throwable) =
        emit(DataResult(value, throwable, DataResultStatus.ERROR))

    suspend fun tryEmitError(value: T?, throwable: Throwable) =
        tryEmit(DataResult(value, throwable, DataResultStatus.ERROR))

}