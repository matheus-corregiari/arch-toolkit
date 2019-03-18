package br.com.arch.toolkit.livedata.response

import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.livedata.ExecutorUtil.async
import br.com.arch.toolkit.livedata.response.DataResultStatus.ERROR
import br.com.arch.toolkit.livedata.response.DataResultStatus.LOADING
import br.com.arch.toolkit.livedata.response.DataResultStatus.SUCCESS

/**
 * A custom implementation of ResponseLiveData responsible for replicate a value from another ResponseLiveData
 */
class SwapResponseLiveData<T> : ResponseLiveData<T>() {

    private val sourceLiveData = MediatorLiveData<Any>()
    private val sourceObserver: (Any?) -> Unit = {}
    private var lastSource: ResponseLiveData<*>? = null

    /**
     * @return True if has some DataSource set, false otherwise
     */
    val hasDataSource: Boolean
        get() = lastSource != null

    /**
     * Changes the actual DataSource
     *
     * @param source The ResponseLiveData to replicate the value
     * @param discardAfterLoading if true, when receives something with status different of LOADING,
     * post the value and then, set the value to null, default is false
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun swapSource(source: ResponseLiveData<T>, discardAfterLoading: Boolean = false) {
        clearSource()
        sourceLiveData.addSource(source) {
            value = it
            if (it?.status != LOADING && discardAfterLoading) value = null
        }
        lastSource = source
    }

    /**
     * Changes the actual DataSource, with transformation
     *
     * @param source The ResponseLiveData to replicate the value
     * @param transformation Receives the data of the source and change to T value
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun <R> swapSource(source: ResponseLiveData<R>, transformation: (R) -> T) {
        clearSource()
        sourceLiveData.addSource(source) { data ->
            async {
                if (data == null) return@async
                val newValue = when (data.status) {
                    SUCCESS -> DataResult(data.data?.let(transformation), null, SUCCESS)
                    ERROR -> DataResult<T>(null, data.error, ERROR)
                    LOADING -> DataResult<T>(null, null, LOADING)
                }
                if (value != newValue) postValue(newValue)
            }
        }
        lastSource = source
    }

    override fun onActive() {
        super.onActive()
        if (!sourceLiveData.hasObservers()) sourceLiveData.observeForever(sourceObserver)
    }

    override fun onInactive() {
        super.onInactive()
        sourceLiveData.removeObserver(sourceObserver)
    }

    private fun clearSource() {
        lastSource?.let { sourceLiveData.removeSource(it) }
        lastSource = null
    }
}