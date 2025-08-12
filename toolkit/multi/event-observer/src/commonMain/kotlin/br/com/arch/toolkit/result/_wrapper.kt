@file:Suppress(
    "KotlinNullnessAnnotation",
    "TooManyFunctions",
    "CyclomaticComplexMethod",
    "UNCHECKED_CAST",
)

package br.com.arch.toolkit.result

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

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
    private val uncaughtHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is DataResultException, is DataResultTransformationException -> throw throwable
        }

        when (val cause = throwable.cause) {
            is DataResultException, is DataResultTransformationException -> throw cause
        }

        if (eventList.none { it is ErrorEvent }) {
            throw DataResultException(
                message = "Any error event found, please add one error { ... } to retry",
                error = throwable,
            )
        }

        suspendFunc {
            runCatching {
                handleResult(dataResultError(throwable))
            }.onFailure {
                throw DataResultException(
                    message = "Error retried but without any success",
                    error = throwable
                )
            }
        }
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
     * **Default: Dispatchers.Default**
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
    private var transformDispatcher: CoroutineDispatcher = Dispatchers.Default

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
     * @param dataStatus Defines whether this event should be evaluated only when data is present,
     * absent, or ignored. Default: DoesNotMatter
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
    fun loading(
        single: Boolean = false,
        dataStatus: EventDataStatus = DoesNotMatter,
        observer: suspend (Boolean) -> Unit,
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
     * @param dataStatus Defines whether this event should be evaluated only when data is present,
     * absent, or ignored. Default: DoesNotMatter
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
    fun showLoading(
        single: Boolean = false,
        dataStatus: EventDataStatus = DoesNotMatter,
        observer: suspend () -> Unit,
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
     * @param dataStatus Defines whether this event should be evaluated only when data is present,
     * absent, or ignored. Default: DoesNotMatter
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
    fun hideLoading(
        single: Boolean = false,
        dataStatus: EventDataStatus = DoesNotMatter,
        observer: suspend () -> Unit,
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
     * @param dataStatus Defines whether this event should be evaluated only when data is present,
     * absent, or ignored. Default: DoesNotMatter
     * @param observer Will be called when the actual value has the ERROR status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.error
     *
     * @return ObserveWrapper`<T>`
     */
    fun error(
        single: Boolean = false,
        dataStatus: EventDataStatus = DoesNotMatter,
        observer: suspend () -> Unit,
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
     * @param dataStatus Defines whether this event should be evaluated only when data is present,
     * absent, or ignored. Default: DoesNotMatter
     * @param observer Will receive the not null error when the actual value has the ERROR status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.error
     *
     * @return ObserveWrapper`<T>`
     */
    fun error(
        single: Boolean = false,
        dataStatus: EventDataStatus = DoesNotMatter,
        observer: suspend (Throwable) -> Unit,
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
     * @param dataStatus Defines whether this event should be evaluated only when data is present,
     * absent, or ignored. Default: DoesNotMatter
     * @param transformer Transform the Throwable into R before deliver it to the observer
     * @param observer Will receive the not null transformed error when the actual value has the ERROR status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.error
     *
     * @return ObserveWrapper`<T>`
     */
    fun <R> error(
        single: Boolean = false,
        dataStatus: EventDataStatus = DoesNotMatter,
        transformer: suspend (Throwable) -> R,
        observer: suspend (R) -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            ErrorEvent(
                wrapper = WrapObserver(
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
     * @param dataStatus Defines whether this event should be evaluated only when data is present,
     * absent, or ignored. Default: DoesNotMatter
     * @param observer Will be called when the actual value has the SUCCESS status
     *
     * @see DataResult
     * @see DataResultStatus
     * @see ObserveWrapper.success
     *
     * @return ObserveWrapper`<T>`
     */
    fun success(
        single: Boolean = false,
        dataStatus: EventDataStatus = DoesNotMatter,
        observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            SuccessEvent(
                wrapper = WrapObserver<Nothing, Any>(emptyObserver = observer),
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
    fun data(
        single: Boolean = false,
        observer: suspend (T) -> Unit,
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
    fun <R> data(
        single: Boolean = false,
        transformer: suspend (T) -> R,
        observer: suspend (R) -> Unit,
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
    fun result(
        single: Boolean = false,
        observer: suspend (DataResult<T>) -> Unit,
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
    fun <R> result(
        single: Boolean = false,
        transformer: suspend (DataResult<T>) -> R,
        observer: suspend (R) -> Unit,
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
    fun status(
        single: Boolean = false,
        observer: suspend (DataResultStatus) -> Unit,
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
    fun <R> status(
        single: Boolean = false,
        transformer: suspend (DataResultStatus) -> R,
        observer: suspend (R) -> Unit,
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
    fun empty(
        single: Boolean = false,
        observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            EmptyEvent(
                WrapObserver<Nothing, Any>(emptyObserver = observer),
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
    fun notEmpty(
        single: Boolean = false,
        observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            NotEmptyEvent(
                WrapObserver<Nothing, Any>(emptyObserver = observer),
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
    fun oneItem(
        single: Boolean = false,
        observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            OneItemEvent(
                WrapObserver<Nothing, Any>(emptyObserver = observer),
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
    fun manyItems(
        single: Boolean = false,
        observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            ManyItemsEvent(
                WrapObserver<Nothing, Any>(emptyObserver = observer),
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
    fun none(
        single: Boolean = false,
        observer: suspend () -> Unit,
    ): ObserveWrapper<T> {
        eventList.add(
            NoneEvent(
                WrapObserver<Nothing, Any>(emptyObserver = observer),
                single,
            ),
        )
        return this
    }
    //endregion

    //region Attach Methods

    /** MISSING */
    internal fun suspendFunc(func: suspend ObserveWrapper<T>.() -> Unit) =
        scope.launchWithErrorTreatment { func() }

    /**
     * Attach this wrapper into a DataResult Instance
     *
     * @param dataResult The desired Data to observe
     *
     * @return The DataResult<T> attached to the Wrapper
     */
    internal fun attachTo(dataResult: DataResult<T>) =
        apply { suspendFunc { handleResult(dataResult) } }
    //endregion

    @Suppress("LongMethod")
    internal suspend fun handleResult(
        result: DataResult<T>?,
        evaluateBeforeDispatch: suspend () -> Boolean = { true },
    ) {
        if (result == null) return

        eventList.iterate(result) { event ->
            return@iterate when {
                // Handle None
                result.isNone -> (event as? NoneEvent)?.wrapper?.handle(
                    data = null, dispatcher = transformDispatcher, evaluate = evaluateBeforeDispatch
                ) == true

                // Handle Loading
                event is LoadingEvent -> event.wrapper.handle(
                    data = result.isLoading,
                    dispatcher = transformDispatcher,
                    evaluate = evaluateBeforeDispatch,
                ) && result.isLoading.not()

                // Handle ShowLoading
                event is ShowLoadingEvent && result.isLoading -> event.wrapper.handle(
                    data = true,
                    dispatcher = transformDispatcher,
                    evaluate = evaluateBeforeDispatch
                )

                // Handle HideLoading
                event is HideLoadingEvent && result.isLoading.not() -> event.wrapper.handle(
                    data = result.isLoading,
                    dispatcher = transformDispatcher,
                    evaluate = evaluateBeforeDispatch,
                )

                // Handle Error
                event is ErrorEvent && result.isError -> event.wrapper.handle(
                    data = result.error,
                    dispatcher = transformDispatcher,
                    evaluate = evaluateBeforeDispatch
                )

                // Handle Success
                event is SuccessEvent && result.isSuccess -> event.wrapper.handle(
                    data = null,
                    dispatcher = transformDispatcher,
                    evaluate = evaluateBeforeDispatch
                )

                // Handle Data
                event is DataEvent -> (event as DataEvent<T>).wrapper.handle(
                    data = result.data,
                    dispatcher = transformDispatcher,
                    evaluate = evaluateBeforeDispatch,
                ) && (result.data != null)

                // Handle Empty
                event is EmptyEvent && result.isListType && result.isEmpty -> event.wrapper.handle(
                    data = null,
                    dispatcher = transformDispatcher,
                    evaluate = evaluateBeforeDispatch
                )

                // Handle Not Empty
                event is NotEmptyEvent && result.isListType && result.isNotEmpty ->
                    event.wrapper.handle(
                        data = null,
                        dispatcher = transformDispatcher,
                        evaluate = evaluateBeforeDispatch
                    )

                // Handle One Item
                event is OneItemEvent && result.isListType && result.hasOneItem ->
                    event.wrapper.handle(
                        data = null,
                        dispatcher = transformDispatcher,
                        evaluate = evaluateBeforeDispatch
                    )

                // Handle Many Items
                event is ManyItemsEvent && result.isListType && result.hasManyItems ->
                    event.wrapper.handle(
                        data = null,
                        dispatcher = transformDispatcher,
                        evaluate = evaluateBeforeDispatch
                    )

                // Handle Result
                event is ResultEvent<*> -> (event as ResultEvent<T>).wrapper.handle(
                    data = result,
                    dispatcher = transformDispatcher,
                    evaluate = evaluateBeforeDispatch
                )

                // Handle Status
                event is StatusEvent -> event.wrapper.handle(
                    data = result.status,
                    dispatcher = transformDispatcher,
                    evaluate = evaluateBeforeDispatch
                )

                else -> false
            }
        }
    }

    private suspend inline fun MutableList<ObserveEvent<*>>.iterate(
        result: DataResult<*>,
        crossinline onEach: suspend (ObserveEvent<*>) -> Boolean,
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
        fun Result<*>.catch() = onFailure { uncaughtHandler.handleException(coroutineContext, it) }
        runCatching { launch(uncaughtHandler) { runCatching { func() }.catch() } }.catch()
    }
}

internal class WrapObserver<T, V>(
    val observer: (suspend (T) -> Unit)? = null,
    val emptyObserver: (suspend () -> Unit)? = null,
    val transformer: (suspend (T) -> V)? = null,
    val transformerObserver: (suspend (V) -> Unit)? = null,
) {
    suspend fun handle(
        data: T?,
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
        data: T,
        dispatcher: CoroutineDispatcher,
        evaluate: suspend () -> Boolean,
    ) = when {
        transformerObserver == null -> false
        transformer == null -> false
        else -> {
            val result = withContext(dispatcher) {
                transformer.runCatching { invoke(data) }
            }

            val catch = CoroutineExceptionHandler { _, error -> throw error }
            withContext(coroutineContext + catch) {
                if (evaluate.invoke()) {
                    result.onSuccess {
                        transformerObserver.invoke(it)
                    }.onFailure {
                        throw DataResultTransformationException(
                            message = "Error performing transformation",
                            error = it
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
    val wrapper: WrapObserver<T, *>,
    val single: Boolean,
    val dataStatus: EventDataStatus,
)

private class LoadingEvent(
    observer: suspend (Boolean) -> Unit,
    single: Boolean,
    dataStatus: EventDataStatus,
) : ObserveEvent<Boolean>(
    wrapper = WrapObserver<Boolean, Any>(observer),
    single = single,
    dataStatus = dataStatus
)

private class ShowLoadingEvent(
    observer: suspend () -> Unit,
    single: Boolean,
    dataStatus: EventDataStatus,
) : ObserveEvent<Boolean>(
    wrapper = WrapObserver<Boolean, Any>(emptyObserver = observer),
    single = single,
    dataStatus = dataStatus,
)

private class HideLoadingEvent(
    observer: suspend () -> Unit,
    single: Boolean,
    dataStatus: EventDataStatus,
) : ObserveEvent<Boolean>(
    wrapper = WrapObserver<Boolean, Any>(emptyObserver = observer),
    single = single,
    dataStatus = dataStatus,
)

private class ErrorEvent(
    wrapper: WrapObserver<Throwable, *>,
    single: Boolean,
    dataStatus: EventDataStatus,
) : ObserveEvent<Throwable>(wrapper, single, dataStatus)

private class SuccessEvent(
    wrapper: WrapObserver<Nothing, *>,
    single: Boolean,
    dataStatus: EventDataStatus,
) : ObserveEvent<Nothing>(wrapper, single, dataStatus)

private class DataEvent<T>(
    wrapper: WrapObserver<T, *>,
    single: Boolean,
) : ObserveEvent<T>(wrapper, single, DoesNotMatter)

private class ResultEvent<T>(
    wrapper: WrapObserver<DataResult<T>, *>,
    single: Boolean,
) : ObserveEvent<DataResult<T>>(wrapper, single, DoesNotMatter)

private class StatusEvent(
    wrapper: WrapObserver<DataResultStatus, *>,
    single: Boolean,
) : ObserveEvent<DataResultStatus>(wrapper, single, DoesNotMatter)

private class NoneEvent(
    wrapper: WrapObserver<Nothing, *>,
    single: Boolean,
) : ObserveEvent<Nothing>(wrapper, single, DoesNotMatter)

private class EmptyEvent(
    wrapper: WrapObserver<Nothing, *>,
    single: Boolean,
) : ObserveEvent<Nothing>(wrapper, single, DoesNotMatter)

private class NotEmptyEvent(
    wrapper: WrapObserver<Nothing, *>,
    single: Boolean,
) : ObserveEvent<Nothing>(wrapper, single, DoesNotMatter)

private class OneItemEvent(
    wrapper: WrapObserver<Nothing, *>,
    single: Boolean,
) : ObserveEvent<Nothing>(wrapper, single, DoesNotMatter)

private class ManyItemsEvent(
    wrapper: WrapObserver<Nothing, *>,
    single: Boolean,
) : ObserveEvent<Nothing>(wrapper, single, DoesNotMatter)
