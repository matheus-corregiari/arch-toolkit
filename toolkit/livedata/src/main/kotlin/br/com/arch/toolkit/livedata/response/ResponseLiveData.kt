package br.com.arch.toolkit.livedata.response

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import br.com.arch.toolkit.livedata.extention.observe
import br.com.arch.toolkit.livedata.extention.observeUntil
import br.com.arch.toolkit.livedata.response.DataResultStatus.ERROR
import br.com.arch.toolkit.livedata.response.DataResultStatus.LOADING

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
        observe(owner) {
            observer.invoke(it.status == LOADING)
        }
        return this
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
        observe(owner) {
            if (it.status == LOADING) observer.invoke()
        }
        return this
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
        observe(owner) {
            if (it.status != LOADING) observer.invoke()
        }
        return this
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
        observeUntil(owner) {
            if (it == null) return@observeUntil false
            observer.invoke(it.status == LOADING)
            it.status != LOADING
        }
        return this
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
        observeUntil(owner) {
            if (it == null) return@observeUntil false
            if (it.status == LOADING) observer.invoke()
            it.status == LOADING
        }
        return this
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
        observeUntil(owner) {
            if (it == null) return@observeUntil false
            if (it.status != LOADING) observer.invoke()
            it.status != LOADING
        }
        return this
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
        observe(owner) {
            if (it.status == ERROR) it.error?.apply(observer)
        }
        return this
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
        observe(owner) {
            if (it.status == ERROR) observer.invoke()
        }
        return this
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
        observeUntil(owner) {
            if (it == null) return@observeUntil false
            if (it.status == ERROR) it.error?.apply(observer)
            it.status == ERROR
        }
        return this
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
        observeUntil(owner) {
            if (it == null) return@observeUntil false
            if (it.status == ERROR) observer.invoke()
            it.status == ERROR
        }
        return this
    }
    // endregion

    // region Success observer methods
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
        observe(owner) {
            if (it.status == DataResultStatus.SUCCESS) it.data?.apply(observer)
        }
        return this
    }

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
        observe(owner) {
            if (it.status == DataResultStatus.SUCCESS) observer.invoke()
        }
        return this
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
        observeUntil(owner) {
            if (it == null) return@observeUntil false
            if (it.status == DataResultStatus.SUCCESS) it.data?.apply(observer)
            it.status == DataResultStatus.SUCCESS
        }
        return this
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
        observeUntil(owner) {
            if (it == null) return@observeUntil false
            if (it.status == DataResultStatus.SUCCESS) observer.invoke()
            it.status == DataResultStatus.SUCCESS
        }
        return this
    }
    // endregion

    /**
     * Transform the actual type from T to R
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
        liveData.swapSource(this, transformation)
        return liveData
    }

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
}