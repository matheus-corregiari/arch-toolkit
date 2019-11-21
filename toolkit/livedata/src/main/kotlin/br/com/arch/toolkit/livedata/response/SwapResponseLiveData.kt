package br.com.arch.toolkit.livedata.response

import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.livedata.ExecutorUtil.async
import br.com.arch.toolkit.livedata.response.DataResultStatus.LOADING

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
     * @param transformAsync Indicate swapSource will execute synchronously or asynchronously
     * @param transformation Receives the data of the source and change to T value
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun <R> swapSource(
        source: ResponseLiveData<R>,
        transformAsync: Boolean,
        transformation: (R) -> T
    ) {
        clearSource()
        sourceLiveData.addSource(source) { data ->
            if (transformAsync) {
                async {
                    doTransformation(data, transformation) { postValue(it) }
                }
            } else {
                doTransformation(data, transformation) { value = it }
            }
        }
        lastSource = source
    }

    /**
     * Synchronously changes the actual DataSource, with transformation
     *
     * @param source The ResponseLiveData to replicate the value
     * @param transformation Receives the data of the source and change to T value
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun <R> swapSource(source: ResponseLiveData<R>, transformation: (R) -> T) {
        swapSource(source, false, transformation)
    }

    /**
     * Removes source
     */
    fun clearSource() {
        lastSource?.let { sourceLiveData.removeSource(it) }
        lastSource = null
    }
    
    override fun onActive() {
        super.onActive()
        if (!sourceLiveData.hasObservers()) sourceLiveData.observeForever(sourceObserver)
    }

    override fun onInactive() {
        super.onInactive()
        sourceLiveData.removeObserver(sourceObserver)
    }

    private inline fun <R> doTransformation(
        data: DataResult<R>,
        transformation: (R) -> T,
        crossinline newValueListener: (DataResult<T>) -> Unit
    ) {
        val newValue = DataResult<T>(data.data?.let(transformation), data.error, data.status)

        if (value != newValue) {
            newValueListener(newValue)
        }
    }
}
