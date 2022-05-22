package br.com.arch.toolkit.livedata.response

import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.livedata.exception.DataTransformationException
import br.com.arch.toolkit.livedata.response.DataResultStatus.ERROR
import br.com.arch.toolkit.livedata.response.DataResultStatus.LOADING
import br.com.arch.toolkit.livedata.response.DataResultStatus.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A custom implementation of ResponseLiveData responsible for replicate a value from another ResponseLiveData
 */
class SwapResponseLiveData<T> : ResponseLiveData<T>() {

    private val sourceLiveData = MediatorLiveData<Any>()
    private val sourceObserver: (Any?) -> Unit = {}
    private var lastSource: ResponseLiveData<*>? = null
    private var discardAfterLoading: Boolean = false

    /**
     * @return True if has some DataSource set, false otherwise
     */
    val hasDataSource: Boolean
        get() = lastSource != null

    /**
     * Changes the actual DataSource
     *
     * @param source The ResponseLiveData to replicate the value
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun swapSource(source: ResponseLiveData<T>) {
        executeSwap(source) { it }
    }

    /**
     * Changes the actual DataSource, with transformation
     *
     * @param source The ResponseLiveData to replicate the value
     * @param dataTransformer Receives the data of the source and change to T value
     * @param errorTransformer Receives the error of the source and change to another Throwable value
     * @param onErrorReturn Receives the error of the source and change to T value
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun <R> swapSource(
        source: ResponseLiveData<R>,
        dataTransformer: (R) -> T,
        errorTransformer: ((Throwable) -> Throwable)? = null,
        onErrorReturn: ((Throwable) -> T)? = null
    ) {
        executeSwap(source = source, transformation = { result ->

            var status = result.status
            val error = result.error?.let { errorTransformer?.invoke(it) ?: result.error }
            var data = result.data?.let(dataTransformer)

            if (data == null && onErrorReturn != null && error != null) {
                data = error.let(onErrorReturn)
            }
            if (onErrorReturn != null && status == ERROR) {
                status = SUCCESS
            }
            val newValue = DataResult(data, error, status)
            newValue.takeIf { value != newValue }
        })
    }

    fun <R> swapSource(
        source: ResponseLiveData<R>,
        transformation: (DataResult<R>) -> DataResult<T>
    ) {
        executeSwap(source, transformation)
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

    /**
     * @param discardAfterLoading if true, when receives something with status different of LOADING,
     * post the value and then, set the value to null, default is false
     */
    fun discardAfterLoading(discardAfterLoading: Boolean) = apply {
        this.discardAfterLoading = discardAfterLoading
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
        transformation: (DataResult<R>) -> DataResult<T>?
    ) {
        clearSource()
        sourceLiveData.addSource(source) { data ->

            if (scope != null) {
                scope?.launch {
                    withContext(Dispatchers.IO) {
                        executeTransformations(data, true, transformation)
                    }
                }
            } else {
                executeTransformations(data, false, transformation)
            }
        }
        lastSource = source
    }

    private fun <R> executeTransformations(
        data: DataResult<R>?,
        callPostValue: Boolean,
        transformation: (DataResult<R>) -> DataResult<T>?
    ) {
        transformation.runCatching { data?.let(this::invoke) }
            .onSuccess {
                if (callPostValue) {
                    it?.let(::postValue)
                } else {
                    it?.let(::setValue)
                }
                if (it?.status != LOADING && discardAfterLoading) {
                    if (callPostValue) {
                        postValue(null)
                    } else {
                        value = null
                    }
                }
            }
            .onFailure {
                val error = DataTransformationException(
                    "Error performing swapSource, please check your transformations",
                    it
                )

                val errorResult = DataResult<T>(null, error, ERROR)
                if (callPostValue) {
                    postValue(errorResult)
                } else {
                    setValue(errorResult)
                }
            }
    }
}
