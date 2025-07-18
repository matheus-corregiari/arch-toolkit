@file:Suppress(
    "KotlinNullnessAnnotation",
    "TooManyFunctions",
    "CyclomaticComplexMethod",
    "UNCHECKED_CAST",
)

package br.com.arch.toolkit.result

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import br.com.arch.toolkit.exception.DataResultException
import br.com.arch.toolkit.exception.DataResultTransformationException
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.EventDataStatus.DoesNotMatter
import br.com.arch.toolkit.util.dataResultError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.UncaughtExceptionHandler

/**
 * Wrapper to handle the DataResult`<T>`
 *
 * This is to help all treatment events without any more hell if checks
 *
 * @see DataResult
 * @see ResponseFlow
 */
class ObserveWrapper<T> internal constructor() {
    /**
     * List of all events configured
     */
    internal val eventList = mutableListOf<ObserveEvent<*>>()

    /**
     * Big "catch" to handle unexpected exceptions inside the executions of the events
     */
    private val scopeUncaughtError =
        CoroutineExceptionHandler { _, throwable ->
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
    internal var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        private set

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
     * Observes only the Loading Status, receives true when status is LOADING and false when status sis non-LOADING
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     loading(
     *         single = true /* default - false */,
     *         dataStatus = EventDataStatus.WithData /* default - EventDataStatus.DoesNotMatter */,
     *         observer = { loading ->
     *             if(loading) {
     *                 // Display Loading
     *             } else {
     *                 // Hide Loading
     *             }
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param dataStatus Defines whether this event should be evaluated only when data is present, absent, or ignored. Default: DoesNotMatter
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
        @NonNull dataStatus: EventDataStatus = DoesNotMatter,
        @NonNull observer: suspend (Boolean) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(LoadingEvent(observer, single, dataStatus))
        return this
    }

    /**
     * Observes when the DataResult has the Loading Status
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     showLoading(
     *         single = true /* default - false */,
     *         dataStatus = EventDataStatus.WithData /* default - EventDataStatus.DoesNotMatter */,
     *         observer = {
     *             // Display Loading
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first LOADING status, Default: false
     * @param dataStatus Defines whether this event should be evaluated only when data is present, absent, or ignored. Default: DoesNotMatter
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
        @NonNull dataStatus: EventDataStatus = DoesNotMatter,
        @NonNull observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(ShowLoadingEvent(observer, single, dataStatus))
        return this
    }

    /**
     * Observes when the DataResult does not have the Loading Status
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     hideLoading(
     *         single = true /* default - false */,
     *         dataStatus = EventDataStatus.WithData /* default - EventDataStatus.DoesNotMatter */,
     *         observer = {
     *             // Hide Loading
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-LOADING status, Default: false
     * @param dataStatus Defines whether this event should be evaluated only when data is present, absent, or ignored. Default: DoesNotMatter
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
        @NonNull dataStatus: EventDataStatus = DoesNotMatter,
        @NonNull observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(HideLoadingEvent(observer, single, dataStatus))
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
     *     error(
     *         single = true /* default - false */,
     *         dataStatus = EventDataStatus.WithData /* default - EventDataStatus.DoesNotMatter */,
     *         observer = ::onError
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param dataStatus Defines whether this event should be evaluated only when data is present, absent, or ignored. Default: DoesNotMatter
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
        @NonNull dataStatus: EventDataStatus = DoesNotMatter,
        @NonNull observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                wrapper = WrapObserver<Throwable, Any>(emptyObserver = observer),
                single = single,
                dataStatus = dataStatus,
            ),
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
     *     error(
     *         single = true /* default - false */,
     *         dataStatus = EventDataStatus.WithData /* default - EventDataStatus.DoesNotMatter */,
     *         observer = { error ->
     *             // Handle Error
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param dataStatus Defines whether this event should be evaluated only when data is present, absent, or ignored. Default: DoesNotMatter
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
        @NonNull dataStatus: EventDataStatus = DoesNotMatter,
        @NonNull observer: suspend (Throwable) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                wrapper = WrapObserver<Throwable, Any>(observer = observer),
                single = single,
                dataStatus = dataStatus,
            ),
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
     *         dataStatus = EventDataStatus.WithData /* default - EventDataStatus.DoesNotMatter */,
     *         transformer = ::transform,
     *         observer = { transformedError ->
     *             // Handle Transformed Error
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first ERROR status, Default: false
     * @param dataStatus Defines whether this event should be evaluated only when data is present, absent, or ignored. Default: DoesNotMatter
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
        @NonNull dataStatus: EventDataStatus = DoesNotMatter,
        @NonNull transformer: suspend (Throwable) -> R,
        @NonNull observer: suspend (R) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                wrapper =
                    WrapObserver(
                        transformer = transformer,
                        transformerObserver = observer,
                    ),
                single = single,
                dataStatus = dataStatus,
            ),
        )
        return this
    }
    //endregion

    //region Success

    /**
     * Observes when the DataResult has the Success Status
     *
     * ## Usage:
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     success(
     *         single = true /* default - false */,
     *         dataStatus = EventDataStatus.WithData /* default - EventDataStatus.DoesNotMatter */,
     *         observer = {
     *             // Handle Success
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first SUCCESS status, Default: false
     * @param dataStatus Defines whether this event should be evaluated only when data is present, absent, or ignored. Default: DoesNotMatter
     * @param observer Will be called when the actual value has the SUCCESS status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.success
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun success(
        @NonNull single: Boolean = false,
        @NonNull dataStatus: EventDataStatus = DoesNotMatter,
        @NonNull observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            SuccessEvent(
                wrapper = WrapObserver<Void, Any>(emptyObserver = observer),
                single = single,
                dataStatus = dataStatus,
            ),
        )
        return this
    }
    //endregion

    //region Data

    /**
     * Observes when the DataResult has data
     *
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     data(
     *         single = true /* default - false */,
     *        observer = { data ->
     *             // Handle Data
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first Non null Data, Default: false
     * @param observer Will receive the not null data
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.data
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun data(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend (T) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(DataEvent(WrapObserver<T, Any>(observer = observer), single))
        return this
    }

    /**
     * Observes when the DataResult has data
     *
     * ```kotlin
     * fun transform(data: String): Int = 123
     *
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     data(
     *         single = true /* default - false */,
     *        withData = false,
     *        transformer = ::transform,
     *        observer = { transformedData ->
     *             // Handle Transformed Data
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first Non null Data, Default: false
     * @param transformer Transform the T into R before deliver it to the observer
     * @param observer Will receive the not null transformed data
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.data
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun <R> data(
        @NonNull single: Boolean = false,
        @NonNull transformer: suspend (T) -> R,
        @NonNull observer: suspend (R) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            DataEvent(
                WrapObserver(
                    transformer = transformer,
                    transformerObserver = observer,
                ),
                single,
            ),
        )
        return this
    }
    //endregion

    //region Result

    /**
     * Observes the DataResult
     *
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     result(
     *         single = true /* default - false */,
     *        observer = { result ->
     *             // Handle Result
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-null result, Default: false
     * @param observer Will be called for every result and will receive the not null result
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.result
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun result(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend (DataResult<T>) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            ResultEvent(
                WrapObserver<DataResult<T>, Any>(observer = observer),
                single,
            ),
        )
        return this
    }

    /**
     * Observes the DataResult
     *
     * ```kotlin
     * fun transform(data: DataResult<String>): Int = 123
     *
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     result(
     *         single = true /* default - false */,
     *        transformer = ::transform,
     *        observer = { transformedResult ->
     *             // Handle Transformed Result
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-null result, Default: false
     * @param transformer Transform the T into R before deliver it to the observer
     * @param observer Will be called for every result and will receive the not null transformed result
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.result
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun <R> result(
        @NonNull single: Boolean = false,
        @NonNull transformer: suspend (DataResult<T>) -> R,
        @NonNull observer: suspend (R) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            ResultEvent(
                WrapObserver(
                    transformer = transformer,
                    transformerObserver = observer,
                ),
                single,
            ),
        )
        return this
    }
    //endregion

    //region Status

    /**
     * Observes the Status
     *
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     status(
     *         single = true /* default - false */,
     *        observer = { status ->
     *             // Handle Status
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-null status, Default: false
     * @param observer Will be called for every status and will receive the not null status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.status
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun status(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend (DataResultStatus) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            StatusEvent(
                WrapObserver<DataResultStatus, Any>(observer = observer),
                single,
            ),
        )
        return this
    }

    /**
     * Observes the Status
     *
     * ```kotlin
     * fun transform(data: DataResultStatus): Int = 123
     *
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     status(
     *         single = true /* default - false */,
     *        transformer = ::transform,
     *        observer = { transformedStatus ->
     *             // Handle Transformed Status
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-null status, Default: false
     * @param transformer Transform the T into R before deliver it to the observer
     * @param observer Will be called for every status and will receive the not null transformed status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.status
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun <R> status(
        @NonNull single: Boolean = false,
        @NonNull transformer: suspend (DataResultStatus) -> R,
        @NonNull observer: suspend (R) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            StatusEvent(
                WrapObserver(
                    transformer = transformer,
                    transformerObserver = observer,
                ),
                single,
            ),
        )
        return this
    }
    //endregion

    //region Empty

    /**
     * Observes when the DataResult has data, the type is a "list" and it is empty
     *
     *
     * Types available:
     *  - Collection`<*>`
     *  - Map`<*, *>`
     *  - Sequence`<*>`
     *
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     empty(
     *         single = true /* default - false */,
     *        observer = {
     *             // Handle Empty Data
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-null valid empty data, Default: false
     * @param observer Will be called only if the data is not-null and is represents a valid list type, with empty data
     *
     * @see DataResult
     * @see DataResult.isListType
     * @see DataResult.isEmpty
     * @see DataResult.isNotEmpty
     * @see DataResultStatus
     * @see ObserveWrapper.empty
     * @see ObserveWrapper.notEmpty
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun empty(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            EmptyEvent(
                WrapObserver<Void, Any>(emptyObserver = observer),
                single,
            ),
        )
        return this
    }

    /**
     * Observes when the DataResult has data, the type is a "list" and it is not empty
     *
     *
     * Types available:
     *  - Collection`<*>`
     *  - Map`<*, *>`
     *  - Sequence`<*>`
     *
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     notEmpty(
     *         single = true /* default - false */,
     *        observer = {
     *             // Handle Not Empty Data
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-null valid not empty data, Default: false
     * @param observer Will be called only if the data is not-null and is represents a valid list type,
     * with not empty data
     *
     * @see DataResult
     * @see DataResult.isListType
     * @see DataResult.isEmpty
     * @see DataResult.isNotEmpty
     * @see DataResultStatus
     * @see ObserveWrapper.empty
     * @see ObserveWrapper.notEmpty
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun notEmpty(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            NotEmptyEvent(
                WrapObserver<Void, Any>(emptyObserver = observer),
                single,
            ),
        )
        return this
    }

    /**
     * Observes when the DataResult has data, the type is a "list" and it has one item
     *
     *
     * Types available:
     *  - Collection`<*>`
     *  - Map`<*, *>`
     *  - Sequence`<*>`
     *
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     oneItem(
     *         single = true /* default - false */,
     *        observer = {
     *             // Handle One Item Data
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-null valid one item data, Default: false
     * @param observer Will be called only if the data is not-null and is represents a valid list type,
     * with only one item data
     *
     * @see DataResult
     * @see DataResult.isListType
     * @see DataResult.hasOneItem
     * @see DataResult.hasManyItems
     * @see DataResultStatus
     * @see ObserveWrapper.oneItem
     * @see ObserveWrapper.manyItems
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun oneItem(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            OneItemEvent(
                WrapObserver<Void, Any>(emptyObserver = observer),
                single,
            ),
        )
        return this
    }

    /**
     * Observes when the DataResult has data, the type is a "list" and it has more than one item
     *
     *
     * Types available:
     *  - Collection`<*>`
     *  - Map`<*, *>`
     *  - Sequence`<*>`
     *
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     oneItem(
     *         single = true /* default - false */,
     *        observer = {
     *             // Handle Many Items Data
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-null valid many items data, Default: false
     * @param observer Will be called only if the data is not-null and is represents a valid list type,
     * with more than one item data
     *
     * @see DataResult
     * @see DataResult.isListType
     * @see DataResult.hasOneItem
     * @see DataResult.hasManyItems
     * @see DataResultStatus
     * @see ObserveWrapper.oneItem
     * @see ObserveWrapper.manyItems
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun manyItems(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            ManyItemsEvent(
                WrapObserver<Void, Any>(emptyObserver = observer),
                single,
            ),
        )
        return this
    }
    //endregion

    //region None

    /**
     * Observes when the DataResult has status NONE
     *
     * ```kotlin
     * val dataResult = dataResultSuccess("data")
     * dataResult.unwrap {
     *     none(
     *         single = true /* default - false */,
     *        observer = {
     *             // Handle None event
     *         }
     *     )
     *}
     * ```
     *
     * @param single If true, will execute only until the first non-null valid not empty data, Default: false
     * @param observer Only way to observe a result with status NONE
     *
     * @see DataResult
     * @see DataResult.isNone
     * @see DataResultStatus
     * @see ObserveWrapper.none
     *
     * @return ObserveWrapper`<T>`
     */
    @NonNull
    fun none(
        @NonNull single: Boolean = false,
        @NonNull observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            NoneEvent(
                WrapObserver<Void, Any>(emptyObserver = observer),
                single,
            ),
        )
        return this
    }
    //endregion

    //region Attach Methods

    /**
     * Attach this wrapper into a flow
     *
     * @param flow The desired Flow to observe
     *
     * @return The Flow<DataResult<T>> attached to the Wrapper
     */
    @NonNull
    internal fun attachTo(
        @NonNull flow: Flow<DataResult<T>>,
    ): Flow<DataResult<T>> {
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
    internal fun attachTo(
        @NonNull dataResult: DataResult<T>,
    ): DataResult<T> {
        scope.launchWithErrorTreatment { handleResult(dataResult) }
        return dataResult
    }
    //endregion

    internal suspend fun handleResult(
        @Nullable result: DataResult<T>?,
        evaluateBeforeDispatch: suspend () -> Boolean = { true },
    ) {
        if (result == null) return

        eventList.iterate(result) { event ->
            return@iterate when {
                // Handle None
                result.isNone ->
                    (event as? NoneEvent)?.run {
                        wrapper.handle(null, transformDispatcher, evaluateBeforeDispatch)
                    } == true

                // Handle Loading
                event is LoadingEvent ->
                    event.run {
                        wrapper.handle(
                            result.isLoading,
                            transformDispatcher,
                            evaluateBeforeDispatch,
                        ) &&
                            result.isLoading.not()
                    }

                // Handle ShowLoading
                event is ShowLoadingEvent && result.isLoading ->
                    event.run {
                        wrapper.handle(true, transformDispatcher, evaluateBeforeDispatch)
                    }

                // Handle HideLoading
                event is HideLoadingEvent && result.isLoading.not() ->
                    event.run {
                        wrapper.handle(
                            result.isLoading,
                            transformDispatcher,
                            evaluateBeforeDispatch,
                        )
                    }

                // Handle Error
                event is ErrorEvent && result.isError ->
                    event.run {
                        wrapper.handle(result.error, transformDispatcher, evaluateBeforeDispatch)
                    }

                // Handle Success
                event is SuccessEvent && result.isSuccess ->
                    event.run {
                        wrapper.handle(null, transformDispatcher, evaluateBeforeDispatch)
                    }

                // Handle Data
                event is DataEvent ->
                    (event as DataEvent<T>).wrapper.let {
                        it.handle(
                            result.data,
                            transformDispatcher,
                            evaluateBeforeDispatch,
                        ) &&
                            (result.data != null)
                    }

                // Handle Empty
                event is EmptyEvent && result.isListType && result.isEmpty ->
                    event.run {
                        wrapper.handle(null, transformDispatcher, evaluateBeforeDispatch)
                    }

                // Handle Not Empty
                event is NotEmptyEvent && result.isListType && result.isNotEmpty ->
                    event.run {
                        wrapper.handle(null, transformDispatcher, evaluateBeforeDispatch)
                    }

                // Handle One Item
                event is OneItemEvent && result.isListType && result.hasOneItem ->
                    event.run {
                        wrapper.handle(null, transformDispatcher, evaluateBeforeDispatch)
                    }

                // Handle Many Items
                event is ManyItemsEvent && result.isListType && result.hasManyItems ->
                    event.run {
                        wrapper.handle(null, transformDispatcher, evaluateBeforeDispatch)
                    }

                // Handle Result
                event is ResultEvent<*> ->
                    (event as ResultEvent<T>).run {
                        wrapper.handle(result, transformDispatcher, evaluateBeforeDispatch)
                    }

                // Handle Status
                event is StatusEvent ->
                    event.run {
                        wrapper.handle(result.status, transformDispatcher, evaluateBeforeDispatch)
                    }

                else -> false
            }
        }
    }

    private suspend inline fun MutableList<ObserveEvent<*>>.iterate(
        @NonNull result: DataResult<*>,
        @NonNull crossinline onEach: suspend (ObserveEvent<*>) -> Boolean,
    ) {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val wrapObserver = iterator.next()
            val eventDataStatusHandled = wrapObserver.dataStatus.considerEvent(result)
            val handled = eventDataStatusHandled && onEach.invoke(wrapObserver)
            if (wrapObserver.single && handled) {
                iterator.remove()
            }
        }
    }

    internal fun CoroutineScope.launchWithErrorTreatment(func: suspend () -> Unit) {
        launch(scopeUncaughtError) { func.invoke() }
    }

    private inner class DataResultUncaughtExceptionHandler : UncaughtExceptionHandler {
        override fun uncaughtException(
            thread: Thread,
            uncaughtError: Throwable,
        ) {
            when (uncaughtError) {
                is DataResultException,
                is DataResultTransformationException,
                -> throw uncaughtError
            }

            when (val cause = uncaughtError.cause) {
                is DataResultException,
                is DataResultTransformationException,
                -> throw cause
            }

            if (eventList.none { it is ErrorEvent }) {
                throw DataResultException(
                    "Any error event found, please add one error { ... } to retry",
                    uncaughtError,
                )
            }

            scope.launch(scopeUncaughtError) {
                kotlin
                    .runCatching {
                        handleResult(dataResultError(uncaughtError))
                    }.onFailure {
                        throw DataResultException(
                            "Error retried but without any success",
                            uncaughtError,
                        )
                    }
            }
        }
    }
}

internal class WrapObserver<T, V>(
    @param:Nullable val observer: (suspend (T) -> Unit)? = null,
    @param:Nullable val emptyObserver: (suspend () -> Unit)? = null,
    @param:Nullable val transformer: (suspend (T) -> V)? = null,
    @param:Nullable val transformerObserver: (suspend (V) -> Unit)? = null,
) {
    suspend fun handle(
        @Nullable data: T?,
        dispatcher: CoroutineDispatcher,
        evaluate: suspend () -> Boolean,
    ) = when {
        evaluate.invoke().not() -> false

        emptyObserver != null -> {
            emptyObserver.invoke()
            true
        }

        data != null && observer != null -> {
            observer.invoke(data)
            true
        }

        data != null -> executeTransformer(data, dispatcher, evaluate)

        else -> false
    }

    private suspend fun executeTransformer(
        @Nullable data: T,
        dispatcher: CoroutineDispatcher,
        evaluate: suspend () -> Boolean,
    ) = when {
        transformerObserver == null -> false
        transformer == null -> false
        else -> {
            val result =
                withContext(dispatcher) {
                    transformer.runCatching { invoke(data) }
                }

            val catch = CoroutineExceptionHandler { _, error -> throw error }
            withContext(currentCoroutineContext() + catch) {
                if (evaluate.invoke()) {
                    result
                        .onSuccess {
                            transformerObserver.invoke(it)
                        }.onFailure {
                            throw DataResultTransformationException(
                                "Error performing transformation",
                                it,
                            )
                        }
                    true
                } else {
                    false
                }
            }
        }
    }
}

internal sealed class ObserveEvent<T>(
    @param:NonNull val wrapper: WrapObserver<T, *>,
    @param:NonNull val single: Boolean,
    @param:NonNull val dataStatus: EventDataStatus,
)

private class LoadingEvent(
    @NonNull observer: suspend (Boolean) -> Unit,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus,
) : ObserveEvent<Boolean>(WrapObserver<Boolean, Any>(observer), single, dataStatus)

private class ShowLoadingEvent(
    @NonNull observer: suspend () -> Unit,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus,
) : ObserveEvent<Boolean>(
        WrapObserver<Boolean, Any>(emptyObserver = observer),
        single,
        dataStatus,
    )

private class HideLoadingEvent(
    @NonNull observer: suspend () -> Unit,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus,
) : ObserveEvent<Boolean>(
        WrapObserver<Boolean, Any>(emptyObserver = observer),
        single,
        dataStatus,
    )

private class ErrorEvent(
    @NonNull wrapper: WrapObserver<Throwable, *>,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus,
) : ObserveEvent<Throwable>(wrapper, single, dataStatus)

private class SuccessEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean,
    @NonNull dataStatus: EventDataStatus,
) : ObserveEvent<Void>(wrapper, single, dataStatus)

private class DataEvent<T>(
    @NonNull wrapper: WrapObserver<T, *>,
    @NonNull single: Boolean,
) : ObserveEvent<T>(wrapper, single, DoesNotMatter)

private class ResultEvent<T>(
    @NonNull wrapper: WrapObserver<DataResult<T>, *>,
    @NonNull single: Boolean,
) : ObserveEvent<DataResult<T>>(wrapper, single, DoesNotMatter)

private class StatusEvent(
    @NonNull wrapper: WrapObserver<DataResultStatus, *>,
    @NonNull single: Boolean,
) : ObserveEvent<DataResultStatus>(wrapper, single, DoesNotMatter)

private class NoneEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean,
) : ObserveEvent<Void>(wrapper, single, DoesNotMatter)

private class EmptyEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean,
) : ObserveEvent<Void>(wrapper, single, DoesNotMatter)

private class NotEmptyEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean,
) : ObserveEvent<Void>(wrapper, single, DoesNotMatter)

private class OneItemEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean,
) : ObserveEvent<Void>(wrapper, single, DoesNotMatter)

private class ManyItemsEvent(
    @NonNull wrapper: WrapObserver<Void, *>,
    @NonNull single: Boolean,
) : ObserveEvent<Void>(wrapper, single, DoesNotMatter)
