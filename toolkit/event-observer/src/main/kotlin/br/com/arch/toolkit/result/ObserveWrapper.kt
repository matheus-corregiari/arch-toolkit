@file:Suppress(
    "KotlinNullnessAnnotation",
    "TooManyFunctions",
    "CyclomaticComplexMethod",
    "UNCHECKED_CAST"
)

package br.com.arch.toolkit.result

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import br.com.arch.toolkit.exception.DataResultException
import br.com.arch.toolkit.exception.DataResultTransformationException
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.observeUntil
import java.lang.Thread.UncaughtExceptionHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Wrapper to handle the DataResult`<T>`
 *
 * This is to help all treatment events without any more hell if checks
 *
 * @see DataResult
 * @see ResponseLiveData
 * @see ResponseFlow
 */
class ObserveWrapper<T> internal constructor() {

    /**
     * List of all events configured
     */
    private val eventList = mutableListOf<ObserveEvent<*>>()

    /**
     * Big "catch" to handle unexpected exceptions inside the executions of the events
     */
    private val scopeUncaughtError = CoroutineExceptionHandler { _, throwable ->
        val thread = Thread.currentThread()
        thread.uncaughtExceptionHandler = DataResultUncaughtExceptionHandler()
        thread.uncaughtExceptionHandler?.uncaughtException(thread, throwable)
    }

    /**
     * Scope to run all the events
     *
     * **Default: Dispatchers.Main + SupervisorJob()**
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     scope(/* Your Custom Scope Here */)
     *}
     * ```
     * @see CoroutineScope
     */
    private var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    fun scope(scope: CoroutineScope): ObserveWrapper<T> {
        this.scope = scope
        return this
    }

    /**
     * Dispatcher to run all the event transformations
     *
     * **Default: Dispatchers.IO**
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     transformDispatcher(/* Your Custom Dispatcher Here */)
     *}
     * ```
     * @see CoroutineDispatcher
     */
    private var transformDispatcher: CoroutineDispatcher = Dispatchers.IO
    fun transformDispatcher(dispatcher: CoroutineDispatcher): ObserveWrapper<T> {
        transformDispatcher = dispatcher
        return this
    }

    //region Loading
    /**
     * Observes only the Loading Status
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     loading(single = true /* default - false */, observer = { loading ->
     *         if(loading) {
     *             // Display Loading
     *         } else {
     *             // Hide Loading
     *         }
     *     })
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param observer Will receive true when the actual value has the LOADING status, false otherwise
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.loading
     * @see ObserveWrapper.hideLoading
     * @see ObserveWrapper.showLoading
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun loading(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend (Boolean) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(LoadingEvent(observer, single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes only the Loading Status, receives true when status is LOADING and false when status sis non-LOADING
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     loading(single = true /* default - false */, withData = false, observer = { loading ->
     *         if(loading) {
     *             // Display Loading
     *         } else {
     *             // Hide Loading
     *         }
     *     })
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param withData If true, will execute only with NonNull data
     * @param observer Will receive true when the actual value has the LOADING status, false otherwise
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.loading
     * @see ObserveWrapper.hideLoading
     * @see ObserveWrapper.showLoading
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun loading(
        @NonNull single: Boolean = false,
        @NonNull withData: Boolean,
        @NonNull observer: suspend (Boolean) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(LoadingEvent(observer, single, getEventDataStatus(withData)))
        return this
    }

    /**
     * Observes when the DataResult has the Loading Status
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     showLoading(single = true /* default - false */, observer = {
     *         // Display Loading
     *     })
     *}
     * ```
     *
     * @param single If true, will execute only until the first LOADING status, Default: false
     * @param observer Will be called when the actual value has the LOADING status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.loading
     * @see ObserveWrapper.hideLoading
     * @see ObserveWrapper.showLoading
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun showLoading(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit
    ): ObserveWrapper<T> {
        eventList.add(ShowLoadingEvent(observer, single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes when the DataResult has the Loading Status
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     showLoading(single = true /* default - false */, withData = false, observer = {
     *         // Display Loading
     *     })
     *}
     * ```
     *
     * @param single If true, will execute only until the first LOADING status, Default: false
     * @param withData If true, will execute only with the status LOADING and with NonNull data
     * @param observer Will be called when the actual value has the LOADING status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.loading
     * @see ObserveWrapper.hideLoading
     * @see ObserveWrapper.showLoading
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun showLoading(
        @NonNull single: Boolean = false,
        @NonNull withData: Boolean,
        @NonNull observer: suspend () -> Unit
    ): ObserveWrapper<T> {
        eventList.add(ShowLoadingEvent(observer, single, getEventDataStatus(withData)))
        return this
    }

    /**
     * Observes when the DataResult does not have the Loading Status
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     hideLoading(single = true /* default - false */, observer = {
     *         // Hide Loading
     *     })
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param observer Will be called when the actual value hasn't the LOADING status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.loading
     * @see ObserveWrapper.hideLoading
     * @see ObserveWrapper.showLoading
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun hideLoading(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit
    ): ObserveWrapper<T> {
        eventList.add(HideLoadingEvent(observer, single, EventDataStatus.DOESNT_MATTER))
        return this
    }

    /**
     * Observes when the DataResult does not have the Loading Status
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     hideLoading(single = true /* default - false */, withData = false, observer = {
     *         // Hide Loading
     *     })
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param withData If true, will execute only with the status non-LOADING and with NonNull data
     * @param observer Will be called when the actual value hasn't the LOADING status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.loading
     * @see ObserveWrapper.hideLoading
     * @see ObserveWrapper.showLoading
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun hideLoading(
        @NonNull single: Boolean = false,
        @NonNull withData: Boolean,
        @NonNull observer: suspend () -> Unit
    ): ObserveWrapper<T> {
        eventList.add(HideLoadingEvent(observer, single, getEventDataStatus(withData)))
        return this
    }
    //endregion

    //region Error
    /**
     * Observes when the DataResult has the Error Status
     *
     * ## Usage:
     * ```kotlin
     * fun onError() {
     *     // Handle Error
     * }
     *
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     error(single = true /* default - false */, observer = ::onError)
     *}
     * ```
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param observer Will be called when the actual value has the ERROR status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.error
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun error(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                WrapObserver<Throwable, Any>(emptyObserver = observer),
                single, EventDataStatus.DOESNT_MATTER
            )
        )
        return this
    }

    /**
     * Observes when the DataResult has the Error Status
     *
     * ## Usage:
     * ```kotlin
     * fun onError() {
     *     // Handle Error
     * }
     *
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     error(single = true /* default - false */, withData = false, observer = ::onError)
     *}
     * ```
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param withData If true, will execute only with the status ERROR and with NonNull data
     * @param observer Will be called when the actual value has the ERROR status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.error
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun error(
        @NonNull single: Boolean = false,
        @NonNull withData: Boolean,
        @NonNull observer: suspend () -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                WrapObserver<Throwable, Any>(emptyObserver = observer),
                single,
                getEventDataStatus(withData)
            )
        )
        return this
    }

    /**
     * Observes when the DataResult has the Error Status and have error
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     error(single = true /* default - false */, observer = { error ->
     *         // Handle Error
     *     })
     *}
     * ```
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param observer Will receive the not null error when the actual value has the ERROR status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.error
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun error(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend (Throwable) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                WrapObserver<Throwable, Any>(observer = observer),
                single,
                EventDataStatus.DOESNT_MATTER
            )
        )
        return this
    }

    /**
     * Observes when the DataResult has the Error Status and have error
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     error(single = true /* default - false */, withData = false, observer = { error ->
     *         // Handle Error
     *     })
     *}
     * ```
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param withData If true, will execute only with the status ERROR and with NonNull data
     * @param observer Will receive the not null error when the actual value has the ERROR status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.error
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun error(
        @NonNull single: Boolean = false,
        @NonNull withData: Boolean,
        @NonNull observer: suspend (Throwable) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                WrapObserver<Throwable, Any>(observer = observer),
                single,
                getEventDataStatus(withData)
            )
        )
        return this
    }

    /**
     * Observes when the DataResult has the Error Status and have error
     *
     * ```kotlin
     * fun transform(error: Throwable): String = error.message
     *
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     error(
     *         single = true /* default - false */,
     *        transformer = ::transform,
     *        observer = { transformedError ->
     *             // Handle Transformed Error
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param transformer Transform the Throwable into R before deliver it to the observer
     * @param observer Will receive the not null transformed error when the actual value has the ERROR status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.error
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun <R> error(
        @NonNull single: Boolean = false,
        @NonNull transformer: suspend (Throwable) -> R,
        @NonNull observer: suspend (R) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                WrapObserver(
                    transformer = transformer,
                    transformerObserver = observer
                ), single, EventDataStatus.DOESNT_MATTER
            )
        )
        return this
    }

    /**
     * Observes when the DataResult has the Error Status and have error
     *
     * ```kotlin
     * fun transform(error: Throwable): String = error.message
     *
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     error(
     *         single = true /* default - false */,
     *        withData = false,
     *        transformer = ::transform,
     *        observer = { transformedError ->
     *             // Handle Transformed Error
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param withData If true, will execute only with the status ERROR and with NonNull data
     * @param transformer Transform the Throwable into R before deliver it to the observer
     * @param observer Will receive the not null transformed error when the actual value has the ERROR status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.error
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun <R> error(
        @NonNull single: Boolean = false,
        @NonNull withData: Boolean,
        @NonNull transformer: suspend (Throwable) -> R,
        @NonNull observer: suspend (R) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                WrapObserver(
                    transformer = transformer,
                    transformerObserver = observer
                ), single, getEventDataStatus(withData)
            )
        )
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
    fun success(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            SuccessEvent(
                WrapObserver<Void, Any>(emptyObserver = observer),
                single,
                EventDataStatus.DOESNT_MATTER
            )
        )
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
    fun success(
        @NonNull single: Boolean = false,
        @NonNull withData: Boolean,
        @NonNull observer: suspend () -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            SuccessEvent(
                WrapObserver<Void, Any>(emptyObserver = observer),
                single,
                getEventDataStatus(withData)
            )
        )
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
    fun data(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend (T) -> Unit
    ): ObserveWrapper<T> {
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
    fun <R> data(
        @NonNull single: Boolean = false,
        @NonNull transformer: suspend (T) -> R,
        @NonNull observer: suspend (R) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            DataEvent(
                WrapObserver(
                    transformer = transformer,
                    transformerObserver = observer
                ), single
            )
        )
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
    fun result(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend (DataResult<T>) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            ResultEvent(
                WrapObserver<DataResult<T>, Any>(observer = observer),
                single
            )
        )
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
    fun <R> result(
        @NonNull single: Boolean = false,
        @NonNull transformer: suspend (DataResult<T>) -> R,
        @NonNull observer: suspend (R) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            ResultEvent(
                WrapObserver(
                    transformer = transformer,
                    transformerObserver = observer
                ), single
            )
        )
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
    fun result(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            ResultEvent(
                WrapObserver<DataResult<T>, Any>(emptyObserver = observer),
                single
            )
        )
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
    fun status(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend (DataResultStatus) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            StatusEvent(
                WrapObserver<DataResultStatus, Any>(observer = observer),
                single
            )
        )
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
    fun <R> status(
        @NonNull single: Boolean = false,
        @NonNull transformer: suspend (DataResultStatus) -> R,
        @NonNull observer: suspend (R) -> Unit
    ): ObserveWrapper<T> {
        eventList.add(
            StatusEvent(
                WrapObserver(
                    transformer = transformer,
                    transformerObserver = observer
                ), single
            )
        )
        return this
    }
    //endregion

    //region Attach Methods
    /**
     * Observes until all observers on Wrapper get removed
     *
     * @param owner The desired Owner to observe
     *
     * @return The ResponseLiveData<T> attached to the Wrapper
     */
    @NonNull
    internal fun attachTo(
        @NonNull liveData: ResponseLiveData<T>,
        @NonNull owner: LifecycleOwner
    ): ResponseLiveData<T> {
        liveData.observeUntil(owner) {
            scope.launchWithErrorTreatment { handleResult(it) }

            // Observe until this list becomes empty
            eventList.isEmpty()
        }
        return liveData
    }

    /**
     * Attach this wrapper into a flow
     *
     * @param flow The desired Flow to observe
     *
     * @return The Flow<DataResult<T>> attached to the Wrapper
     */
    @NonNull
    internal fun attachTo(@NonNull flow: ResponseFlow<T>): ResponseFlow<T> {
        scope.launchWithErrorTreatment { flow.collect(::handleResult) }
        return flow
    }

    /**
     * Attach this wrapper into a DataResult Instance
     *
     * @param dataResult The desired Data to observe
     *
     * @return The DataResult<T> attached to the Wrapper
     */
    @NonNull
    internal fun attachTo(@NonNull dataResult: DataResult<T>): DataResult<T> {
        scope.launchWithErrorTreatment { handleResult(dataResult) }
        return dataResult
    }
    //endregion

    private suspend fun handleResult(@Nullable result: DataResult<T>?) {

        if (result == null) return
        val isLoading = result.status == LOADING

        eventList.iterate(result) { event ->

            when {

                // Handle None
                result.status == NONE -> {
                    (event as? NoneEvent)?.wrapper?.handle(null, transformDispatcher)
                    return@iterate event is NoneEvent
                }

                // Handle Loading
                event is LoadingEvent -> event.run {
                    wrapper.handle(isLoading, transformDispatcher)
                    return@run isLoading.not()
                }

                // Handle ShowLoading
                event is ShowLoadingEvent && isLoading -> event.run {
                    wrapper.handle(true, transformDispatcher)
                    return@run true
                }

                // Handle HideLoading
                event is HideLoadingEvent && isLoading.not() -> event.run {
                    wrapper.handle(isLoading, transformDispatcher)
                    return@run true
                }

                // Handle Error
                event is ErrorEvent && result.status == ERROR -> event.run {
                    wrapper.handle(result.error, transformDispatcher)
                    return@run true
                }

                // Handle Success
                event is SuccessEvent && result.status == SUCCESS -> event.run {
                    wrapper.handle(null, transformDispatcher)
                    return@run true
                }

                // Handle Data
                event is DataEvent -> (event as DataEvent<T>).wrapper.let {
                    it.handle(result.data, transformDispatcher)
                    return@let result.data != null
                }

                // Handle Empty
                event is EmptyEvent && result.isListType && result.isEmpty -> event.run {
                    wrapper.handle(null, transformDispatcher)
                    return@run true
                }

                // Handle Not Empty
                event is NotEmptyEvent && result.isListType && result.isNotEmpty -> event.run {
                    wrapper.handle(null, transformDispatcher)
                    return@run true
                }

                // Handle Result
                event is ResultEvent<*> -> (event as ResultEvent<T>).run {
                    wrapper.handle(result, transformDispatcher)
                    return@run true
                }

                // Handle Status
                event is StatusEvent -> event.run {
                    wrapper.handle(result.status, transformDispatcher)
                    return@run true
                }

                else -> return@iterate false
            }
        }
    }

    private fun handleEventDataStatus(
        @NonNull dataStatus: EventDataStatus,
        @NonNull result: DataResult<*>
    ): Boolean {
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

    private suspend inline fun MutableList<ObserveEvent<*>>.iterate(
        @NonNull result: DataResult<*>,
        @NonNull crossinline onEach: suspend (ObserveEvent<*>) -> Boolean
    ) {
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

    private fun CoroutineScope.launchWithErrorTreatment(func: suspend () -> Unit) {
        launch(scopeUncaughtError) { func.invoke() }
    }

    private inner class DataResultUncaughtExceptionHandler : UncaughtExceptionHandler {
        override fun uncaughtException(thread: Thread, uncaughtError: Throwable) {
            when (uncaughtError) {
                is DataResultException,
                is DataResultTransformationException -> throw uncaughtError
            }

            when (val cause = uncaughtError.cause) {
                is DataResultException,
                is DataResultTransformationException -> throw cause
            }

            if (eventList.none { it is ErrorEvent }) {
                throw DataResultException(
                    "Any error event found, please add one error { ... } to retry",
                    uncaughtError
                )
            }

            scope.launch(scopeUncaughtError) {
                kotlin.runCatching {
                    handleResult(dataResultError(uncaughtError))
                }.onFailure {
                    throw DataResultException(
                        "Error retried but without any success",
                        uncaughtError
                    )
                }
            }
        }
    }
}

private class WrapObserver<T, V>(
    @Nullable val observer: (suspend (T) -> Unit)? = null,
    @Nullable val emptyObserver: (suspend () -> Unit)? = null,
    @Nullable val transformer: (suspend (T) -> V)? = null,
    @Nullable val transformerObserver: (suspend (V) -> Unit)? = null
) {

    suspend fun handle(@Nullable data: T?, dispatcher: CoroutineDispatcher) {
        emptyObserver?.invoke()
        data?.also {
            observer?.invoke(data)
            executeTransformer(data, dispatcher)
        }
    }

    private suspend fun executeTransformer(@Nullable data: T, dispatcher: CoroutineDispatcher) {
        if (transformerObserver == null) return
        if (transformer == null) return

        val result = withContext(dispatcher) {
            transformer.runCatching { invoke(data) }
        }

        val catch = CoroutineExceptionHandler { _, error -> throw error }
        withContext(currentCoroutineContext() + catch) {
            result.onSuccess {
                transformerObserver.invoke(it)
            }.onFailure {
                throw DataResultTransformationException("Error performing transformation", it)
            }
        }
    }
}

private enum class EventDataStatus { WITH_DATA, WITHOUT_DATA, DOESNT_MATTER }

private sealed class ObserveEvent<T>(
    @NonNull val wrapper: WrapObserver<T, *>,
    @NonNull val single: Boolean,
    @NonNull val dataStatus: EventDataStatus
)

private class LoadingEvent(
    @NonNull observer: suspend (Boolean) -> Unit,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus
) : ObserveEvent<Boolean>(WrapObserver<Boolean, Any>(observer), single, dataStatus)

private class ShowLoadingEvent(
    @NonNull observer: suspend () -> Unit,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus
) : ObserveEvent<Boolean>(
    WrapObserver<Boolean, Any>(emptyObserver = observer),
    single,
    dataStatus
)

private class HideLoadingEvent(
    @NonNull observer: suspend () -> Unit,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus
) : ObserveEvent<Boolean>(
    WrapObserver<Boolean, Any>(emptyObserver = observer),
    single,
    dataStatus
)

private class ErrorEvent(
    @NonNull wrapper: WrapObserver<Throwable, *>,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus
) : ObserveEvent<Throwable>(wrapper, single, dataStatus)

private class SuccessEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus
) : ObserveEvent<Void>(wrapper, single, dataStatus)

private class DataEvent<T>(
    @NonNull wrapper: WrapObserver<T, *>,
    @NonNull single: Boolean
) : ObserveEvent<T>(wrapper, single, EventDataStatus.DOESNT_MATTER)

private class ResultEvent<T>(
    @NonNull wrapper: WrapObserver<DataResult<T>, *>,
    @NonNull single: Boolean
) : ObserveEvent<DataResult<T>>(wrapper, single, EventDataStatus.DOESNT_MATTER)

private class StatusEvent(
    @NonNull wrapper: WrapObserver<DataResultStatus, *>,
    @NonNull single: Boolean
) : ObserveEvent<DataResultStatus>(wrapper, single, EventDataStatus.DOESNT_MATTER)

private class NoneEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean
) : ObserveEvent<Void>(wrapper, single, EventDataStatus.DOESNT_MATTER)

private class EmptyEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean
) : ObserveEvent<Void>(wrapper, single, EventDataStatus.DOESNT_MATTER)

private class NotEmptyEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean
) : ObserveEvent<Void>(wrapper, single, EventDataStatus.DOESNT_MATTER)
