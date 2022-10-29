package br.com.arch.toolkit.livedata.response

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.common.ObserveWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Custom implementation of LiveData made to help the data handling with needs the interpretation of:
 * - SUCCESS with some data
 * - LOADING without data or error
 * - ERROR   with error
 *
 * This model of interpretation was based on Google Architecture Components Example
 * @see <a href="https://github.com/googlesamples/android-architecture-components">Google's github repository</a>
 */
open class ResponseLiveData<T> : LiveData<DataResult<T>> {

    private var mergeDelegate: ResponseLiveDataMergeDelegate? = null

    protected var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        private set

    open fun scope(scope: CoroutineScope): ResponseLiveData<T> {
        this.scope = scope
        return this
    }

    protected var transformDispatcher: CoroutineDispatcher = Dispatchers.IO
        private set

    open fun transformDispatcher(dispatcher: CoroutineDispatcher): ResponseLiveData<T> {
        transformDispatcher = dispatcher
        return this
    }

    /**
     * @return The actual Error value
     */
    val error: Throwable?
        @Nullable get() = value?.error

    /**
     * @return The actual Status value
     */
    val status: DataResultStatus?
        @Nullable get() = value?.status

    /**
     * @return The actual Data value
     */
    val data: T?
        @Nullable get() = value?.data

    /**
     * Empty constructor when initializing with a value is not needed
     *
     * @return An empty ResponseLiveData<T> instance
     */
    constructor() : super()

    /**
     * Constructor for initializing with a value
     *
     * @param value The initial value for this ResponseLiveData
     *
     * @return An instance of ResponseLiveData<T> with a default value set
     */
    constructor(value: DataResult<T>) : super(value)

    override fun onActive() {
        super.onActive()
        mergeDelegate?.start()
    }

    override fun onInactive() {
        super.onInactive()
        mergeDelegate?.stop()
    }

    // region Loading observer methods
    /**
     * Observes only the Loading Status
     *
     * @param owner The desired Owner to observe
     * @param observer Will receive true when the actual value has the LOADING status, false otherwise
     *
     * @return The ResponseLiveData<T>
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeLoading(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: (Boolean) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { loading(observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Loading Status
     *
     * @param owner The desired Owner to observe
     * @param observer Will be called when the actual value has the LOADING status
     *
     * @return The ResponseLiveData<T>
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeShowLoading(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: () -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { showLoading(observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData does not have the Loading Status
     *
     * @param owner The desired Owner to observe
     * @param observer Will be called when the actual value hasn't the LOADING status
     *
     * @return The ResponseLiveData<T>
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeHideLoading(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: () -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { hideLoading(observer = observer) }
    }

    /**
     * Observes only the Loading Status only one time (until receive another status besides LOADING)
     *
     * @param owner The desired Owner to observe
     * @param observer Will receive true when the actual value has the LOADING status, false otherwise
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeLoading
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeSingleLoading(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: (Boolean) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { loading(single = true, observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Loading Status only one time
     *
     * @param owner The desired Owner to observe
     * @param observer Will be called when the actual value has the LOADING status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeShowLoading
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeSingleShowLoading(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: () -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { showLoading(single = true, observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData does not have the Loading Status only one time
     *
     * @param owner The desired Owner to observe
     * @param observer Will be called when the actual value hasn't the LOADING status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeHideLoading
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeSingleHideLoading(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: () -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { hideLoading(single = true, observer = observer) }
    }
    // endregion

    // region Error observer methods
    /**
     * Observes when the ResponseLiveData has the Error Status and have error
     *
     * @param owner The desired Owner to observe
     * @param observer Will receive the not null error when the actual value has the ERROR status
     *
     * @return The ResponseLiveData<T>
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeError(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: (Throwable) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { error(observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Error Status
     *
     * @param owner The desired Owner to observe
     * @param observer Will be called when the actual value has the ERROR status
     *
     * @return The ResponseLiveData<T>
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeError(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: () -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { error(observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Error Status and have error
     *
     * @param owner The desired Owner to observe
     * @param transformer Transform the Throwable into R before deliver it to the observer
     * @param observer Will receive the not null transformed error when the actual value has the ERROR status
     *
     * @return The ResponseLiveData<T>
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun <R> observeError(
        @NonNull owner: LifecycleOwner,
        @NonNull transformer: (Throwable) -> R,
        @NonNull observer: (R) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { error(transformer = transformer, observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Error Status and have error only one time
     *
     * @param owner The desired Owner to observe
     * @param observer Will receive the not null error when the actual value has the ERROR status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeError
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeSingleError(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: (Throwable) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { error(single = true, observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Error Status and have error only one time
     *
     * @param owner The desired Owner to observe
     * @param transformer Transform the Throwable into R before deliver it to the observer
     * @param observer Will receive the not null transformed error when the actual value has the ERROR status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeError
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun <R> observeSingleError(
        @NonNull owner: LifecycleOwner,
        @NonNull transformer: (Throwable) -> R,
        @NonNull observer: (R) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) {
            error(
                single = true,
                transformer = transformer,
                observer = observer
            )
        }
    }

    /**
     * Observes when the ResponseLiveData has the Error Status only one time
     *
     * @param owner The desired Owner to observe
     * @param observer Will be called when the actual value has the ERROR status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeError
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeSingleError(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: () -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { error(single = true, observer = observer) }
    }
    // endregion

    // region Success observer methods
    /**
     * Observes when the ResponseLiveData has the Success Status
     *
     * @param owner The desired Owner to observe
     * @param observer Will be called when the actual value has the SUCCESS status
     *
     * @return The ResponseLiveData<T>
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeSuccess(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: () -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { success(observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Success Status only one time
     *
     * @param owner The desired Owner to observe
     * @param observer Will be called when the actual value has the SUCCESS status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeSuccess
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeSingleSuccess(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: () -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { success(single = true, observer = observer) }
    }
    // endregion

    //region Data observer methods
    /**
     * Observes when the ResponseLiveData has the Success Status and have data
     *
     * @param owner The desired Owner to observe
     * @param observer Will receive the not null data when the actual value has the SUCCESS status
     *
     * @return The ResponseLiveData<T>
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeData(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: (T) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { data(observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Success Status and have data
     *
     * @param owner The desired Owner to observe
     * @param transformer Transform the T into R before deliver it to the observer
     * @param observer Will receive the not null transformed data when the actual value has the SUCCESS status
     *
     * @return The ResponseLiveData<T>
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun <R> observeData(
        @NonNull owner: LifecycleOwner,
        @NonNull transformer: (T) -> R,
        @NonNull observer: (R) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { data(transformer = transformer, observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Success Status and have data only one time
     *
     * @param owner The desired Owner to observe
     * @param observer Will receive the not null data when the actual value has the SUCCESS status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeData
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun observeSingleData(
        @NonNull owner: LifecycleOwner,
        @NonNull observer: (T) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) { data(single = true, observer = observer) }
    }

    /**
     * Observes when the ResponseLiveData has the Success Status and have data only one time
     *
     * @param owner The desired Owner to observe
     * @param transformer Transform the Data into R before deliver it to the observer
     * @param observer Will receive the not null transformed data when the actual value has the SUCCESS status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeData
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("observe"))
    @NonNull
    fun <R> observeSingleData(
        @NonNull owner: LifecycleOwner,
        @NonNull transformer: (T) -> R,
        @NonNull observer: (R) -> Unit
    ): ResponseLiveData<T> {
        return observe(owner) {
            data(
                single = true,
                transformer = transformer,
                observer = observer
            )
        }
    }
    //endregion

    //region Mappers
    /**
     * Transforms the actual type from T to R
     *
     * @param transformation Receive the actual non null T value and return the transformed non null R value
     *
     * @return The ResponseLiveData<R>
     *
     * @see ResponseLiveData.onNext
     */
    @NonNull
    fun <R> map(@NonNull transformation: ((T) -> R)): ResponseLiveData<R> {
        val liveData = SwapResponseLiveData<R>()
            .scope(scope)
            .transformDispatcher(transformDispatcher)
        liveData.swapSource(this, transformation)
        return liveData
    }

    /**
     * Transforms the Error into another type of Error
     *
     * @param transformation Receive the actual non null Error value and return the transformed non null Error value
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.onError
     */
    @NonNull
    fun mapError(
        @NonNull transformation: (Throwable) -> Throwable
    ): ResponseLiveData<T> {
        val liveData = SwapResponseLiveData<T>()
            .scope(scope)
            .transformDispatcher(transformDispatcher)
        liveData.swapSource(this, { it }, transformation)
        return liveData
    }

    /**
     * Transforms the Error into a T value
     *
     * This block will execute the transformation ONLY when the Error is non null and with the DataResultStatus equal to ERROR
     * After this, the DataResult will be transformed into a DataResultStatus.SUCCESS and with a non null data
     *
     * @param onErrorReturn Receive the actual non null Error value and return the transformed non null T value
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.onErrorReturn
     */
    @NonNull
    fun onErrorReturn(
        @NonNull onErrorReturn: ((Throwable) -> T)
    ): ResponseLiveData<T> {
        val liveData = SwapResponseLiveData<T>()
            .scope(scope)
            .transformDispatcher(transformDispatcher)
        liveData.swapSource(this, { it }, null, onErrorReturn)
        return liveData
    }

    /**
     * Combines the result of this ResponseLiveData with a second one
     *
     * @param source The source this ResponseLiveData will be combined with
     *
     * @return The ResponseLiveData<T, R>
     */
    @NonNull
    fun <R> mergeWith(@NonNull source: ResponseLiveData<R>): ResponseLiveData<Pair<T, R>> {
        if (mergeDelegate == null) mergeDelegate = DefaultResponseLiveDataMergeDelegate()
        return mergeDelegate!!.merge(this, source, scope, transformDispatcher)
    }

    /**
     * Combines the result of this ResponseLiveData with multiple ones
     *
     * @param tag The tag this ResponseLiveData will be marked with
     * @param sources The sources this ResponseLiveData will be combined with
     *
     * @return The ResponseLiveData<T, R>
     */
    @NonNull
    fun mergeWith(
        @NonNull tag: String,
        @NonNull vararg sources: Pair<String, ResponseLiveData<*>>
    ): ResponseLiveData<Map<String, *>> {
        if (mergeDelegate == null) mergeDelegate = DefaultResponseLiveDataMergeDelegate()
        return mergeDelegate!!.merge(
            scope,
            transformDispatcher,
            sources.toMutableList().apply { add(0, tag to this@ResponseLiveData) }
        )
    }
    //endregion

    //region Observability
    /**
     * Execute the function onNext before any observe set after this method be called
     *
     * On this method, you cannot change the entire instance of the T value, but you still can change some attributes
     *
     * @param onNext Receive the actual non null T value
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.map
     */
    @NonNull
    fun onNext(@NonNull onNext: ((T) -> Unit)): ResponseLiveData<T> {
        return map {
            onNext(it)
            it
        }
    }

    /**
     * Execute the function onError before any observe set after this method be called
     *
     * On this method, you cannot change the entire instance of the Error, but you still can change some attributes
     *
     * @param onError Receive the actual non null error value
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.mapError
     */
    @NonNull
    fun onError(@NonNull onError: ((Throwable) -> Unit)): ResponseLiveData<T> {
        return mapError {
            onError(it)
            it
        }
    }

    /**
     * Execute the function transformation before any observe set after this method be called
     *
     * @param transformation With the entire data DataResult<T> and returns the new DataResult<R> value
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.transform
     */
    @NonNull
    fun <R> transform(
        @NonNull transformation: (DataResult<T>) -> DataResult<R>
    ): ResponseLiveData<R> {
        val liveData = SwapResponseLiveData<R>()
            .scope(scope)
            .transformDispatcher(transformDispatcher)
        liveData.swapSource(this, transformation)
        return liveData
    }
    //endregion

    /**
     * Creates a ObserveWrapper<T> and observe it after execute the wrapper configuration
     *
     * @param owner The desired Owner to observe
     * @param wrapperConfig The function to configure the wrapper before observe it
     *
     * @return The ResponseLiveData<T>
     */
    @NonNull
    fun observe(
        @NonNull owner: LifecycleOwner,
        @NonNull wrapperConfig: ObserveWrapper<T>.() -> Unit
    ): ResponseLiveData<T> {
        return newWrapper()
            .scope(scope)
            .transformDispatcher(transformDispatcher)
            .apply(wrapperConfig)
            .attachTo(this, owner)
    }

    /**
     * @return A new instance of ObserveWrapper<T>
     */
    @NonNull
    private fun newWrapper() = ObserveWrapper<T>()
}