package br.com.arch.toolkit.livedata.response

import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.livedata.ExecutorUtil.async
import br.com.arch.toolkit.livedata.exception.DataTransformationException
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
     * @param transformAsync Indicate swapSource will execute synchronously or asynchronously
     * @param transformation Receives the data of the source and change to T value
     * @param errorTransformer Receives the error of the source and change to another Throwable value
     * @param onErrorReturn Receives the error of the source and change to T value
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun <R> swapSource(
        source: ResponseLiveData<R>,
        transformAsync: Boolean,
        transformation: (R) -> T,
        errorTransformer: ((Throwable) -> Throwable)? = null,
        onErrorReturn: ((Throwable) -> T)? = null
    ) {
        clearSource()
        sourceLiveData.addSource(source) { data ->

            val block: () -> DataResult<T>? = {
                val status = data.status
                val errorData = data.error?.let { onErrorReturn?.invoke(it) }
                if (status == ERROR && errorData != null) {
                    DataResult<T>(errorData, null, SUCCESS)
                } else {
                    val newError = data.error?.let { errorTransformer?.invoke(it) ?: data.error }
                    val newValue = DataResult(data.data?.let(transformation), newError, data.status)
                    if (value != newValue) {
                        newValue
                    } else {
                        null
                    }
                }
            }

            if (transformAsync) {
                async {
                    block.runCatching { invoke() }
                            .onSuccess { it?.let(::postValue) }
                            .onFailure {
                                val error = DataTransformationException("Error performing swapSource, please check your transformations", it)
                                postValue(DataResult(null, error, ERROR))
                            }
                }
            } else {
                block.runCatching { invoke() }
                        .onSuccess { it?.let(::setValue) }
                        .onFailure {
                            val error = DataTransformationException("Error performing swapSource, please check your transformations", it)
                            setValue(DataResult(null, error, ERROR))
                        }
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

    /**
     * Returns true if does not have data source or if the status is equal to DataResultStatus.ERROR
     */
    fun needsRefresh(): Boolean {
        return hasDataSource.not() || status == ERROR
    }

    override fun onActive() {
        super.onActive()
        if (!sourceLiveData.hasObservers()) sourceLiveData.observeForever(sourceObserver)
    }

    override fun onInactive() {
        super.onInactive()
        sourceLiveData.removeObserver(sourceObserver)
    }
}
