package br.com.arch.toolkit.livedata.response

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

/**
 * Custom implementation of LiveData made to help the data handling with needs the interpretation of:
 * - SUCCESS with some data
 * - LOADING without data or error
 * - ERROR   with error
 *
 * This model of interpretation was based on Google Architecture Components Example
 * @see <a href="https://github.com/googlesamples/android-architecture-components">Google's github repository</a>
 */
open class ResponseLiveData<T> : LiveData<DataResult<T>>() {

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

    // region Loading observer methods
    /**
     * Observes only the Loading Status
     *
     * @param owner The desired Owner to observe
     * @param observer Will receive true when the actual value has the LOADING status, false otherwise
     *
     * @return The ResponseLiveData<T>
     */
    @NonNull
    fun observeLoading(@NonNull owner: LifecycleOwner, @NonNull observer: (Boolean) -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeShowLoading(@NonNull owner: LifecycleOwner, @NonNull observer: () -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeHideLoading(@NonNull owner: LifecycleOwner, @NonNull observer: () -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeSingleLoading(@NonNull owner: LifecycleOwner, @NonNull observer: (Boolean) -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeSingleShowLoading(@NonNull owner: LifecycleOwner, @NonNull observer: () -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeSingleHideLoading(@NonNull owner: LifecycleOwner, @NonNull observer: () -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeError(@NonNull owner: LifecycleOwner, @NonNull observer: (Throwable) -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeError(@NonNull owner: LifecycleOwner, @NonNull observer: () -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun <R> observeError(@NonNull owner: LifecycleOwner, @NonNull transformer: (Throwable) -> R, @NonNull observer: (R) -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeSingleError(@NonNull owner: LifecycleOwner, @NonNull observer: (Throwable) -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun <R> observeSingleError(@NonNull owner: LifecycleOwner, @NonNull transformer: (Throwable) -> R, @NonNull observer: (R) -> Unit): ResponseLiveData<T> {
        return observe(owner) { error(single = true, transformer = transformer, observer = observer) }
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
    @NonNull
    fun observeSingleError(@NonNull owner: LifecycleOwner, @NonNull observer: () -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeSuccess(@NonNull owner: LifecycleOwner, @NonNull observer: () -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeSingleSuccess(@NonNull owner: LifecycleOwner, @NonNull observer: () -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeData(@NonNull owner: LifecycleOwner, @NonNull observer: (T) -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun <R> observeData(@NonNull owner: LifecycleOwner, @NonNull transformer: (T) -> R, @NonNull observer: (R) -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun observeSingleData(@NonNull owner: LifecycleOwner, @NonNull observer: (T) -> Unit): ResponseLiveData<T> {
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
    @NonNull
    fun <R> observeSingleData(@NonNull owner: LifecycleOwner, @NonNull transformer: (T) -> R, @NonNull observer: (R) -> Unit): ResponseLiveData<T> {
        return observe(owner) { data(single = true, transformer = transformer, observer = observer) }
    }
    //endregion

    /**
     * Transform the actual type from T to R
     *
     * @param transformAsync Indicate swapSource will execute synchronously or asynchronously
     * @param transformation Receive the actual non null T value and return the transformed non null R value
     *
     * @return The ResponseLiveData<R>
     *
     * @see ResponseLiveData.onNext
     */
    @NonNull
    fun <R> map(transformAsync: Boolean, @NonNull transformation: ((T) -> R)): ResponseLiveData<R> {
        val liveData = SwapResponseLiveData<R>()
        liveData.swapSource(this, transformAsync, transformation)
        return liveData
    }

    @NonNull
    fun <R> map(@NonNull transformation: ((T) -> R)): ResponseLiveData<R> {
        val liveData = SwapResponseLiveData<R>()
        liveData.swapSource(this, false, transformation)
        return liveData
    }

    /**
     * Execute the function onNext before any observe set after this method be called
     *
     * On this method, you cannot change the entire instance of the T value, but you still can change some attributes
     *
     * @param transformAsync Indicate map will execute synchronously or asynchronously
     * @param onNext Receive the actual non null T value
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.map
     */
    @NonNull
    fun onNext(transformAsync: Boolean, @NonNull onNext: ((T) -> Unit)): ResponseLiveData<T> {
        return map(transformAsync) {
            onNext(it)
            it
        }
    }

    @NonNull
    fun onNext(@NonNull onNext: ((T) -> Unit)): ResponseLiveData<T> {
        return map(false) {
            onNext(it)
            it
        }
    }

    /**
     * Creates a ObserveWrapper<T> and observe it after execute the wrapper configuration
     *
     * @param owner The desired Owner to observe
     * @param wrapperConfig The function to configure the wrapper before observe it
     *
     * @return The ResponseLiveData<T>
     */
    @NonNull
    inline fun observe(@NonNull owner: LifecycleOwner, @NonNull crossinline wrapperConfig: ObserveWrapper<T>.() -> Unit): ResponseLiveData<T> {
        return newWrapper().apply(wrapperConfig).observeOn(owner)
    }

    /**
     * @return A new instance of ObserveWrapper<T>
     */
    @NonNull
    fun newWrapper() = ObserveWrapper(this)
}
