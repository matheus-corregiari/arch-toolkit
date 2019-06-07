package br.com.arch.toolkit.livedata.response

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import br.com.arch.toolkit.livedata.extention.observeUntil
import br.com.arch.toolkit.livedata.response.DataResultStatus.ERROR
import br.com.arch.toolkit.livedata.response.DataResultStatus.LOADING
import br.com.arch.toolkit.livedata.response.DataResultStatus.SUCCESS

/**
 * Wrapper to handle the DataResult<T> inside a ResponseLiveData<T>
 */
class ObserveWrapper<T> internal constructor(@NonNull private val liveData: ResponseLiveData<T>) {

    private val eventList = mutableListOf<ObserveEvent<*>>()

    //region Loading
    /**
     * Observes only the Loading Status
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param observer Will receive true when the actual value has the LOADING status, false otherwise
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun loading(@NonNull single: Boolean = false, @NonNull observer: (Boolean) -> Unit): ObserveWrapper<T> {
        eventList.add(LoadingEvent(observer, single))
        return this
    }

    /**
     * Observes when the DataResult has the Loading Status
     *
     * @param single If true, will execute only until the first LOADING status, Default: false
     * @param observer Will be called when the actual value has the LOADING status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun showLoading(@NonNull single: Boolean = false, @NonNull observer: () -> Unit): ObserveWrapper<T> {
        eventList.add(ShowLoadingEvent(observer, single))
        return this
    }

    /**
     * Observes when the DataResult does not have the Loading Status
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param observer Will be called when the actual value hasn't the LOADING status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun hideLoading(@NonNull single: Boolean = false, @NonNull observer: () -> Unit): ObserveWrapper<T> {
        eventList.add(HideLoadingEvent(observer, single))
        return this
    }
    //endregion

    //region Error
    /**
     * Observes when the DataResult has the Error Status
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param observer Will be called when the actual value has the ERROR status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun error(@NonNull single: Boolean = false, @NonNull observer: () -> Unit): ObserveWrapper<T> {
        eventList.add(ErrorEvent(WrapObserver<Throwable, Any>(emptyObserver = observer), single))
        return this
    }

    /**
     * Observes when the DataResult has the Error Status and have error
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param observer Will receive the not null error when the actual value has the ERROR status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun error(@NonNull single: Boolean = false, @NonNull observer: (Throwable) -> Unit): ObserveWrapper<T> {
        eventList.add(ErrorEvent(WrapObserver<Throwable, Any>(observer = observer), single))
        return this
    }

    /**
     * Observes when the DataResult has the Error Status and have error
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param transformer Transform the Throwable into R before deliver it to the observer
     * @param observer Will receive the not null transformed error when the actual value has the ERROR status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun <R> error(@NonNull single: Boolean = false, @NonNull transformer: (Throwable) -> R, @NonNull observer: (R) -> Unit): ObserveWrapper<T> {
        eventList.add(ErrorEvent(WrapObserver(transformer = transformer, transformerObserver = observer), single))
        return this
    }
    //endregion

    //region Success
    /**
     * Observes when the DataResult has the Success Status and have data
     *
     * @param single If true, will execute only until the first SUCCESS status, Default: false
     * @param observer Will receive the not null data when the actual value has the SUCCESS status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun data(@NonNull single: Boolean = false, @NonNull observer: (T) -> Unit): ObserveWrapper<T> {
        eventList.add(SuccessEvent(WrapObserver<T, Any>(observer = observer), single))
        return this
    }

    /**
     * Observes when the DataResult has the Success Status and have data
     *
     * @param single If true, will execute only until the first SUCCESS status, Default: false
     * @param transformer Transform the T into R before deliver it to the observer
     * @param observer Will receive the not null transformed data when the actual value has the SUCCESS status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun <R> data(@NonNull single: Boolean = false, @NonNull transformer: (T) -> R, @NonNull observer: (R) -> Unit): ObserveWrapper<T> {
        eventList.add(SuccessEvent(WrapObserver(transformer = transformer, transformerObserver = observer), single))
        return this
    }

    /**
     * Observes when the DataResult has the Success Status
     *
     * @param single If true, will execute only until the first SUCCESS status, Default: false
     * @param observer Will be called when the actual value has the SUCCESS status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun success(@NonNull single: Boolean = false, @NonNull observer: () -> Unit): ObserveWrapper<T> {
        eventList.add(SuccessEvent(WrapObserver<T, Any>(emptyObserver = observer), single))
        return this
    }
    //endregion

    /**
     * Observes until all observers on Wrapper get removed
     *
     * @param owner The desired Owner to observe
     *
     * @return The ResponseLiveData<T> attached to the Wrapper
     */
    @NonNull
    fun observeOn(@NonNull owner: LifecycleOwner): ResponseLiveData<T> {
        liveData.observeUntil(owner, ::handleResult)
        return liveData
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleResult(@Nullable result: DataResult<T>?): Boolean {

        if (result == null) return false

        val hasObservers = eventList.isNotEmpty()
        val isLoading = result.status == LOADING

        eventList.iterate { event ->
            when {
                // Handle Loading
                event is LoadingEvent -> event.wrapper.let {
                    it.handle(isLoading)
                    return@let isLoading.not()
                }

                // Handle ShowLoading
                event is ShowLoadingEvent && isLoading -> event.wrapper.let {
                    it.handle(isLoading)
                    return@let true
                }

                // Handle HideLoading
                event is HideLoadingEvent && isLoading.not() -> event.wrapper.let {
                    it.handle(isLoading)
                    return@let true
                }

                // Handle Error
                event is ErrorEvent && result.status == ERROR -> event.wrapper.let {
                    it.handle(result.error)
                    return@let true
                }

                // Handle Success
                event is SuccessEvent && result.status == SUCCESS -> (event as SuccessEvent<T>).wrapper.let {
                    it.handle(result.data)
                    return@let true
                }

                else -> return@iterate false
            }
        }
        return hasObservers && eventList.isEmpty()
    }

    private inline fun MutableList<ObserveEvent<*>>.iterate(@NonNull crossinline onEach: (ObserveEvent<*>) -> Boolean) {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val wrapObserver = iterator.next()
            val handled = onEach.invoke(wrapObserver)
            if (wrapObserver.single && handled) {
                iterator.remove()
            }
        }
    }
}

private class WrapObserver<T, V>(
        @Nullable val observer: ((T) -> Unit)? = null,
        @Nullable val emptyObserver: (() -> Unit)? = null,
        @Nullable val transformer: ((T) -> V)? = null,
        @Nullable val transformerObserver: ((V) -> Unit)? = null
) {

    fun handle(@Nullable data: T?) {
        emptyObserver?.invoke()
        data?.also {
            observer?.invoke(data)
            transformer?.invoke(data)?.let {
                transformerObserver?.invoke(it)
            }
        }
    }
}

private sealed class ObserveEvent<T>(@NonNull val wrapper: WrapObserver<T, *>, @NonNull val single: Boolean)

private class LoadingEvent(observer: (Boolean) -> Unit, single: Boolean) : ObserveEvent<Boolean>(WrapObserver<Boolean, Any>(observer), single)

private class ShowLoadingEvent(observer: () -> Unit, single: Boolean) : ObserveEvent<Boolean>(WrapObserver<Boolean, Any>(emptyObserver = observer), single)

private class HideLoadingEvent(observer: () -> Unit, single: Boolean) : ObserveEvent<Boolean>(WrapObserver<Boolean, Any>(emptyObserver = observer), single)

private class ErrorEvent(wrapper: WrapObserver<Throwable, *>, single: Boolean) : ObserveEvent<Throwable>(wrapper, single)

private class SuccessEvent<T>(wrapper: WrapObserver<T, *>, single: Boolean) : ObserveEvent<T>(wrapper, single)
