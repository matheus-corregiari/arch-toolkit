package br.com.arch.toolkit.livedata.response

import android.os.Looper
import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.exception.DataTransformationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A custom implementation of ResponseLiveData responsible for replicate a value from another ResponseLiveData
 */
class SwapResponseLiveData<T> : ResponseLiveData<T> {

    private val sourceLiveData = MediatorLiveData<Any>()
    private val sourceObserver: (Any?) -> Unit = {}
    private var lastSource: ResponseLiveData<*>? = null

    /**
     * Empty constructor when initializing with a value is not needed
     *
     * @return An empty SwapResponseLiveData<T> instance
     */
    constructor() : super()

    /**
     * Constructor for initializing with a value
     *
     * @param value The initial value for this SwapResponseLiveData
     *
     * @return An instance of SwapResponseLiveData<T> with a default value set
     */
    constructor(value: DataResult<T>) : super(value)

    /**
     * @return True if has some DataSource set, false otherwise
     */
    val hasDataSource: Boolean
        get() = lastSource != null

    /**
     * Flag to set whether we're notifying on every change or only on distinct values
     */
    var notifyOnlyOnDistinct: Boolean = false

    /**
     * Changes the actual DataSource
     *
     * @param source The ResponseLiveData to replicate the value
     * @param discardAfterLoading if true, when receives something with status different of LOADING,
     * post the value and then, set the value to null, default is false
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun swapSource(source: ResponseLiveData<T>, discardAfterLoading: Boolean = false) =
        executeSwap(source, discardAfterLoading) { it }

    /**
     * Changes the actual DataSource, with transformation
     *
     * @param source The ResponseLiveData to replicate the value
     * @param transformation Receives the DataResult of the source and change to T value
     *
     * @see SwapResponseLiveData.swapSource
     */
    fun <R> swapSource(
        source: ResponseLiveData<R>,
        transformation: (DataResult<R>) -> DataResult<T>
    ) = executeSwap(source, false, transformation)

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
    ) = executeSwap(source, false) { result ->

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

    override fun scope(scope: CoroutineScope): SwapResponseLiveData<T> {
        return super.scope(scope) as SwapResponseLiveData<T>
    }

    override fun transformDispatcher(dispatcher: CoroutineDispatcher): SwapResponseLiveData<T> {
        return super.transformDispatcher(dispatcher) as SwapResponseLiveData<T>
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
        discardAfterLoading: Boolean,
        transformation: (DataResult<R>) -> DataResult<T>?
    ) {
        clearSource()
        sourceLiveData.addSource(source) { data ->
            scope.launch {
                withContext(transformDispatcher) {
                    transformation.runCatching { invoke(data) }
                }.onFailure {
                    val error = DataTransformationException(
                        "Error performing swapSource, please check your transformations",
                        it
                    )

                    val result = DataResult<T>(null, error, DataResultStatus.ERROR)
                    if (value == result && notifyOnlyOnDistinct) return@onFailure

                    if (Looper.getMainLooper()?.isCurrentThread == true) {
                        value = result
                    } else {
                        postValue(result)
                    }
                }.getOrNull().let {
                    if (value == it && notifyOnlyOnDistinct) return@let

                    if (Looper.getMainLooper()?.isCurrentThread == true) {
                        value = it
                    } else {
                        postValue(it)
                    }

                    if (it?.status != DataResultStatus.LOADING && discardAfterLoading) value = null
                }
            }
        }
        lastSource = source
    }
}