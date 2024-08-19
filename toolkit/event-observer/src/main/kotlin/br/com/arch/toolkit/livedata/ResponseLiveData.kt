@file:Suppress(
    "KotlinNullnessAnnotation",
    "TooManyFunctions",
    "MemberVisibilityCanBePrivate",
    "DeprecatedCallableAddReplaceWith",
    "unused"
)

package br.com.arch.toolkit.livedata

import android.os.Looper
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.result.ObserveWrapper
import br.com.arch.toolkit.util.mapNotNull
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

    private var mergeLock = Object()
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

    val liveData: LiveData<DataResult<T>> get() = this
    val dataLiveData: LiveData<T> get() = liveData.mapNotNull { it.data }
    val statusLiveData: LiveData<DataResultStatus> get() = liveData.mapNotNull { it.status }
    val errorLiveData: LiveData<Throwable> get() = liveData.mapNotNull { it.error }

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
    fun mapError(@NonNull transformation: (Throwable) -> Throwable): ResponseLiveData<T> {
        val liveData = SwapResponseLiveData<T>()
            .scope(scope)
            .transformDispatcher(transformDispatcher)
        liveData.swapSource(this, { it }, transformation)
        return liveData
    }

    /**
     * Transforms the Error into a T value
     *
     * This block will execute the transformation ONLY when the Error is non null and with the
     * DataResultStatus equals to ERROR
     *
     * After this, the DataResult will be transformed into a DataResultStatus.SUCCESS and with
     * a non null data
     *
     * @param onErrorReturn Receive the actual non null Error value and return the transformed
     * non null T value
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.onErrorReturn
     */
    @NonNull
    fun onErrorReturn(@NonNull onErrorReturn: ((Throwable) -> T)): ResponseLiveData<T> {
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
    @Experimental
    @Deprecated("Use combine instead")
    fun <R> mergeWith(@NonNull source: ResponseLiveData<R>): ResponseLiveData<Pair<T, R>> =
        withDelegate {
            merge(this@ResponseLiveData, source, scope, transformDispatcher)
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
    @Experimental
    @Deprecated("Use combine instead")
    fun mergeWith(
        @NonNull tag: String,
        @NonNull vararg sources: Pair<String, ResponseLiveData<*>>
    ): ResponseLiveData<Map<String, *>> = withDelegate {
        merge(
            scope,
            transformDispatcher,
            sources.toMutableList().apply { add(0, tag to this@ResponseLiveData) }
        )
    }

    /**
     * Combines the result of this ResponseLiveData with a second one after the first success
     * only if the established condition is fulfilled
     *
     * @param source The source this ResponseLiveData will be combined with
     * @param condition The condition for this merge to succeed
     *
     * @return The ResponseLiveData<T, R>
     */
    @NonNull
    @Experimental
    @Deprecated("Use chainWith instead")
    fun <R> followedBy(
        @NonNull source: (T) -> ResponseLiveData<R>,
        @NonNull condition: (T) -> Boolean,
        @NonNull successOnConditionError: Boolean
    ): ResponseLiveData<Pair<T, R?>> = withDelegate {
        followedBy(
            this@ResponseLiveData,
            source,
            scope,
            transformDispatcher,
            condition,
            successOnConditionError
        )
    }

    /**
     * Combines the result of this ResponseLiveData with a second one after the first success
     * only if the established condition is fulfilled
     *
     * @param source The source this ResponseLiveData will be combined with
     * @param condition The condition for this merge to succeed
     *
     * @return The ResponseLiveData<T, R>
     */
    @NonNull
    @Experimental
    @Deprecated("Use chainWith instead")
    fun <R> followedBy(
        @NonNull source: (T) -> ResponseLiveData<R>,
        @NonNull condition: (T) -> Boolean
    ): ResponseLiveData<Pair<T, R>> =
        followedBy(source, condition, false).map { it.first to it.second!! }

    /**
     * Combines the result of this ResponseLiveData with a second one after the first success
     *
     * @param source The source this ResponseLiveData will be combined with
     *
     * @return The ResponseLiveData<T, R>
     */
    @NonNull
    @Experimental
    @Deprecated("Use chainWith instead")
    fun <R> followedBy(@NonNull source: (T) -> ResponseLiveData<R>) = followedBy(source) { true }
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
    fun onNext(@NonNull onNext: ((T) -> Unit)): ResponseLiveData<T> = map {
        onNext(it)
        it
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
     * @see br.com.arch.toolkit.util.safePostValue
     */
    protected open fun safePostValue(value: DataResult<T>?) {
        if (Looper.getMainLooper()?.isCurrentThread == true) {
            this.value = value
        } else {
            postValue(value)
        }
    }

    /**
     * @return A new instance of ObserveWrapper<T>
     */
    @NonNull
    private fun newWrapper() = ObserveWrapper<T>()

    /**
     * Creates synchronously a instance of mergeDelegate
     *
     * @return A new instance of ResponseLiveData<T>
     */
    @Experimental
    private fun <R> withDelegate(func: ResponseLiveDataMergeDelegate.() -> ResponseLiveData<R>): ResponseLiveData<R> =
        synchronized(mergeLock) {
            mergeDelegate = mergeDelegate ?: DefaultResponseLiveDataMergeDelegate()
            return func.invoke(requireNotNull(mergeDelegate))
        }
}
