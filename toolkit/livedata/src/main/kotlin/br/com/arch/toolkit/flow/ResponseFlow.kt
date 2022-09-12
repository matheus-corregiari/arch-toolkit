package br.com.arch.toolkit.flow

import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.common.ObserveWrapper
import br.com.arch.toolkit.livedata.response.MutableResponseLiveData
import br.com.arch.toolkit.livedata.response.ResponseLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

open class ResponseFlow<T> {

    private val _flow: MutableStateFlow<DataResult<T>>
    val flow: Flow<DataResult<T>> get() = _flow

    private val _liveData: MutableResponseLiveData<T>
    val liveData: ResponseLiveData<T> get() = _liveData

    open var value: DataResult<T>
        get() = _flow.value
        protected set(value) {
            _flow.value = value
            _liveData.value = value
        }

    val status: DataResultStatus
        get() = _flow.value.status
    val error: Throwable?
        get() = _flow.value.error
    val data: T?
        get() = _flow.value.data

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
        _flow = MutableStateFlow(value)
        _liveData = MutableResponseLiveData(value)
    }

    /**
     *
     */
    suspend fun collect(collector: suspend ObserveWrapper<T>.() -> Unit) {
        newWrapper().apply { collector.invoke(this) }.attachTo(_flow)
    }

    /**
     *
     */
    @NonNull
    fun observe(
        @NonNull owner: LifecycleOwner,
        @NonNull wrapperConfig: ObserveWrapper<T>.() -> Unit
    ): ResponseLiveData<T> {
        return liveData.observe(owner, wrapperConfig)
    }

    /**
     * @return A new instance of ObserveWrapper<T>
     */
    @NonNull
    private fun newWrapper() = ObserveWrapper<T>()
}