package br.com.arch.toolkit.flow

import androidx.annotation.NonNull
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.common.ObserveWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

open class ResponseFlow<T> : StateFlow<DataResult<T>> {

    protected val innerFlow: MutableStateFlow<DataResult<T>>
    private val innerScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    constructor(
        value: DataResult<T> = DataResult(
            data = null,
            error = null,
            status = DataResultStatus.LOADING
        )
    ) {
        this.innerFlow = MutableStateFlow(value)
    }

    internal constructor(value: DataResult<T>, mirror: Flow<DataResult<T>>) : this(value) {
        innerScope.launch { mirror.collect(innerFlow::tryEmit) }
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
        newWrapper().apply { collector.invoke(this) }.attachTo(this)
    }

    fun shareIn(scope: CoroutineScope, started: SharingStarted): ResponseFlow<T> {
        return ResponseFlow(value, innerFlow.shareIn(scope, started))
    }

    /**
     * @return A new instance of ObserveWrapper<T>
     */
    @NonNull
    private fun newWrapper() = ObserveWrapper<T>()


}