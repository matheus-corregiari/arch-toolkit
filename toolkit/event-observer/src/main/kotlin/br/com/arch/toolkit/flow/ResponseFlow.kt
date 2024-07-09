package br.com.arch.toolkit.flow

import androidx.annotation.NonNull
import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.result.ObserveWrapper
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Experimental
open class ResponseFlow<T> : StateFlow<DataResult<T>> {

    protected val innerFlow: MutableStateFlow<DataResult<T>>

    protected var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        private set

    open fun scope(scope: CoroutineScope) =
        apply { this.scope = scope }

    protected var transformDispatcher: CoroutineDispatcher = Dispatchers.IO
        private set

    open fun transformDispatcher(dispatcher: CoroutineDispatcher) =
        apply { transformDispatcher = dispatcher }

    constructor(value: DataResult<T> = dataResultNone()) {
        this.innerFlow = MutableStateFlow(value)
    }

    private constructor(
        value: DataResult<T>,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        mirror: Flow<DataResult<T>>
    ) : this(value) {
        this.scope = scope
        this.transformDispatcher = dispatcher
        scope.launch { mirror.collect(innerFlow::tryEmit) }
    }

    val status: DataResultStatus
        get() = value.status
    val error: Throwable?
        get() = value.error
    val data: T?
        get() = value.data

    override val replayCache: List<DataResult<T>>
        get() = innerFlow.replayCache
    override var value: DataResult<T>
        get() = innerFlow.value
        protected set(value) {
            innerFlow.value = value
        }

    override suspend fun collect(collector: FlowCollector<DataResult<T>>) =
        innerFlow.collect(collector)

    override fun equals(other: Any?): Boolean = innerFlow == other

    override fun hashCode() = innerFlow.hashCode()

    /**
     *
     */
    suspend fun collect(collector: suspend ObserveWrapper<T>.() -> Unit) {
        newWrapper()
            .scope(scope)
            .transformDispatcher(transformDispatcher)
            .apply { collector.invoke(this) }
            .attachTo(this)
    }

    /**
     *
     */
    fun shareIn(scope: CoroutineScope, started: SharingStarted, replay: Int = 0) = ResponseFlow(
        value = value,
        scope = scope,
        dispatcher = transformDispatcher,
        mirror = innerFlow.shareIn(scope, started, replay)
    )

    /**
     *
     */
    fun mirror(other: Flow<DataResult<T>>) = ResponseFlow(
        value = value,
        scope = scope,
        dispatcher = transformDispatcher,
        mirror = other
    )

    /**
     *
     */
    @Suppress("RemoveExplicitTypeArguments")
    fun <R> mirror(other: Flow<R>, transform: (R) -> T) = ResponseFlow(
        value = value,
        scope = scope,
        dispatcher = transformDispatcher,
        mirror = other.map<R, DataResult<T>> {
            withContext(transformDispatcher) {
                dataResultSuccess(it.let(transform))
            }
        }.catch { emit(dataResultError(it)) }
    )

    /**
     * @return A new instance of ObserveWrapper<T>
     */
    @NonNull
    private fun newWrapper() = ObserveWrapper<T>()
}
