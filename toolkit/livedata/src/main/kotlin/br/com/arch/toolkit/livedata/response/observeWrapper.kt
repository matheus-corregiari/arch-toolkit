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
        eventList.add(LoadingEvent(observer, single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes only the Loading Status, receives true when status is LOADING and false when status sis non-LOADING
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param withData If true, will execute only with NonNull data
     * @param observer Will receive true when the actual value has the LOADING status, false otherwise
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun loading(@NonNull single: Boolean = false, @NonNull withData: Boolean, @NonNull observer: (Boolean) -> Unit): ObserveWrapper<T> {
        eventList.add(LoadingEvent(observer, single, getEventDataStatus(withData)))
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
        eventList.add(ShowLoadingEvent(observer, single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes when the DataResult has the Loading Status
     *
     * @param single If true, will execute only until the first LOADING status, Default: false
     * @param withData If true, will execute only with the status LOADING and with NonNull data
     * @param observer Will be called when the actual value has the LOADING status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun showLoading(@NonNull single: Boolean = false, @NonNull withData: Boolean, @NonNull observer: () -> Unit): ObserveWrapper<T> {
        eventList.add(ShowLoadingEvent(observer, single, getEventDataStatus(withData)))
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
        eventList.add(HideLoadingEvent(observer, single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes when the DataResult does not have the Loading Status
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param withData If true, will execute only with the status non-LOADING and with NonNull data
     * @param observer Will be called when the actual value hasn't the LOADING status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun hideLoading(@NonNull single: Boolean = false, @NonNull withData: Boolean, @NonNull observer: () -> Unit): ObserveWrapper<T> {
        eventList.add(HideLoadingEvent(observer, single, getEventDataStatus(withData)))
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
        eventList.add(ErrorEvent(WrapObserver<Throwable, Any>(emptyObserver = observer), single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes when the DataResult has the Error Status
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param withData If true, will execute only with the status ERROR and with NonNull data
     * @param observer Will be called when the actual value has the ERROR status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun error(@NonNull single: Boolean = false, @NonNull withData: Boolean, @NonNull observer: () -> Unit): ObserveWrapper<T> {
        eventList.add(ErrorEvent(WrapObserver<Throwable, Any>(emptyObserver = observer), single, getEventDataStatus(withData)))
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
        eventList.add(ErrorEvent(WrapObserver<Throwable, Any>(observer = observer), single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes when the DataResult has the Error Status and have error
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param withData If true, will execute only with the status ERROR and with NonNull data
     * @param observer Will receive the not null error when the actual value has the ERROR status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun error(@NonNull single: Boolean = false, @NonNull withData: Boolean, @NonNull observer: (Throwable) -> Unit): ObserveWrapper<T> {
        eventList.add(ErrorEvent(WrapObserver<Throwable, Any>(observer = observer), single, getEventDataStatus(withData)))
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
        eventList.add(ErrorEvent(WrapObserver(transformer = transformer, transformerObserver = observer), single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes when the DataResult has the Error Status and have error
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param withData If true, will execute only with the status ERROR and with NonNull data
     * @param transformer Transform the Throwable into R before deliver it to the observer
     * @param observer Will receive the not null transformed error when the actual value has the ERROR status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun <R> error(@NonNull single: Boolean = false, @NonNull withData: Boolean, @NonNull transformer: (Throwable) -> R, @NonNull observer: (R) -> Unit): ObserveWrapper<T> {
        eventList.add(ErrorEvent(WrapObserver(transformer = transformer, transformerObserver = observer), single, getEventDataStatus(withData)))
        return this
    }
    //endregion

    //region Success
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
        eventList.add(SuccessEvent(WrapObserver<Void, Any>(emptyObserver = observer), single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes when the DataResult has the Success Status
     *
     * @param single If true, will execute only until the first SUCCESS status, Default: false
     * @param withData If true, will execute only with the status SUCCESS and with NonNull data
     * @param observer Will be called when the actual value has the SUCCESS status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun success(@NonNull single: Boolean = false, @NonNull withData: Boolean, @NonNull observer: () -> Unit): ObserveWrapper<T> {
        eventList.add(SuccessEvent(WrapObserver<Void, Any>(emptyObserver = observer), single, getEventDataStatus(withData)))
        return this
    }
    //endregion

    //region Data
    /**
     * Observes when the DataResult has data
     *
     * @param single If true, will execute only until the first Non null Data, Default: false
     * @param observer Will receive the not null data
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun data(@NonNull single: Boolean = false, @NonNull observer: (T) -> Unit): ObserveWrapper<T> {
        eventList.add(DataEvent(WrapObserver<T, Any>(observer = observer), single))
        return this
    }

    /**
     * Observes when the DataResult has data
     *
     * @param single If true, will execute only until the first Non null Data, Default: false
     * @param transformer Transform the T into R before deliver it to the observer
     * @param observer Will receive the not null transformed data
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun <R> data(@NonNull single: Boolean = false, @NonNull transformer: (T) -> R, @NonNull observer: (R) -> Unit): ObserveWrapper<T> {
        eventList.add(DataEvent(WrapObserver(transformer = transformer, transformerObserver = observer), single))
        return this
    }
    //endregion

    //region Result
    /**
     * Observes the DataResult
     *
     * @param single If true, will execute only until the first non-null result, Default: false
     * @param observer Will be called for every result and will receive the not null result
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun result(@NonNull single: Boolean = false, @NonNull observer: (DataResult<T>) -> Unit): ObserveWrapper<T> {
        eventList.add(ResultEvent(WrapObserver<DataResult<T>, Any>(observer = observer), single))
        return this
    }

    /**
     * Observes the DataResult
     *
     * @param single If true, will execute only until the first non-null result, Default: false
     * @param transformer Transform the T into R before deliver it to the observer
     * @param observer Will be called for every result and will receive the not null transformed result
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun <R> result(@NonNull single: Boolean = false, @NonNull transformer: (DataResult<T>) -> R, @NonNull observer: (R) -> Unit): ObserveWrapper<T> {
        eventList.add(ResultEvent(WrapObserver(transformer = transformer, transformerObserver = observer), single))
        return this
    }

    /**
     * Observes the DataResult
     *
     * @param single If true, will execute only until the first non-null result, Default: false
     * @param observer Will be called for every result
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun result(@NonNull single: Boolean = false, @NonNull observer: () -> Unit): ObserveWrapper<T> {
        eventList.add(ResultEvent(WrapObserver<DataResult<T>, Any>(emptyObserver = observer), single))
        return this
    }
    //endregion

    //region Status
    /**
     * Observes the Status
     *
     * @param single If true, will execute only until the first non-null status, Default: false
     * @param observer Will be called for every status and will receive the not null status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun status(@NonNull single: Boolean = false, @NonNull observer: (DataResultStatus) -> Unit): ObserveWrapper<T> {
        eventList.add(StatusEvent(WrapObserver<DataResultStatus, Any>(observer = observer), single))
        return this
    }

    /**
     * Observes the Status
     *
     * @param single If true, will execute only until the first non-null status, Default: false
     * @param transformer Transform the T into R before deliver it to the observer
     * @param observer Will be called for every status and will receive the not null transformed status
     *
     * @return The ObserveWrapper<T>
     */
    @NonNull
    fun <R> status(@NonNull single: Boolean = false, @NonNull transformer: (DataResultStatus) -> R, @NonNull observer: (R) -> Unit): ObserveWrapper<T> {
        eventList.add(StatusEvent(WrapObserver(transformer = transformer, transformerObserver = observer), single))
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

        eventList.iterate(result) { event ->
            when {
                // Handle Loading
                event is LoadingEvent -> event.run {
                    wrapper.handle(isLoading)
                    return@run isLoading.not()
                }

                // Handle ShowLoading
                event is ShowLoadingEvent && isLoading -> event.run {
                    wrapper.handle(isLoading)
                    return@run true
                }

                // Handle HideLoading
                event is HideLoadingEvent && isLoading.not() -> event.run {
                    wrapper.handle(isLoading)
                    return@run true
                }

                // Handle Error
                event is ErrorEvent && result.status == ERROR -> event.run {
                    wrapper.handle(result.error)
                    return@run true
                }

                // Handle Success
                event is SuccessEvent && result.status == SUCCESS -> event.run {
                    wrapper.handle(null)
                    return@run true
                }

                // Handle Data
                event is DataEvent -> (event as DataEvent<T>).wrapper.let {
                    it.handle(result.data)
                    return@let result.data != null
                }

                // Handle Result
                event is ResultEvent<*> -> (event as ResultEvent<T>).run {
                    wrapper.handle(result)
                    return@run true
                }

                // Handle Status
                event is StatusEvent -> event.run {
                    wrapper.handle(result.status)
                    return@run true
                }

                else -> return@iterate false
            }
        }
        return hasObservers && eventList.isEmpty()
    }

    private inline fun MutableList<ObserveEvent<*>>.iterate(@NonNull result: DataResult<*>, @NonNull crossinline onEach: (ObserveEvent<*>) -> Boolean) {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val wrapObserver = iterator.next()
            val eventDataStatusHandled = handleEventDataStatus(wrapObserver.dataStatus, result)
            val handled = eventDataStatusHandled && onEach.invoke(wrapObserver)
            if (wrapObserver.single && handled) {
                iterator.remove()
            }
        }
    }

    private fun handleEventDataStatus(@NonNull dataStatus: EventDataStatus, @NonNull result: DataResult<*>): Boolean {
        return when (dataStatus) {
            EventDataStatus.WITH_DATA -> result.data != null
            EventDataStatus.WITHOUT_DATA -> result.data == null
            EventDataStatus.DOESNT_MATTER -> true
        }
    }

    private fun getEventDataStatus(withData: Boolean): EventDataStatus {
        return if (withData) EventDataStatus.WITH_DATA
        else EventDataStatus.WITHOUT_DATA
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

private enum class EventDataStatus {
    WITH_DATA, WITHOUT_DATA, DOESNT_MATTER
}

private sealed class ObserveEvent<T>(@NonNull val wrapper: WrapObserver<T, *>, @NonNull val single: Boolean, @NonNull val dataStatus: EventDataStatus)

private class LoadingEvent(@NonNull observer: (Boolean) -> Unit, @NonNull single: Boolean, @NonNull dataStatus: EventDataStatus) : ObserveEvent<Boolean>(WrapObserver<Boolean, Any>(observer), single, dataStatus)

private class ShowLoadingEvent(@NonNull observer: () -> Unit, @NonNull single: Boolean, @NonNull dataStatus: EventDataStatus) : ObserveEvent<Boolean>(WrapObserver<Boolean, Any>(emptyObserver = observer), single, dataStatus)

private class HideLoadingEvent(@NonNull observer: () -> Unit, @NonNull single: Boolean, @NonNull dataStatus: EventDataStatus) : ObserveEvent<Boolean>(WrapObserver<Boolean, Any>(emptyObserver = observer), single, dataStatus)

private class ErrorEvent(@NonNull wrapper: WrapObserver<Throwable, *>, @NonNull single: Boolean, @NonNull dataStatus: EventDataStatus) : ObserveEvent<Throwable>(wrapper, single, dataStatus)

private class SuccessEvent(@NonNull wrapper: WrapObserver<Void, *>, @NonNull single: Boolean, @NonNull dataStatus: EventDataStatus) : ObserveEvent<Void>(wrapper, single, dataStatus)

private class DataEvent<T>(@NonNull wrapper: WrapObserver<T, *>, @NonNull single: Boolean) : ObserveEvent<T>(wrapper, single, EventDataStatus.DOESNT_MATTER)

private class ResultEvent<T>(@NonNull wrapper: WrapObserver<DataResult<T>, *>, @NonNull single: Boolean) : ObserveEvent<DataResult<T>>(wrapper, single, EventDataStatus.DOESNT_MATTER)

private class StatusEvent(@NonNull wrapper: WrapObserver<DataResultStatus, *>, @NonNull single: Boolean) : ObserveEvent<DataResultStatus>(wrapper, single, EventDataStatus.DOESNT_MATTER)
