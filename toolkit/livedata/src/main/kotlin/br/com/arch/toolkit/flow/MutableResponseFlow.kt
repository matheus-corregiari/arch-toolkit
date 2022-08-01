package br.com.arch.toolkit.flow

import br.com.arch.toolkit.common.DataResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/* TODO Missing implementation =( */
internal class MutableResponseFlow<T> : MutableStateFlow<DataResult<T>>, ResponseFlow<T>() {
    override val subscriptionCount: StateFlow<Int>
        get() = TODO("Not yet implemented")

    override suspend fun emit(value: DataResult<T>) {
        TODO("Not yet implemented")
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        TODO("Not yet implemented")
    }

    override fun tryEmit(value: DataResult<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun compareAndSet(expect: DataResult<T>, update: DataResult<T>): Boolean {
        TODO("Not yet implemented")
    }
}