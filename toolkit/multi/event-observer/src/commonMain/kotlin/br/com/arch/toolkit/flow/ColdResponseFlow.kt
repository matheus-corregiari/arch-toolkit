package br.com.arch.toolkit.flow

import androidx.annotation.NonNull
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.ObserveWrapper
import br.com.arch.toolkit.util.dataResultNone
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf

open class ColdResponseFlow<T> : Flow<DataResult<T>> {

    private val innerFlow: Flow<DataResult<T>>

    protected var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        private set

    open fun scope(scope: CoroutineScope) =
        apply { this.scope = scope }

    protected var transformDispatcher: CoroutineDispatcher = Dispatchers.IO
        private set

    open fun transformDispatcher(dispatcher: CoroutineDispatcher) =
        apply { transformDispatcher = dispatcher }

    constructor(value: DataResult<T> = dataResultNone()) {
        this.innerFlow = flowOf(value)
    }

    internal constructor(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        mirror: Flow<DataResult<T>>,
        until: suspend (DataResult<T>) -> Boolean,
    ) {
        this.scope = scope
        this.transformDispatcher = dispatcher
        this.innerFlow = channelFlow {
            try {
                var size = 0
                mirror.collect { value ->
                    if (!value.isNone || size != 0) {
                        send(value)
                        size++
                    }
                    if (!until(value)) close()
                }
            } finally {
                coroutineContext.ensureActive()
            }
        }
    }

    override suspend fun collect(collector: FlowCollector<DataResult<T>>) =
        innerFlow.collect(collector)

    override fun equals(other: Any?): Boolean = innerFlow == other

    override fun hashCode() = innerFlow.hashCode()

    /**
     *
     */
    suspend fun collect(collector: suspend ObserveWrapper<T>.() -> Unit) = newWrapper()
        .scope(scope)
        .transformDispatcher(transformDispatcher)
        .apply { collector.invoke(this) }
        .attachTo(this)

    /**
     * @return A new instance of ObserveWrapper<T>
     */
    @NonNull
    private fun newWrapper() = ObserveWrapper<T>()
}
