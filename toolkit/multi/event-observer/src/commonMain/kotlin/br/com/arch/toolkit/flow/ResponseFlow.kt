@file:Suppress("TooManyFunctions")
@file:OptIn(ExperimentalForInheritanceCoroutinesApi::class)

package br.com.arch.toolkit.flow

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.ObserveWrapper
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

open class ResponseFlow<T> internal constructor(
    private val innerFlow: Flow<DataResult<T>>
) : Flow<DataResult<T>> by innerFlow {

    private var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var transformDispatcher: CoroutineDispatcher = Dispatchers.Default

    fun scope(scope: CoroutineScope) = apply { this.scope = scope }
    fun transformDispatcher(dispatcher: CoroutineDispatcher) =
        apply { transformDispatcher = dispatcher }

    suspend fun collect(wrapper: ObserveWrapper<T>.() -> Unit) =
        ObserveWrapper<T>().scope(scope).transformDispatcher(transformDispatcher).apply(wrapper)
            .suspendFunc { collect { data -> handleResult(data) } }

    /* MUAHAHA */
    fun observe(
        owner: LifecycleOwner,
        wrapper: ObserveWrapper<T>.() -> Unit
    ) = ObserveWrapper<T>().scope(owner.lifecycleScope).transformDispatcher(transformDispatcher)
        .apply(wrapper).suspendFunc {
            owner.lifecycle.repeatOnLifecycle(
                state = Lifecycle.State.STARTED,
                block = { collect(::handleResult) }
            )
        }

    fun <R> map(transform: (T) -> R) = from(flow = innerFlow, transform = transform)

    fun state(
        scope: CoroutineScope = this.scope,
        started: SharingStarted = SharingStarted.WhileSubscribed(),
        initial: DataResult<T> = (innerFlow as? StateFlow<DataResult<T>>)?.value
            ?: (innerFlow as? SharedFlow<DataResult<T>>)?.replayCache?.lastOrNull()
            ?: dataResultNone()
    ): ResponseStateFlow<T> = ResponseStateFlow(
        innerFlow = innerFlow.stateIn(scope = scope, started = started, initialValue = initial)
    )

    fun shared(
        scope: CoroutineScope = this.scope,
        started: SharingStarted = SharingStarted.WhileSubscribed(),
        replay: Int = (innerFlow as? SharedFlow<DataResult<T>>)?.replayCache?.size
            ?: (innerFlow as? StateFlow<DataResult<T>>)?.replayCache?.size ?: 0
    ): ResponseSharedFlow<T> = ResponseSharedFlow(
        innerFlow = innerFlow.shareIn(scope = scope, started = started, replay = replay)
    )

    override fun equals(other: Any?) = innerFlow == other

    override fun hashCode() = innerFlow.hashCode()

    override fun toString() = innerFlow.toString()

    companion object {

        operator fun <T> invoke(): ResponseFlow<T> = ResponseFlow(emptyFlow())
        operator fun <T> invoke(data: DataResult<T>): ResponseFlow<T> = ResponseFlow(flowOf(data))

        operator fun <T> invoke(vararg data: DataResult<T>): ResponseFlow<T> =
            ResponseFlow(flowOf(*data))

        operator fun <T> invoke(dataList: List<DataResult<T>>): ResponseFlow<T> =
            ResponseFlow(dataList.asFlow())

        fun <T> from(flow: Flow<DataResult<T>>): ResponseFlow<T> = ResponseFlow(innerFlow = flow)

        fun <T, R> from(
            flow: Flow<DataResult<R>>,
            transform: (R) -> T
        ): ResponseFlow<T> = ResponseFlow(
            innerFlow = flow
                .map { it.transform(transform) }
                .catch { emit(dataResultError(it)) }
        )

        fun <T> fromFlow(flow: Flow<T>): ResponseFlow<T> = from(
            flow = flow.map(::dataResultSuccess)
        )

        fun <T, R> fromFlow(
            flow: Flow<R>,
            transform: suspend (R) -> T
        ): ResponseFlow<T> = ResponseFlow<T>(
            innerFlow = flow
                .map { dataResultSuccess<T>(transform(it)) }
                .catch { emit(dataResultError<T>(it)) }
        )
    }
}
