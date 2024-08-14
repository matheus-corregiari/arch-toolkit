@file:Suppress("TooManyFunctions")

package br.com.arch.toolkit.result

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * DataResult is a generic class that encapsulates the result of an operation, including the data, error, and status.
 * It provides various utility methods to handle and transform the data, as well as to check the state of the result.
 *
 * @param T The type of data held by this DataResult.
 * @property data The data resulting from the operation, or null if there was an error.
 * @property error The exception thrown during the operation, or null if the operation was successful.
 * @property status The current status of the operation, represented by the [DataResultStatus] enum.
 *
 * Example usage:
 * ```
 * val result: DataResult<String> = DataResult("Success", null, DataResultStatus.SUCCESS)
 * if (result.isSuccess) {
 *     println("Data: ${result.data}")
 * }
 * ```
 */
data class DataResult<T>(
    val data: T?,
    val error: Throwable?,
    val status: DataResultStatus
) {

    private var scope: CoroutineScope? = null
    private var transformDispatcher: CoroutineDispatcher? = null

    /**
     * Sets a CoroutineScope to be used by this DataResult.
     *
     * @param scope The CoroutineScope to set.
     * @return This DataResult instance.
     *
     * Example usage:
     * ```
     * val result = DataResult("Success", null, DataResultStatus.SUCCESS)
     * result.scope(CoroutineScope(Dispatchers.Main))
     * ```
     */
    fun scope(scope: CoroutineScope) = apply { this.scope = scope }

    /**
     * Sets a CoroutineDispatcher to be used by this DataResult to create a CoroutineScope.
     *
     * @param scope The CoroutineDispatcher to set.
     * @return This DataResult instance.
     *
     * Example usage:
     * ```
     * val result = DataResult("Success", null, DataResultStatus.SUCCESS)
     * result.scope(Dispatchers.IO)
     * ```
     */
    fun scope(scope: CoroutineDispatcher) = apply { this.scope = CoroutineScope(scope) }

    /**
     * Sets a CoroutineDispatcher to be used for transforming data.
     *
     * @param dispatcher The CoroutineDispatcher to set.
     * @return This DataResult instance.
     *
     * Example usage:
     * ```
     * val result = DataResult("Success", null, DataResultStatus.SUCCESS)
     * result.transformDispatcher(Dispatchers.Default)
     * ```
     */
    fun transformDispatcher(dispatcher: CoroutineDispatcher) =
        apply { this.transformDispatcher = dispatcher }

    /**
     * Checks if the data is not null.
     *
     * @return True if the data is not null, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult("Success", null, DataResultStatus.SUCCESS)
     * if (result.hasData) {
     *     println("Data is present.")
     * }
     * ```
     */
    val hasData: Boolean get() = data != null

    /**
     * Checks if an error is present.
     *
     * @return True if an error is present, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, RuntimeException("An error occurred"), DataResultStatus.ERROR)
     * if (result.hasError) {
     *     println("Error: ${result.error?.message}")
     * }
     * ```
     */
    val hasError: Boolean get() = error != null

    /**
     * Checks if the data is empty. This is applicable if the data is a Collection, Map, or Sequence.
     *
     * @return True if the data is empty, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult(listOf<String>(), null, DataResultStatus.SUCCESS)
     * if (result.isEmpty) {
     *     println("Data is empty.")
     * }
     * ```
     */
    val isEmpty: Boolean
        get() = when (data) {
            is Collection<*> -> data.isEmpty()
            is Map<*, *> -> data.isEmpty()
            is Sequence<*> -> data.count() == 0
            else -> false
        }

    /**
     * Checks if the data is not empty. This is applicable if the data is a Collection, Map, or Sequence.
     *
     * @return True if the data is not empty, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult(listOf("Item1", "Item2"), null, DataResultStatus.SUCCESS)
     * if (result.isNotEmpty) {
     *     println("Data is not empty.")
     * }
     * ```
     */
    val isNotEmpty: Boolean
        get() = when (data) {
            is Collection<*> -> data.isNotEmpty()
            is Map<*, *> -> data.isNotEmpty()
            is Sequence<*> -> data.count() > 0
            else -> false
        }

    /**
     * Checks if the data contains exactly one item. This is applicable if the data is a Collection, Map, or Sequence.
     *
     * @return True if the data contains exactly one item, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult(listOf("OnlyItem"), null, DataResultStatus.SUCCESS)
     * if (result.hasOneItem) {
     *     println("Data contains one item.")
     * }
     * ```
     */
    val hasOneItem: Boolean
        get() = when (data) {
            is Collection<*> -> data.size == 1
            is Map<*, *> -> data.size == 1
            is Sequence<*> -> data.count() == 1
            else -> false
        }

    /**
     * Checks if the data contains more than one item. This is applicable if the data is a Collection, Map, or Sequence.
     *
     * @return True if the data contains more than one item, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult(listOf("Item1", "Item2"), null, DataResultStatus.SUCCESS)
     * if (result.hasManyItems) {
     *     println("Data contains multiple items.")
     * }
     * ```
     */
    val hasManyItems: Boolean
        get() = when (data) {
            is Collection<*> -> data.size > 1
            is Map<*, *> -> data.size > 1
            is Sequence<*> -> data.count() > 1
            else -> false
        }

    /**
     * Checks if the data is of a list type (Collection, Map, or Sequence).
     *
     * @return True if the data is of a list type, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult(listOf("Item1", "Item2"), null, DataResultStatus.SUCCESS)
     * if (result.isListType) {
     *     println("Data is of a list type.")
     * }
     * ```
     */
    val isListType: Boolean
        get() = hasData && when (data) {
            is Collection<*>,
            is Map<*, *>,
            is Sequence<*> -> true

            else -> false
        }

    /**
     * Checks if the current status is LOADING.
     *
     * @return True if the status is LOADING, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, null, DataResultStatus.LOADING)
     * if (result.isLoading) {
     *     println("The operation is loading.")
     * }
     * ```
     */
    val isLoading: Boolean get() = status == DataResultStatus.LOADING

    /**
     * Checks if the current status is ERROR.
     *
     * @return True if the status is ERROR, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, RuntimeException("An error occurred"), DataResultStatus.ERROR)
     * if (result.isError) {
     *     println("The operation encountered an error.")
     * }
     * ```
     */
    val isError: Boolean get() = status == DataResultStatus.ERROR

    /**
     * Checks if the current status is SUCCESS.
     *
     * @return True if the status is SUCCESS, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult("Success", null, DataResultStatus.SUCCESS)
     * if (result.isSuccess) {
     *     println("The operation was successful.")
     * }
     * ```
     */
    val isSuccess: Boolean get() = status == DataResultStatus.SUCCESS

    /**
     * Checks if the current status is NONE.
     *
     * @return True if the status is NONE, false otherwise.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, null, DataResultStatus.NONE)
     * if (result.isNone) {
     *     println("The operation has no status.")
     * }
     * ```
     */
    val isNone: Boolean get() = status == DataResultStatus.NONE

    /**
     * Transforms the data using the provided function and returns a new [DataResult] with the transformed data.
     *
     * @param R The type of the transformed data.
     * @param transform The function to transform the data.
     * @return A new [DataResult] containing the transformed data, or the original error and status if the transformation fails.
     *
     * Example usage:
     * ```
     * val result = DataResult(10, null, DataResultStatus.SUCCESS)
     * val transformedResult = result.transform { it * 2 }
     * println(transformedResult.data) // Output: 20
     * ```
     */
    fun <R> transform(transform: (T) -> R): DataResult<R> = data?.runCatching {
        DataResult(transform(this), error, status)
    }?.getOrElse { error ->
        DataResult<R>(null, error, status)
    } ?: DataResult(null, error, status)

    /**
     * Unwraps the DataResult, applying the provided configuration and attaching the current DataResult.
     *
     * @param config The configuration to apply using [ObserveWrapper].
     * @return The configured [ObserveWrapper] instance.
     *
     * Example usage:
     * ```
     * val result = DataResult("Success", null, DataResultStatus.SUCCESS)
     * result.unwrap {
     *     data { println("Data: $it") }
     *     loading { println("Loading: $it") }
     *     error { println("Error: ${it.message}") }
     * }
     * ```
     */
    fun unwrap(config: ObserveWrapper<T>.() -> Unit) =
        ObserveWrapper<T>().also {
            scope?.let(it::scope)
            transformDispatcher?.let(it::transformDispatcher)
        }.apply(config).attachTo(this)

    //region Data

    /**
     * Executes the provided function if data is present.
     *
     * @param func The function to execute with the data.
     * @return The [ObserveWrapper] instance configured with the data observer.
     *
     * Example usage:
     * ```
     * val result = DataResult("Success", null, DataResultStatus.SUCCESS)
     * result.data { println("Data: $it") }
     * ```
     */
    fun data(func: suspend (T) -> Unit) = unwrap { data(observer = func) }

    /**
     * Transforms the data using the provided transformer function and then executes the provided function with the transformed data.
     *
     * @param R The type of the transformed data.
     * @param transformer The function to transform the data.
     * @param func The function to execute with the transformed data.
     * @return The [ObserveWrapper] instance configured with the data observer.
     *
     * Example usage:
     * ```
     * val result = DataResult(10, null, DataResultStatus.SUCCESS)
     * result.data({ it * 2 }) { println("Transformed Data: $it") } // Output: Transformed Data: 20
     * ```
     */
    fun <R> data(transformer: suspend (T) -> R, func: suspend (R) -> Unit) =
        unwrap { data(transformer = transformer, observer = func) }
    //endregion

    //region Loading

    /**
     * Executes the provided function with the loading state.
     *
     * @param func The function to execute with the loading state.
     * @return The [ObserveWrapper] instance configured with the loading observer.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, null, DataResultStatus.LOADING)
     * result.loading { isLoading -> println("Loading: $isLoading") }
     * ```
     */
    fun loading(func: suspend (Boolean) -> Unit) = unwrap { loading(observer = func) }

    /**
     * Executes the provided function when the loading state is shown.
     *
     * @param func The function to execute when loading is shown.
     * @return The [ObserveWrapper] instance configured with the showLoading observer.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, null, DataResultStatus.LOADING)
     * result.showLoading { println("Loading started...") }
     * ```
     */
    fun showLoading(func: suspend () -> Unit) = unwrap { showLoading(observer = func) }

    /**
     * Executes the provided function when the loading state is hidden.
     *
     * @param func The function to execute when loading is hidden.
     * @return The [ObserveWrapper] instance configured with the hideLoading observer.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, null, DataResultStatus.SUCCESS)
     * result.hideLoading { println("Loading finished.") }
     * ```
     */
    fun hideLoading(func: suspend () -> Unit) = unwrap { hideLoading(observer = func) }
    //endregion

    //region Error

    /**
     * Executes the provided function if an error is present.
     *
     * @param func The function to execute with the error.
     * @return The [ObserveWrapper] instance configured with the error observer.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, RuntimeException("An error occurred"), DataResultStatus.ERROR)
     * result.error { error -> println("Error: ${error.message}") }
     * ```
     */
    fun error(func: suspend (Throwable) -> Unit) = unwrap { error(observer = func) }

    /**
     * Executes the provided function if an error is present (no parameter version).
     *
     * @param func The function to execute if an error is present.
     * @return The [ObserveWrapper] instance configured with the error observer.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, RuntimeException("An error occurred"), DataResultStatus.ERROR)
     * result.error { println("An error occurred.") }
     * ```
     */
    fun error(func: suspend () -> Unit) = unwrap { error(observer = func) }

    /**
     * Transforms the error using the provided transformer function and then executes the provided function with the transformed error.
     *
     * @param R The type of the transformed error.
     * @param transformer The function to transform the error.
     * @param func The function to execute with the transformed error.
     * @return The [ObserveWrapper] instance configured with the error observer.
     *
     * Example usage:
     * ```
     * val result = DataResult<String>(null, RuntimeException("An error occurred"), DataResultStatus.ERROR)
     * result.error({ it.message ?: "Unknown Error" }) { errorMsg -> println("Error: $errorMsg") }
     * ```
     */
    fun <R> error(transformer: suspend (Throwable) -> R, func: suspend (R) -> Unit) =
        unwrap { error(transformer = transformer, observer = func) }
    //endregion
}
