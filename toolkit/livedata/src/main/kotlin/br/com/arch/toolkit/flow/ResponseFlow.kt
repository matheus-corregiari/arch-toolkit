package br.com.arch.toolkit.flow

import br.com.arch.toolkit.common.DataResult
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

/* TODO Missing implementation =( */
internal open class ResponseFlow<T> : StateFlow<DataResult<T>> {

    override val replayCache: List<DataResult<T>>
        get() = TODO("Not yet implemented")

    override suspend fun collect(collector: FlowCollector<DataResult<T>>): Nothing {
        TODO("Not yet implemented")
    }

    override val value: DataResult<T>
        get() = TODO("Not yet implemented")
}