package br.com.arch.toolkit.result

import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.livedata.ResponseLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * The capsule with contains a optional data, optional error and a non null status
 *
 * This model of interpretation was based on Google Architecture Components Example
 *
 * @see <a href="https://github.com/googlesamples/android-architecture-components">Google's github repository</a>
 * @see DataResultStatus
 * @see ObserveWrapper
 * @see ResponseLiveData
 * @see ResponseFlow
 */
data class DataResult<T>(
    val data: T?,
    val error: Throwable?,
    val status: DataResultStatus
) {

    /**
     * Scope to run all the transformations, it is optional, is null, will use the default inside ObserveWrapper
     *
     * @see ObserveWrapper.scope
     */
    private var scope: CoroutineScope? = null
    fun scope(scope: CoroutineScope) = apply { this.scope = scope }

    /**
     * Scope to run all the transformations, it is optional, is null, will use the default inside ObserveWrapper
     *
     * @see ObserveWrapper.transformDispatcher
     */
    private var transformDispatcher: CoroutineDispatcher? = null
    fun transformDispatcher(dispatcher: CoroutineDispatcher) =
        apply { this.transformDispatcher = dispatcher }

    /**
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * if(dataResult.hasData) {
     *     // Have data!
     * } else {
     *     // Not have data!
     * }
     * ```
     *
     * - **true** - if this DataResult has data != null
     * - **false** - if this DataResult has data == null
     *
     * @return Flag indicating if this DataResult has data or not
     */
    val hasData: Boolean get() = data != null

    /**
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * if(dataResult.hasError) {
     *     // Have data!
     * } else {
     *     // Not have data!
     * }
     * ```
     *
     * - **true** - if this DataResult has error != null
     * - **false** - if this DataResult has error == null
     *
     * @return Flag indicating if this DataResult has error or not
     */
    val hasError: Boolean get() = error != null

    /**
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * if(dataResult.isLoading) {
     *     // Is Loading!
     * } else {
     *     // Is Not Loading
     * }
     * ```
     *
     * - **true** - if this DataResult has loading status
     * - **false** - if this DataResult hasn't loading status
     *
     * @return Flag indicating if this DataResult has loading status
     * @see DataResultStatus
     */
    val isLoading: Boolean get() = status == DataResultStatus.LOADING

    /**
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * if(dataResult.isError) {
     *     // Is Error!
     * } else {
     *     // Is Not Error
     * }
     * ```
     *
     * - **true** - if this DataResult has error status
     * - **false** - if this DataResult hasn't error status
     *
     * @return Flag indicating if this DataResult has error status
     * @see DataResultStatus
     */
    val isError: Boolean get() = status == DataResultStatus.ERROR

    /**
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * if(dataResult.isSuccess) {
     *     // Is Success!
     * } else {
     *     // Is Not Success
     * }
     * ```
     *
     * - **true** - if this DataResult has success status
     * - **false** - if this DataResult hasn't success status
     *
     * @return Flag indicating if this DataResult has success status
     * @see DataResultStatus
     */
    val isSuccess: Boolean get() = status == DataResultStatus.SUCCESS

    /**
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * if(dataResult.isNone) {
     *     // Is None!
     * } else {
     *     // Is Not None
     * }
     * ```
     *
     * - **true** - if this DataResult has none status
     * - **false** - if this DataResult hasn't none status
     *
     * @return Flag indicating if this DataResult has none status
     * @see DataResultStatus
     */
    val isNone: Boolean get() = status == DataResultStatus.NONE

    /**
     * Creates and configure a ObserverWrapper to handle by yourself any changes on this data
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * dataResult.unwrap {
     *    // See what you can do inside ObserveWrapper
     *    data { data -> /* Work with data here */ }
     *    loading { loading -> /* Work with loading here */ }
     *    showLoading { /* Display loading here */ }
     *    hideLoading { /* Hide loading here */ }
     *    error { error -> /* Work with error here */ }
     *    status { status -> /* Work with status here */ }
     * }
     * ```
     *
     * @see ObserveWrapper
     */
    fun unwrap(config: ObserveWrapper<T>.() -> Unit) =
        ObserveWrapper<T>().also {
            scope?.let(it::scope)
            transformDispatcher?.let(it::transformDispatcher)
        }.apply(config).attachTo(this)

    //region Data
    /**
     * Function that will run only if this DataResult has non-null data
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * dataResult.data { data ->
     *    /* work with data here */
     * }
     * ```
     * @param func Function that receives data
     * @see DataResult.unwrap
     * @see ObserveWrapper.data
     */
    fun data(func: (T) -> Unit) = unwrap { data(observer = func) }

    /**
     * Function that will run only if this DataResult has non-null data
     *
     * Usage:
     * ```kotlin
     * fun transform(data: T): R {
     *     return "transformed data"
     * }
     *
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * dataResult.data(transformer = ::transform) { transformed ->
     *     /* work with transformed data here */
     * }
     * ```
     * @param transformer Function that receives data and return the transformed data
     * @param func Function that receives transformed data
     * @see DataResult.unwrap
     * @see ObserveWrapper.data
     */
    fun <R> data(transformer: (T) -> R, func: (R) -> Unit) =
        unwrap { data(transformer = transformer, observer = func) }
    //endregion

    //region Loading
    /**
     * Function that will run based on this DataResult status
     *
     * Will receive true if the status is LOADING, and false otherwise
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * dataResult.loading { loading ->
     *     if(loading) {
     *         /* Display Loading here */
     *     else {
     *         /* Hide Loading here */
     *     }
     * }
     * ```
     * @param func Function that receives a bool that indicates to display/hide loading
     * @see DataResult.unwrap
     * @see ObserveWrapper.loading
     */
    fun loading(func: (Boolean) -> Unit) = unwrap { loading(observer = func) }

    /**
     * Function that will run only if this DataResult status is LOADING
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * dataResult.showLoading {
     *     /* Display Loading here */
     * }
     * ```
     * @param func Function that will execute only if the status is LOADING
     * @see DataResult.unwrap
     * @see ObserveWrapper.showLoading
     */
    fun showLoading(func: () -> Unit) = unwrap { showLoading(observer = func) }

    /**
     * Function that will run only if this DataResult status is not LOADING
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * dataResult.hideLoading {
     *     /* Hide Loading here */
     * }
     * ```
     * @param func Function that will execute only if the status is not LOADING
     * @see DataResult.unwrap
     * @see ObserveWrapper.hideLoading
     */
    fun hideLoading(func: () -> Unit) = unwrap { hideLoading(observer = func) }
    //endregion

    //region Error
    /**
     * Function that will run only if this DataResult has non-null error
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * dataResult.error { error ->
     *    /* work with error here */
     * }
     * ```
     * @param func Function that receives error
     * @see DataResult.unwrap
     * @see ObserveWrapper.error
     */
    fun error(func: (Throwable) -> Unit) = unwrap { error(observer = func) }

    /**
     * Function that will run only if this DataResult has status ERROR
     *
     * Usage:
     * ```kotlin
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * dataResult.error {
     *    /* work here in case of error */
     * }
     * ```
     * @param func Function that runs in case of status ERROR
     * @see DataResult.unwrap
     * @see ObserveWrapper.error
     */
    fun error(func: () -> Unit) = unwrap { error(observer = func) }

    /**
     * Function that will run only if this DataResult has non-null error
     *
     * Usage:
     * ```kotlin
     * fun transform(error: Throwable): R {
     *     return "transformed data"
     * }
     *
     * val dataResult = DataResult(null, null, DataResultStatus.SUCCESS)
     * dataResult.error(transformer = ::transform) { transformed ->
     *     /* work with transformed error here */
     * }
     * ```
     * @param transformer Function that receives error and return the transformed data
     * @param func Function that receives transformed error
     * @see DataResult.unwrap
     * @see ObserveWrapper.error
     */
    fun <R> error(transformer: (Throwable) -> R, func: (R) -> Unit) =
        unwrap { error(transformer = transformer, observer = func) }
    //endregion
}
