package br.com.arch.toolkit.livedata.response

import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.common.exception.DataTransformationException
import br.com.arch.toolkit.livedata.ExecutorUtil.runOnNewThread

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
            if (it?.status != DataResultStatus.LOADING && discardAfterLoading) value = null
        }
        lastSource = source
    }

    /**
     * Changes the actual DataSource, with transformation
     *
     * @param source The ResponseLiveData to replicate the value
     * @param dataTransformer Receives the data of the source and change to T value
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun <R> swapSource(
        source: ResponseLiveData<R>,
        dataTransformer: (R) -> T
    ) = swapSource(source, false, dataTransformer)

    /**
     * Changes the actual DataSource, with transformation
     *
     * @param source The ResponseLiveData to replicate the value
     * @param async Indicate swapSource will execute synchronously or asynchronously
     * @param dataTransformer Receives the data of the source and change to T value
     * @param errorTransformer Receives the error of the source and change to another Throwable value
     * @param onErrorReturn Receives the error of the source and change to T value
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun <R> swapSource(
        source: ResponseLiveData<R>,
        async: Boolean,
        dataTransformer: (R) -> T,
        errorTransformer: ((Throwable) -> Throwable)? = null,
        onErrorReturn: ((Throwable) -> T)? = null
    ) = executeSwap(source, async) { result ->

        var status = result.status
        val error = result.error?.let { errorTransformer?.invoke(it) ?: result.error }
        var data = result.data?.let(dataTransformer)

        if (data == null && onErrorReturn != null && error != null) {
            data = error.let(onErrorReturn)
        }
        if (onErrorReturn != null && status == DataResultStatus.ERROR) {
            status = DataResultStatus.SUCCESS
        }
        val newValue = DataResult<T>(data, error, status)
        newValue.takeIf { value != newValue }
    }

    fun <R> swapSource(
        source: ResponseLiveData<R>,
        async: Boolean,
        transformation: (DataResult<R>) -> DataResult<T>
    ) = executeSwap(source, async, transformation)

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
        return hasDataSource.not() || status == DataResultStatus.ERROR
    }

    override fun onActive() {
        super.onActive()
        if (!sourceLiveData.hasObservers()) sourceLiveData.observeForever(sourceObserver)
    }

    override fun onInactive() {
        super.onInactive()
        sourceLiveData.removeObserver(sourceObserver)
    }

    private fun <R> executeSwap(
        source: ResponseLiveData<R>,
        async: Boolean,
        transformation: (DataResult<R>) -> DataResult<T>?
    ) {
        clearSource()
        sourceLiveData.addSource(source) { data ->

            if (async) {
                runOnNewThread {
                    transformation.runCatching { invoke(data) }
                        .onSuccess { it?.let(::postValue) }
                        .onFailure {
                            val error = DataTransformationException(
                                "Error performing swapSource, please check your transformations",
                                it
                            )
                            postValue(DataResult(null, error, DataResultStatus.ERROR))
                        }
                }
            } else {
                transformation.runCatching { invoke(data) }
                    .onSuccess { it?.let(::setValue) }
                    .onFailure {
                        val error = DataTransformationException(
                            "Error performing swapSource, please check your transformations",
                            it
                        )
                        setValue(DataResult(null, error, DataResultStatus.ERROR))
                    }
            }
        }
        lastSource = source
    }
}