@file:Suppress("Filename", "TooManyFunctions", "LongParameterList", "unused")

package br.com.arch.toolkit.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/* region Regular Functions --------------------------------------------------------------------- */
/**
 * Chains this [LiveData] with another [LiveData] based on a condition, returning a pair of values from both sources.
 *
 * @param other A function that returns another [LiveData] based on the value of this [LiveData].
 * The [LiveData] provided by this function will be chained with the current [LiveData] if the condition is met.
 * @param condition A function that determines whether to chain with the other [LiveData]. If this function
 * returns `true`, the [other] function is invoked to provide the [LiveData] to be chained.
 * @return A [LiveData] emitting a [Pair] of values where the first component is from the original [LiveData] and the
 * second component is from the chained [LiveData].
 *
 * This method allows for chaining two [LiveData] sources based on a specified condition and returns a [LiveData]
 * containing a pair of values from both sources. This is particularly useful when you need to combine related data
 * from two different sources and observe the combined result.
 *
 * ### Success Case:
 * - If the `condition` function returns `true`, the [other] function is called to retrieve another [LiveData].
 * - The resulting [LiveData] emits a [Pair] containing the value from the original [LiveData] as the first component,
 * and the value from the chained [LiveData] as the second component.
 * - If either of the values is `null`, the corresponding component in the emitted [Pair] will be `null`.
 *
 * ### Failure Case:
 * - If the `condition` function returns `false`, no chaining occurs, and the original [LiveData] emits its values unchanged.
 * - If the [other] function throws an exception or returns `null`, the chaining process stops, and the original [LiveData]
 *   continues emitting its values without the paired [LiveData].
 * - If the original [LiveData] emits `null`, the [other] function might return `null`, leading to a `Pair(null, null)` being emitted.
 *
 * ### Example Usage:
 * ```
 * // Assuming we have LiveData sources for a user's ID and user details
 * val userIdLiveData: LiveData<Int> = ...
 * val userDetailLiveData: LiveData<Pair<Int?, UserDetail?>> = userIdLiveData.chainWith(
 *     // Function to get user details based on user ID
 *     other = { userId -> getUserDetailLiveData(userId) },
 *     // Condition to chain only for valid user IDs
 *     condition = { userId -> userId != null && userId > 0 }
 * )
 *
 * // Observing the resulting LiveData
 * userDetailLiveData.observe(this, Observer { result ->
 *     val (userId, userDetail) = result
 *     println("User ID: $userId, User Details: $userDetail")
 * })
 * ```
 *
 * ### Error Handling:
 * - If any exceptions are thrown during the [other] function execution, they are caught, and the resulting `LiveData`
 *   will not emit a value for that cycle. Instead, the original [LiveData] continues emitting its values.
 *
 * @param T The type of the value in the original [LiveData].
 * @param R The type of the value in the chained [LiveData].
 */
fun <T, R> LiveData<T>.chainWith(
    other: (T?) -> LiveData<R>,
    condition: (T?) -> Boolean
): LiveData<Pair<T?, R?>> {
    val mediator = MediatorLiveData<Pair<T?, R?>>()
    var bLiveData: LiveData<R>? = null

    fun onBReceived(bValue: R?) = (value to bValue).let(mediator::setValue)

    fun onAReceived(aValue: T?) {
        bLiveData?.let(mediator::removeSource)
        val isConditionMet = condition.runCatching { invoke(aValue) }.getOrDefault(false)
        bLiveData = if (isConditionMet) other.runCatching { invoke(aValue) }.getOrNull() else null
        bLiveData?.let { liveData ->
            if (liveData.isInitialized.not()) mediator.value = aValue to null
            mediator.addSource(liveData, ::onBReceived)
        }
    }

    mediator.addSource(this, ::onAReceived)
    return mediator
}

/**
 * Chains this [LiveData] with another non-nullable [LiveData] based on a condition, emitting pairs of non-nullable values.
 *
 * @param other A function that returns another [LiveData] based on the non-null value of this [LiveData].
 * This function is invoked when the condition is met, and its result is chained with the current [LiveData].
 * @param condition A function that determines whether to chain with the other [LiveData] based on the non-null value of this [LiveData].
 * If the condition evaluates to `true`, the `other` function is called to get the chained [LiveData].
 * @return A [LiveData] emitting pairs of non-nullable values from this [LiveData] and the chained [LiveData].
 *
 * This method is a variant of `chainWith` that operates exclusively with non-nullable values. It ensures that only non-null values
 * are passed to the `other` function and the `condition` function. If this [LiveData] emits a null value, no further actions are taken,
 * and the chaining does not occur. The resulting [LiveData] emits a [Pair] containing values from both [LiveData] sources.
 *
 * ### Success Case:
 * - If this [LiveData] emits a non-null value, the `condition` function is evaluated.
 * - If the `condition` returns `true`, the `other` function is called to get another [LiveData].
 * - The resulting [LiveData] emits a [Pair] containing the non-null value from this [LiveData] and the value from the chained [LiveData].
 * - If both values are non-null, they are combined into a [Pair] and emitted.
 *
 * ### Failure Case:
 * - If this [LiveData] emits a null value, no chaining occurs, and the method does nothing for that emission.
 * - If the `condition` function returns `false`, the chaining is not performed, and the resulting [LiveData] does not emit a value for that emission.
 * - If the `other` function fails or returns null, the resulting [LiveData] does not emit a value for that emission.
 *
 * ### Example Usage:
 * ```
 * // Assuming we have LiveData sources for an order ID and order details
 * val orderIdLiveData: LiveData<Int> = MutableLiveData(123)
 * val orderDetailLiveData: LiveData<OrderDetail> = orderIdLiveData.chainNotNullWith(
 *     // Function to get order details based on order ID
 *     other = { orderId -> getOrderDetailLiveData(orderId) },
 *     // Condition to chain only for valid order IDs
 *     condition = { orderId -> orderId > 0 }
 * )
 *
 * // Observing the resulting LiveData
 * orderDetailLiveData.observe(this, Observer { result ->
 *     val (orderId, orderDetail) = result
 *     println("Order ID: $orderId, Order Details: $orderDetail")
 * })
 * ```
 *
 * ### Error Handling:
 * - If this [LiveData] emits a null value, the method ignores it and takes no further action.
 * - If the `condition` function throws an exception, the exception is caught, and the condition is treated as false, so the chaining does not occur.
 * - If the `other` function throws an exception, it is caught, and no value is emitted by the resulting [LiveData] for that emission.
 *
 * @param T The type of the value in the original [LiveData].
 * @param R The type of the value in the chained [LiveData].
 */
fun <T, R> LiveData<T>.chainNotNullWith(
    other: (T) -> LiveData<R>,
    condition: (T) -> Boolean
): LiveData<Pair<T, R>> {
    val mediator = MediatorLiveData<Pair<T, R>>()
    var bLiveData: LiveData<R>? = null

    fun onBReceived(bValue: R?) = (value to bValue).onlyWithValues()?.let(mediator::setValue)

    fun onAReceived(aValue: T?) {
        bLiveData?.let(mediator::removeSource)
        aValue ?: return
        val isConditionMet = condition.runCatching { invoke(aValue) }.getOrDefault(false)
        bLiveData = if (isConditionMet) other.runCatching { invoke(aValue) }.getOrNull() else null
        bLiveData?.let { liveData -> mediator.addSource(liveData, ::onBReceived) }
    }

    mediator.addSource(this, ::onAReceived)
    return mediator
}
/* endregion ------------------------------------------------------------------------------------ */

/* region Nullable -------------------------------------------------------------------------- */
/**
 * Chains this [LiveData] with another [LiveData] based on a condition, returning a pair of values from both sources.
 *
 * @param context The [CoroutineContext] to use for the coroutine. This context controls the execution of the chaining logic,
 * allowing you to specify which thread or dispatcher should be used. By default, you can use [Dispatchers.IO] for IO-bound tasks
 * or [Dispatchers.Main] for UI-related tasks.
 * @param other A suspend function that returns another [LiveData] based on the value of this [LiveData].
 * The [LiveData] provided by this function will be chained with the current [LiveData] if the condition is met.
 * @param condition A suspend function that determines whether to chain with the other [LiveData]. If this function
 * returns `true`, the [other] function is invoked to provide the [LiveData] to be chained.
 * @return A [LiveData] emitting a [Pair] of values where the first component is from the original [LiveData] and the
 * second component is from the chained [LiveData].
 *
 * This method allows for chaining two [LiveData] sources based on a specified condition and returns a [LiveData]
 * containing a pair of values from both sources. This is particularly useful when you need to combine related data
 * from two different sources and observe the combined result.
 *
 * ### Success Case:
 * - If the `condition` function returns `true`, the [other] function is called to retrieve another [LiveData].
 * - The resulting [LiveData] emits a [Pair] containing the value from the original [LiveData] as the first component,
 * and the value from the chained [LiveData] as the second component.
 * - If either of the values is `null`, the corresponding component in the emitted [Pair] will be `null`.
 *
 * ### Failure Case:
 * - If the `condition` function returns `false`, no chaining occurs, and the original [LiveData] emits its values unchanged.
 * - If the [other] function throws an exception or returns `null`, the chaining process stops, and the original [LiveData]
 *   continues emitting its values without the paired [LiveData].
 * - If the original [LiveData] emits `null`, the [other] function might return `null`, leading to a `Pair(null, null)` being emitted.
 *
 * ### Example Usage:
 * ```
 * // Assuming we have LiveData sources for a user's ID and user details
 * val userIdLiveData: LiveData<Int> = ...
 * val userDetailLiveData: LiveData<Pair<Int?, UserDetail?>> = userIdLiveData.chainWith(
 *     context = Dispatchers.Main,
 *     // Function to get user details based on user ID
 *     other = { userId -> getUserDetailLiveData(userId) },
 *     // Condition to chain only for valid user IDs
 *     condition = { userId -> userId != null && userId > 0 }
 * )
 *
 * // Observing the resulting LiveData
 * userDetailLiveData.observe(this, Observer { result ->
 *     val (userId, userDetail) = result
 *     println("User ID: $userId, User Details: $userDetail")
 * })
 * ```
 *
 * ### Error Handling:
 * - If any exceptions are thrown during the [other] function execution, they are caught, and the resulting `LiveData`
 *   will not emit a value for that cycle. Instead, the original [LiveData] continues emitting its values.
 *
 * @param T The type of the value in the original [LiveData].
 * @param R The type of the value in the chained [LiveData].
 */
fun <T, R> LiveData<T>.chainWith(
    context: CoroutineContext,
    other: suspend (T?) -> LiveData<R>,
    condition: suspend (T?) -> Boolean,
): LiveData<Pair<T?, R?>> =
    liveData(context) { internalChainWith(other, condition).collect(::emit) }

/**
 * Chains this [LiveData] with another [LiveData] based on a condition, using a transformation function.
 *
 * @param context The [CoroutineContext] to use for the coroutine. This context controls the execution of the chaining logic,
 * allowing you to specify which thread or dispatcher should be used. By default, you can use [Dispatchers.IO] for IO-bound tasks
 * or [Dispatchers.Main] for UI-related tasks.
 * @param other A suspend function that returns another [LiveData] based on the value of this [LiveData].
 * The [LiveData] provided by this function will be chained with the current [LiveData] if the condition is met.
 * @param condition A suspend function that determines whether to chain with the other [LiveData]. If this function
 * returns `true`, the [other] function is invoked to provide the [LiveData] to be chained.
 * @param transform A [Transform] object consisting of a [CoroutineDispatcher], a transformation function, a failure mode,
 * and an optional error-handling function.
 * @return A [LiveData] emitting the transformed values as `X?`.
 *
 * This method allows for chaining two [LiveData] sources and applying a transformation function to the combined values.
 * The transformation function is applied in the context provided by the [CoroutineDispatcher] within [Transform],
 * ensuring that the operation is performed on the appropriate thread.
 *
 * The `transform` parameter allows for detailed control over how the transformation is handled:
 * - **dispatcher**: The [CoroutineDispatcher] that defines the thread where the transformation will be executed.
 * - **func**: The suspend function that performs the transformation. It receives the values from the original and
 * chained [LiveData] and produces the transformed output.
 * - **failMode**: Specifies how to handle failures during the transformation:
 *   - [Transform.Mode.OMIT_WHEN_FAIL]: Omits the emission if the transformation fails.
 *   - [Transform.Mode.NULL_WHEN_FAIL]: Emits `null` if the transformation fails.
 * - **onErrorReturn**: An optional suspend function that handles errors during the transformation, returning a fallback value.
 *   If this is provided and the transformation fails, the fallback value will be emitted instead of omitting or emitting `null`.
 *
 * ### Success Case:
 * - If the `condition` function returns `true`, the [other] function is called to retrieve another [LiveData].
 * - The values from the original [LiveData] and the chained [LiveData] are passed to the transformation function.
 * - If the transformation function completes successfully, its result is emitted by the resulting [LiveData].
 *
 * ### Failure Case:
 * - If the `condition` function returns `false`, no chaining occurs, and the current [LiveData] is returned unchanged.
 * - If the [other] function throws an exception or returns `null`, the chaining process stops, and the current [LiveData]
 *   is emitted with no transformation.
 * - If the transformation function throws an exception:
 *   - If `failMode` is [Transform.Mode.OMIT_WHEN_FAIL], the emission is omitted.
 *   - If `failMode` is [Transform.Mode.NULL_WHEN_FAIL], `null` is emitted.
 *   - If an `onErrorReturn` function is provided, its result will be emitted instead of omitting or emitting `null`.
 *
 * ### The `Transform.Nullable` class offers several variations:
 * - **OmitFail**:
 *      - **Description**: Omits the result when the transformation fails, without handling the error.
 *      - **Constructor Parameters**:
 *           - `dispatcher`: The [CoroutineDispatcher] on which the transformation will occur. Default is [Dispatchers.IO].
 *           - `func`: The transformation function to apply to the paired non-nullable values.
 *      - **Example**:
 *   ```kotlin
 *   val transform = Transform.Nullable.OmitFail(
 *       dispatcher = Dispatchers.Default,
 *       func = { intValue, stringValue -> "$intValue: $stringValue" }
 *   )
 *   ```
 * - **NullFail**:
 *      - **Description**: Emits `null` when the transformation fails, without handling the error.
 *      - **Constructor Parameters**:
 *           - `dispatcher`: The [CoroutineDispatcher] on which the transformation will occur. Default is [Dispatchers.IO].
 *           - `func`: The transformation function to apply to the paired nullable values.
 *      - **Example**:
 *   ```kotlin
 *   val transform = Transform.Nullable.NullFail(
 *       dispatcher = Dispatchers.IO,
 *       func = { intValue, stringValue -> "$intValue: $stringValue" }
 *   )
 *   ```
 * - **Fallback**:
 *       - **Description**: Omits the result when the transformation fails but provides a fallback value via `onErrorReturn`.
 *       - **Constructor Parameters**:
 *           - `dispatcher`: The [CoroutineDispatcher] on which the transformation will occur. Default is [Dispatchers.IO].
 *           - `func`: The transformation function to apply to the paired nullable values.
 *           - `onErrorReturn`: A function that handles errors during transformation, allowing you to provide an alternate result.
 *      - **Example**:
 *   ```kotlin
 *   val transform = Transform.Nullable.Fallback(
 *       dispatcher = Dispatchers.IO,
 *       func = { intValue, stringValue -> "$intValue: $stringValue" },
 *       onErrorReturn = { error -> "Error: ${error.message}" }
 *   )
 *   ```
 * - **Custom**:
 *      - **Description**: A customizable transformation that lets you define the dispatcher, fail mode,
 *   transformation function, and optional error handling.
 *      - **Constructor Parameters**:
 *           - `dispatcher`: The [CoroutineDispatcher] on which the transformation will occur.
 *           - `failMode`: Specifies how to handle failures during transformation. Options include:
 *                - `OMIT_WHEN_FAIL`: Omits the emission if the transformation fails.
 *                - `NULL_WHEN_FAIL`: Emits `null` if the transformation fails.
 *           - `func`: The transformation function to apply to the paired nullable values.
 *           - `onErrorReturn`: An optional function to handle errors during transformation.
 *      - **Example**:
 *   ```kotlin
 *   val transform = Transform.Nullable.Custom(
 *       dispatcher = Dispatchers.Default,
 *       failMode = Transform.Mode.OMIT_WHEN_FAIL,
 *       func = { intValue, stringValue -> "$intValue: $stringValue" },
 *       onErrorReturn = { error -> "Error: ${error.message}" }
 *   )
 *   ```
 *
 * ### Example Usage:
 * ```
 * // Assuming we have LiveData sources for a user's ID and user details
 * val userIdLiveData: LiveData<Int> = ...
 * val userDetailLiveData: LiveData<UserDetail> = userIdLiveData.chainWith(
 *     context = Dispatchers.Main,
 *     // Function to get user details based on user ID
 *     other = { userId -> getUserDetailLiveData(userId) },
 *     // Condition to chain only for valid user IDs
 *     condition = { userId -> userId != null && userId > 0 },
 *     // Transform user details to a formatted string
 *     transform = Transform.Nullable.OmitFail(
 *         dispatcher = Dispatchers.IO,
 *         func = { userId, userDetail -> userDetail?.let { "${it.name}, ID: $userId" } }
 *     )
 * )
 *
 * // Observing the resulting LiveData
 * userDetailLiveData.observe(this, Observer { result ->
 *     println("User Info: $result")
 * })
 * ```
 *
 * ### Error Handling:
 * - If any exceptions are thrown during the [other] or [transform] functions, the error is caught, and the resulting `LiveData`
 *   will not emit a value for that cycle, unless an `onErrorReturn` function is provided in the `Transform`.
 *
 * @param T The type of the value in the original [LiveData].
 * @param R The type of the value in the chained [LiveData].
 * @param X The type of the value after transformation.
 */
fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: suspend (T?) -> LiveData<R>,
    condition: suspend (T?) -> Boolean,
    transform: Transform.Nullable<T, R, X>
): LiveData<X?> = liveData(context) {
    internalChainWith(other, condition).applyTransformation(context, transform).collect(::emit)
}
/* endregion ------------------------------------------------------------------------------------ */

/* region Non Nullable -------------------------------------------------------------------------- */
/**
 * Chains this [LiveData] with another non-nullable [LiveData] based on a condition, using coroutines.
 *
 * @param context The [CoroutineContext] to use for the coroutine. This context controls the execution of the chaining logic,
 * allowing you to specify which thread or dispatcher should be used. By default, you can use [Dispatchers.IO] for IO-bound tasks
 * or [Dispatchers.Main] for UI-related tasks.
 * @param other A suspend function that returns another [LiveData] based on the non-null value of this [LiveData]. This function is
 * called asynchronously, allowing you to perform non-blocking operations when determining the next [LiveData] to chain.
 * @param condition A suspend function that determines whether to chain with the other [LiveData] based on a non-null value.
 * The condition is evaluated asynchronously, and if it returns `true`, the chaining occurs.
 * @return A [LiveData] emitting pairs of non-nullable values from this [LiveData] and the chained [LiveData]. The resulting [LiveData]
 * emits a [Pair] containing the current non-null value from this [LiveData] and the value from the chained [LiveData].
 *
 * This coroutine-based method allows for asynchronous operations when chaining non-nullable [LiveData] sources. It is useful in scenarios
 * where you need to perform operations like network requests, database queries, or other asynchronous tasks as part of the chaining process.
 *
 * ### Success Case:
 * - When this [LiveData] emits a non-null value, the `condition` function is asynchronously evaluated.
 * - If the condition returns `true`, the `other` suspend function is invoked to get the chained [LiveData].
 * - The resulting [LiveData] emits a [Pair] of values from both [LiveData] sources if both are non-null.
 *
 * ### Failure Case:
 * - If this [LiveData] emits a null value, the chaining does not occur, and the method does nothing for that emission.
 * - If the `condition` suspend function returns `false` or throws an exception, the chaining is skipped, and no value is emitted.
 * - If the `other` suspend function fails or returns null, the resulting [LiveData] does not emit a value for that emission.
 *
 * ### Example Usage:
 * ```
 * // Example of chaining two LiveData sources with a condition
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = liveData1.chainNotNullWith(
 *     context = Dispatchers.IO,
 *     other = { value -> fetchStringLiveDataBasedOnValue(value) },
 *     // Only chain if the value is greater than 0
 *     condition = { value -> value > 0 }
 * )
 *
 * // Observing the resulting LiveData
 * liveData2.observe(this, Observer { result ->
 *     val (intValue, stringValue) = result
 *     println("Int Value: $intValue, String Value: $stringValue")
 * })
 * ```
 *
 * ### Error Handling:
 * - If the condition or the other function throws an exception, the exception is caught, and the method does not emit a value for that emission.
 * - Null values in the original [LiveData] are ignored, ensuring that the chaining only occurs for non-null values.
 *
 * @param T The type of the value in the original [LiveData].
 * @param R The type of the value in the chained [LiveData].
 */
fun <T, R> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
): LiveData<Pair<T, R>> = liveData(context) {
    internalChainNotNullWith(other, condition).collect(::emit)
}

/**
 * Chains this [LiveData] with another non-nullable [LiveData] based on a condition, applies a transformation, and emits the result.
 *
 * @param context The [CoroutineContext] to use for the coroutine. This context controls the execution of the chaining and transformation logic,
 * allowing you to specify which thread or dispatcher should be used. Common contexts include [Dispatchers.IO] for IO-bound tasks and
 * [Dispatchers.Main] for UI-related tasks.
 * @param other A suspend function that returns another [LiveData] based on the non-null value of this [LiveData]. This function is
 * called asynchronously, allowing non-blocking operations when determining the next [LiveData] to chain.
 * @param condition A suspend function that determines whether to chain with the other [LiveData] based on a non-null value.
 * The condition is evaluated asynchronously, and if it returns `true`, the chaining occurs.
 * @param transform A [Transform.NotNull] object that contains the transformation logic, including the dispatcher on which the transformation
 * will occur, the function to apply to the paired values, and optional error handling logic.
 * @return A [LiveData] emitting transformed values from the non-nullable values of this and the chained [LiveData].
 *
 * This method is useful for scenarios where you need to chain two non-nullable [LiveData] sources, apply some complex logic on their combined
 * values, and emit the result. The transformation is performed asynchronously, and the method allows for error handling through the
 * [Transform.NotNull] object.
 *
 * ### Success Case:
 * - When this [LiveData] emits a non-null value, the `condition` function is asynchronously evaluated.
 * - If the condition returns `true`, the `other` suspend function is invoked to get the chained [LiveData].
 * - The resulting [LiveData] emits a transformed value based on the combined non-nullable values from both [LiveData] sources.
 *
 * ### Failure Case:
 * - If this [LiveData] emits a null value, the chaining does not occur, and the method does nothing for that emission.
 * - If the `condition` suspend function returns `false` or throws an exception, the chaining is skipped, and no value is emitted.
 * - If the `other` suspend function fails or returns null, the resulting [LiveData] does not emit a value for that emission.
 * - If the transformation fails and error handling is not provided, the method omits the emission for that combination.
 *
 * ### Transform Parameter:
 * The `transform` parameter is an instance of [Transform.NotNull], which allows you to define how the values from the two [LiveData]
 * sources are transformed and how errors are handled during the transformation.
 *
 * ### The `Transform.NotNull` class offers several variations:
 * - **OmitFail**:
 *      - **Description**: Transforms the values and omits the emission if the transformation fails.
 *      - **Constructor Parameters**:
 *           - `dispatcher`: The [CoroutineDispatcher] on which the transformation will occur. Default is [Dispatchers.IO].
 *           - `func`: The transformation function to apply to the paired non-nullable values.
 *      - **Example**:
 *   ```kotlin
 *   val transform = Transform.NotNull.OmitFail(
 *       dispatcher = Dispatchers.Default,
 *       func = { intValue, stringValue -> "$intValue: $stringValue" }
 *   )
 *   ```
 * - **Fallback**:
 *      - **Description**: Transforms the values and applies a fallback function if the transformation fails.
 *      - **Constructor Parameters**:
 *           - `dispatcher`: The [CoroutineDispatcher] on which the transformation will occur. Default is [Dispatchers.IO].
 *           - `func`: The transformation function to apply to the paired non-nullable values.
 *           - `onErrorReturn`: A function that handles errors during transformation, allowing you to provide an alternate result.
 *      - **Example**:
 *   ```kotlin
 *   val transform = Transform.NotNull.Fallback(
 *       dispatcher = Dispatchers.IO,
 *       func = { intValue, stringValue -> "$intValue: $stringValue" },
 *       onErrorReturn = { error -> "Error: ${error.message}" }
 *   )
 *   ```
 *
 * ### Example Usage:
 * ```
 * // Example of chaining two LiveData sources, applying a transformation, and handling errors
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<String> = liveData1.chainNotNullWith(
 *     context = Dispatchers.IO,
 *     other = { liveData2 },
 *     condition = { it > 0 },
 *     transform = Transform.NotNull.OmitFail(
 *         func = { intValue, stringValue -> "$intValue: $stringValue" },
 *         onErrorReturn = { error -> "Error occurred: ${error.message}" }
 *     )
 * )
 *
 * // Observing the resulting LiveData
 * chainedLiveData.observe(this, Observer { result ->
 *     println("Transformed Value: $result")
 * })
 * ```
 *
 * ### Error Handling:
 * - If the condition or the other function throws an exception, the exception is caught, and the method does not emit a value for that emission.
 * - If the transformation function throws an exception and `onErrorReturn` is provided, the fallback result is emitted.
 * - Null values in the original [LiveData] are ignored, ensuring that the chaining only occurs for non-null values.
 *
 * @param T The type of the value in the original [LiveData].
 * @param R The type of the value in the chained [LiveData].
 * @param X The type of the value after applying the transformation.
 */
fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
    transform: Transform.NotNull<T, R, X>
): LiveData<X> = liveData(context) {
    internalChainNotNullWith(other, condition)
        .applyTransformation(context, transform)
        .collect(::emit)
}
/* endregion ------------------------------------------------------------------------------------ */

/* region Auxiliary Functions ------------------------------------------------------------------- */
private suspend inline fun <T, R> LiveData<T>.internalChainNotNullWith(
    noinline other: suspend (T) -> LiveData<R>,
    noinline condition: suspend (T) -> Boolean,
) = internalChainWith(
    other = { data -> data?.let { other(it) } ?: error("Data null in chainNotNullWith") },
    condition = { data -> data?.let { condition(it) } ?: false }
).mapNotNull { it.onlyWithValues() }

private suspend inline fun <T, R> LiveData<T>.internalChainWith(
    noinline other: suspend (T?) -> LiveData<R>,
    noinline condition: suspend (T?) -> Boolean,
) = channelFlow {
    val aFlow = asFlow()
    var bJob: Job? = null

    aFlow.collect { aValue ->
        bJob?.cancel("New Data Arrived, so, should cancel previous job")
        val isConditionMet = condition.runCatching { invoke(aValue) }.getOrDefault(false)
        val liveData = other.takeIf { isConditionMet }?.runCatching { invoke(aValue) }?.getOrNull()

        when {
            /* Do nothing */
            liveData == null -> Unit

            /* Here the magic becomes alive! */
            else -> {
                if (liveData.isInitialized.not()) trySend(aValue to null)
                bJob = launch { internalCombine(liveData).collect(::trySend) }
            }
        }
    }
}
/* endregion ------------------------------------------------------------------------------------ */
