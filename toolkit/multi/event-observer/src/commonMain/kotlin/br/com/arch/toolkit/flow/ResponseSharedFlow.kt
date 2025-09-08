package br.com.arch.toolkit.flow

import br.com.arch.toolkit.result.DataResult
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
open class ResponseSharedFlow<T> internal constructor(
    private val innerFlow: SharedFlow<DataResult<T>>
) : ResponseFlow<T>(innerFlow), SharedFlow<DataResult<T>> by innerFlow {

    override suspend fun collect(
        collector: FlowCollector<DataResult<T>>
    ) = innerFlow.collect(collector)

    /**
     * true -> continue
     * false -> close
     */
    fun cold(hotWhile: suspend (DataResult<T>) -> Boolean = { it.isSuccess.not() }) = ResponseFlow(
        innerFlow = channelFlow<DataResult<T>> {
            try {
                var size = 0
                innerFlow.collect { value ->
                    if (!value.isNone || size != 0) {
                        send(value)
                        size++
                    }
                    if (!hotWhile(value)) close()
                }
            } finally {
                coroutineContext.ensureActive()
            }
        }
    )

    companion object {
        operator fun <T> invoke(): ResponseSharedFlow<T> =
            ResponseSharedFlow(innerFlow = MutableSharedFlow())

        operator fun <T> invoke(
            flow: Flow<T>,
            started: SharingStarted = SharingStarted.WhileSubscribed(),
            replay: Int = (flow as? SharedFlow<T>)?.replayCache?.size
                ?: (flow as? StateFlow<T>)?.replayCache?.size
                ?: 0
        ): ResponseSharedFlow<T> = ResponseFlow.fromFlow(flow)
            .shared(started = started, replay = replay)

        fun <T> from(
            flow: Flow<DataResult<T>>,
            started: SharingStarted = SharingStarted.WhileSubscribed(),
            replay: Int = (flow as? SharedFlow<DataResult<T>>)?.replayCache?.size
                ?: (flow as? StateFlow<DataResult<T>>)?.replayCache?.size
                ?: 0
        ): ResponseSharedFlow<T> = ResponseFlow.from(flow)
            .shared(started = started, replay = replay)

        fun <T, R> from(
            flow: Flow<DataResult<R>>,
            started: SharingStarted = SharingStarted.WhileSubscribed(),
            replay: Int = (flow as? SharedFlow<DataResult<R>>)?.replayCache?.size
                ?: (flow as? StateFlow<DataResult<R>>)?.replayCache?.size
                ?: 0,
            transform: (R) -> T
        ): ResponseSharedFlow<T> = from(flow, transform)
            .shared(started = started, replay = replay)

        fun <T> fromFlow(
            flow: Flow<T>,
            started: SharingStarted = SharingStarted.WhileSubscribed(),
            replay: Int = (flow as? SharedFlow<T>)?.replayCache?.size
                ?: (flow as? StateFlow<T>)?.replayCache?.size
                ?: 0
        ): ResponseSharedFlow<T> = ResponseFlow.fromFlow(flow)
            .shared(started = started, replay = replay)

        fun <T, R> fromFlow(
            flow: Flow<R>,
            started: SharingStarted = SharingStarted.WhileSubscribed(),
            replay: Int = (flow as? SharedFlow<R>)?.replayCache?.size
                ?: (flow as? StateFlow<R>)?.replayCache?.size
                ?: 0,
            transform: suspend (R) -> T
        ): ResponseSharedFlow<T> = fromFlow(flow, transform)
            .shared(started = started, replay = replay)
    }
}
