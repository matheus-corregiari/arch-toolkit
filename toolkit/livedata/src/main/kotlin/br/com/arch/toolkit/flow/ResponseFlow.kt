package br.com.arch.toolkit.flow

import androidx.annotation.NonNull
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.common.ObserveWrapper
import kotlinx.coroutines.flow.MutableStateFlow

open class ResponseFlow<T> {

    private val flow: MutableStateFlow<DataResult<T>>
    open var value: DataResult<T>
        get() = flow.value
        protected set(value) {
            flow.value = value
        }

    val status: DataResultStatus
        get() = flow.value.status
    val error: Throwable?
        get() = flow.value.error
    val data: T?
        get() = flow.value.data

    /**
     * Empty constructor when initializing with a value is not needed
     *
     * @return An empty ResponseFlow<T> instance
     */
    constructor() : this(DataResult(null, null, DataResultStatus.LOADING))

    /**
     * Constructor for initializing with a value
     *
     * @param value The initial value for this MutableResponseLiveData
     *
     * @return An instance of ResponseFlow<T> with a default value set
     */
    constructor(value: DataResult<T>) {
        flow = MutableStateFlow(value)
    }

    /**
     *
     */
    suspend fun collect(collector: suspend ObserveWrapper<T>.() -> Unit) {
        newWrapper().apply { collector.invoke(this) }.attachTo(flow)
    }

    /**
     * @return A new instance of ObserveWrapper<T>
     */
    @NonNull
    private fun newWrapper() = ObserveWrapper<T>()
}