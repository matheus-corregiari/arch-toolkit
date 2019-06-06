package br.com.arch.toolkit.livedata.response

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import br.com.arch.toolkit.livedata.extention.observeUntil
import br.com.arch.toolkit.livedata.response.DataResultStatus.ERROR
import br.com.arch.toolkit.livedata.response.DataResultStatus.LOADING
import br.com.arch.toolkit.livedata.response.DataResultStatus.SUCCESS

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
        return wrap().loading(observer).observeOn(owner)
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
        return wrap().showLoading(observer).observeOn(owner)
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
        return wrap().hideLoading(observer).observeOn(owner)
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
        return wrap().loadingSingle(observer).observeOn(owner)
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
        return wrap().showLoadingSingle(observer).observeOn(owner)
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
        return wrap().hideLoadingSingle(observer).observeOn(owner)
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
        return wrap().error(observer).observeOn(owner)
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
        return wrap().error(observer).observeOn(owner)
    }

    /**
     * Observes when the ResponseLiveData has the Error Status and have error
     *
     * @param owner The desired Owner to observe
     * @param transformer Transform the Throwable into R before deliver it to the observer
     * @param observer Will receive the not null error when the actual value has the ERROR status
     *
     * @return The ResponseLiveData<T>
     */
    @NonNull
    fun <R> observeError(@NonNull owner: LifecycleOwner, @NonNull transformer: (Throwable) -> R, @NonNull observer: (R) -> Unit): ResponseLiveData<T> {
        return wrap().error(transformer = transformer, observer = observer).observeOn(owner)
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
        return wrap().singleError(observer).observeOn(owner)
    }

    /**
     * Observes when the ResponseLiveData has the Error Status and have error only one time
     *
     * @param owner The desired Owner to observe
     * @param transformer Transform the Throwable into R before deliver it to the observer
     * @param observer Will receive the not null error when the actual value has the ERROR status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeError
     */
    @NonNull
    fun <R> observeSingleError(@NonNull owner: LifecycleOwner, @NonNull transformer: (Throwable) -> R, @NonNull observer: (R) -> Unit): ResponseLiveData<T> {
        return wrap().singleError(transformer = transformer, observer = observer).observeOn(owner)
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
        return wrap().singleError(observer = observer).observeOn(owner)
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
        return wrap().data(observer = observer).observeOn(owner)
    }

    /**
     * Observes when the ResponseLiveData has the Success Status and have data
     *
     * @param owner The desired Owner to observe
     * @param transformer Transform the Data into R before deliver it to the observer
     * @param observer Will receive the not null data when the actual value has the SUCCESS status
     *
     * @return The ResponseLiveData<T>
     */
    @NonNull
    fun <R> observeData(@NonNull owner: LifecycleOwner, @NonNull transformer: (T) -> R, @NonNull observer: (R) -> Unit): ResponseLiveData<T> {
        return wrap().data(transformer = transformer, observer = observer).observeOn(owner)
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
        return wrap().success(observer = observer).observeOn(owner)
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
        return wrap().singleData(observer = observer).observeOn(owner)
    }

    /**
     * Observes when the ResponseLiveData has the Success Status and have data only one time
     *
     * @param owner The desired Owner to observe
     * @param transformer Transform the Data into R before deliver it to the observer
     * @param observer Will receive the not null data when the actual value has the SUCCESS status
     *
     * @return The ResponseLiveData<T>
     *
     * @see ResponseLiveData.observeData
     */
    @NonNull
    fun <R> observeSingleData(@NonNull owner: LifecycleOwner, @NonNull transformer: (T) -> R, @NonNull observer: (R) -> Unit): ResponseLiveData<T> {
        return wrap().singleData(transformer = transformer, observer = observer).observeOn(owner)
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
        return wrap().singleSuccess(observer = observer).observeOn(owner)
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

    @NonNull
    fun wrap(): ObserveWrapper<T> {
        return ObserveWrapper(this)
    }

    class ObserveWrapper<T> internal constructor(private val liveData: ResponseLiveData<T>) {

        private val loadingObservers = mutableListOf<WrapObserver<Boolean, *>>()
        private val showLoadingObservers = mutableListOf<WrapObserver<Any, *>>()
        private val hideLoadingObservers = mutableListOf<WrapObserver<Any, *>>()
        private val errorObservers = mutableListOf<WrapObserver<Throwable, *>>()
        private val successObservers = mutableListOf<WrapObserver<T, *>>()

        //region Loading
        fun loading(observer: (Boolean) -> Unit): ObserveWrapper<T> {
            loadingObservers.add(WrapObserver<Boolean, Any>(false, observer = observer))
            return this
        }

        fun showLoading(observer: () -> Unit): ObserveWrapper<T> {
            showLoadingObservers.add(WrapObserver<Any, Any>(false, emptyObserver = observer))
            return this
        }

        fun hideLoading(observer: () -> Unit): ObserveWrapper<T> {
            hideLoadingObservers.add(WrapObserver<Any, Any>(false, emptyObserver = observer))
            return this
        }

        fun loadingSingle(observer: (Boolean) -> Unit): ObserveWrapper<T> {
            loadingObservers.add(WrapObserver<Boolean, Any>(true, observer = observer))
            return this
        }

        fun showLoadingSingle(observer: () -> Unit): ObserveWrapper<T> {
            showLoadingObservers.add(WrapObserver<Any, Any>(true, emptyObserver = observer))
            return this
        }

        fun hideLoadingSingle(observer: () -> Unit): ObserveWrapper<T> {
            hideLoadingObservers.add(WrapObserver<Any, Any>(true, emptyObserver = observer))
            return this
        }
        //endregion

        //region Error
        fun error(observer: () -> Unit): ObserveWrapper<T> {
            errorObservers.add(WrapObserver<Throwable, Any>(false, emptyObserver = observer))
            return this
        }

        fun error(observer: (Throwable) -> Unit): ObserveWrapper<T> {
            errorObservers.add(WrapObserver<Throwable, Any>(false, observer = observer))
            return this
        }

        fun <R> error(transformer: (Throwable) -> R, observer: (R) -> Unit): ObserveWrapper<T> {
            errorObservers.add(WrapObserver(false, transformer = transformer, transformerObserver = observer))
            return this
        }

        fun singleError(observer: () -> Unit): ObserveWrapper<T> {
            errorObservers.add(WrapObserver<Throwable, Any>(true, emptyObserver = observer))
            return this
        }

        fun singleError(observer: (Throwable) -> Unit): ObserveWrapper<T> {
            errorObservers.add(WrapObserver<Throwable, Any>(true, observer = observer))
            return this
        }

        fun <R> singleError(transformer: (Throwable) -> R, observer: (R) -> Unit): ObserveWrapper<T> {
            errorObservers.add(WrapObserver(true, transformer = transformer, transformerObserver = observer))
            return this
        }
        //endregion

        //region Success
        fun data(observer: (T) -> Unit): ObserveWrapper<T> {
            successObservers.add(WrapObserver<T, Any>(false, observer = observer))
            return this
        }

        fun <R> data(transformer: (T) -> R, observer: (R) -> Unit): ObserveWrapper<T> {
            successObservers.add(WrapObserver(false, transformer = transformer, transformerObserver = observer))
            return this
        }

        fun success(observer: () -> Unit): ObserveWrapper<T> {
            successObservers.add(WrapObserver<T, Any>(false, emptyObserver = observer))
            return this
        }

        fun singleData(observer: (T) -> Unit): ObserveWrapper<T> {
            successObservers.add(WrapObserver<T, Any>(true, observer = observer))
            return this
        }

        fun <R> singleData(transformer: (T) -> R, observer: (R) -> Unit): ObserveWrapper<T> {
            successObservers.add(WrapObserver(true, transformer = transformer, transformerObserver = observer))
            return this
        }

        fun singleSuccess(observer: () -> Unit): ObserveWrapper<T> {
            successObservers.add(WrapObserver<T, Any>(true, emptyObserver = observer))
            return this
        }
        //endregion

        internal fun observeOn(owner: LifecycleOwner): ResponseLiveData<T> {
            liveData.observeUntil(owner, ::handleResult)
            return liveData
        }

        private fun handleResult(result: DataResult<T>?): Boolean {

            if (result == null) return false

            val hasLoadingObservers = loadingObservers.isNotEmpty()
            val hasShowLoadingObservers = showLoadingObservers.isNotEmpty()
            val hasHideLoadingObservers = hideLoadingObservers.isNotEmpty()
            val hasErrorObservers = errorObservers.isNotEmpty()
            val hasSuccessObservers = successObservers.isNotEmpty()

            val isLoading = result.status == LOADING
            loadingObservers.iterate({ it.single && isLoading.not() }) { isLoading.also(it::handle) }
            showLoadingObservers.iterate({ it.single && isLoading }) { if (isLoading) it.handle(null) }
            hideLoadingObservers.iterate({ it.single && isLoading.not() }) { if (isLoading.not()) it.handle(null) }

            when {
                result.status == SUCCESS -> successObservers.iterate { result.data.also(it::handle) }
                result.status == ERROR -> errorObservers.iterate { result.error.also(it::handle) }
            }

            val doneLoadingObserve = if (hasLoadingObservers) loadingObservers.isEmpty() else true
            val doneShowLoadingObserve = if (hasShowLoadingObservers) showLoadingObservers.isEmpty() else true
            val doneHideLoadingObserve = if (hasHideLoadingObservers) hideLoadingObservers.isEmpty() else true
            val doneErrorObserve = if (hasErrorObservers) errorObservers.isEmpty() else true
            val doneSuccessObserve = if (hasSuccessObservers) successObservers.isEmpty() else true

            return doneLoadingObserve && doneShowLoadingObserve && doneHideLoadingObserve && doneErrorObserve && doneSuccessObserve
        }

        private inline fun <V> MutableList<WrapObserver<V, *>>.iterate(crossinline shouldRemove: (WrapObserver<V, *>) -> Boolean = WrapObserver<V, *>::single, crossinline onEach: (WrapObserver<V, *>) -> Unit) {
            val iterator = iterator()
            while (iterator.hasNext()) {
                val wrapObserver = iterator.next()
                onEach.invoke(wrapObserver)
                if (shouldRemove.invoke(wrapObserver)) {
                    iterator.remove()
                }
            }
        }

        private class WrapObserver<V, I>(
                val single: Boolean,
                val observer: ((V) -> Unit)? = null,
                val emptyObserver: (() -> Unit)? = null,
                val transformer: ((V) -> I)? = null,
                val transformerObserver: ((I) -> Unit)? = null) {

            fun handle(data: V?) {
                emptyObserver?.invoke()
                data?.also {
                    observer?.invoke(data)
                    transformer?.invoke(data)?.let {
                        transformerObserver?.invoke(it)
                    }
                }
            }
        }
    }
}