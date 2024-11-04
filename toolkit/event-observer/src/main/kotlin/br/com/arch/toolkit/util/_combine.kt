@file:Suppress("Filename", "TooManyFunctions", "unused")

package br.com.arch.toolkit.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/* Operator Functions --------------------------------------------------------------------------- */
operator fun <T, R> LiveData<T>.plus(other: LiveData<R>) = combine(other = other)

/* region Regular Functions --------------------------------------------------------------------- */
/**
 * Combines this [LiveData] with another [LiveData], allowing for nullable values, and emits pairs of their values.
 *
 * @param other The other [LiveData] whose values (nullable) will be combined with this [LiveData].
 * @return A [LiveData] emitting pairs of values from both [LiveData] sources, with nullability allowed.
 *
 * This method creates a [MediatorLiveData] that listens to both [LiveData] sources. Whenever either [LiveData] emits a value,
 * the combined value is emitted as a pair. The method allows for nullable values, meaning either value in the resulting pair
 * can be null. It also accounts for initial values if either [LiveData] was already initialized.
 *
 * ### Behavior:
 * - The method uses `AtomicBoolean` flags to ignore the first emission from each [LiveData] if they were already initialized.
 * - If either [LiveData] is initialized, the initial pair of values is emitted immediately.
 * - Subsequent emissions occur whenever either [LiveData] emits a new value.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int?> = MutableLiveData(1)
 * val liveData2: LiveData<String?> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int?, String?>> = liveData1.combine(liveData2)
 *
 * combinedLiveData.observe(this, Observer { pair ->
 *     println("Combined Value: ${pair.first}, ${pair.second}")
 * })
 * ```
 *
 * ### Error Handling:
 * - If both values are null, the method emits a pair containing `null` for each value.
 * - The method ensures that a pair is emitted only when there's a valid change, ignoring the first emission if it was already initialized.
 *
 * @param T The type of the value in this [LiveData].
 * @param R The type of the value in the other [LiveData].
 */
fun <T, R> LiveData<T>.combine(other: LiveData<R>): LiveData<Pair<T?, R?>> {
    val ignoreA = AtomicBoolean(isInitialized)
    val ignoreB = AtomicBoolean(other.isInitialized)
    val initial = (value to other.value).takeIf { isInitialized || other.isInitialized }
    val mediator = when {
        initial == null -> MediatorLiveData()
        else -> MediatorLiveData(initial)
    }

    mediator.addSource(this) {
        (it to other.value).takeUnless { ignoreA.compareAndSet(true, false) }
            ?.let(mediator::setValue)
    }
    mediator.addSource(other) {
        (value to it).takeUnless { ignoreB.compareAndSet(true, false) }
            ?.let(mediator::setValue)
    }
    return mediator
}

/**
 * Combines this [LiveData] with another [LiveData], requiring non-nullable values, and emits pairs of their values.
 *
 * @param other The other [LiveData] whose non-nullable values will be combined with this [LiveData].
 * @return A [LiveData] emitting pairs of non-nullable values from both [LiveData] sources.
 *
 * This method creates a [MediatorLiveData] that listens to both [LiveData] sources and emits pairs of their values
 * only when both values are non-null. If either value is null, the pair is not emitted. The method also accounts for
 * initial values if either [LiveData] was already initialized and non-null.
 *
 * ### Behavior:
 * - The method uses `AtomicBoolean` flags to ignore the first emission from each [LiveData] if they were already initialized.
 * - If either [LiveData] is initialized with a non-null value, the initial pair of values is emitted immediately.
 * - Subsequent emissions occur whenever either [LiveData] emits a new non-null value.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int, String>> = liveData1.combineNotNull(liveData2)
 *
 * combinedLiveData.observe(this, Observer { pair ->
 *     println("Combined Value: ${pair.first}, ${pair.second}")
 * })
 * ```
 *
 * ### Error Handling:
 * - If either value is null, the pair is not emitted.
 * - The method ensures that a pair is emitted only when both values are non-null and valid.
 *
 * @param T The type of the value in this [LiveData].
 * @param R The type of the value in the other [LiveData].
 */
fun <T, R> LiveData<T>.combineNotNull(other: LiveData<R>): LiveData<Pair<T, R>> {
    val ignoreA = AtomicBoolean(isInitialized)
    val ignoreB = AtomicBoolean(other.isInitialized)
    val initial = (value to other.value).takeIf { isInitialized || other.isInitialized }
    val mediator = when {
        initial == null -> MediatorLiveData()
        initial.onlyWithValues() == null -> MediatorLiveData()
        else -> MediatorLiveData<Pair<T, R>>(initial.onlyWithValues())
    }

    mediator.addSource(this) {
        (it to other.value).takeUnless { ignoreA.compareAndSet(true, false) }
            ?.onlyWithValues()
            ?.let(mediator::setValue)
    }
    mediator.addSource(other) {
        (value to it).takeUnless { ignoreB.compareAndSet(true, false) }
            ?.onlyWithValues()
            ?.let(mediator::setValue)
    }
    return mediator
}
/* endregion ------------------------------------------------------------------------------------ */

/* region Nullable -------------------------------------------------------------------------- */
/**
 * Combines this [LiveData] with another [LiveData], allowing for nullable values, and emits pairs of their values.
 *
 * @param context The [CoroutineContext] to use for the coroutine. This context dictates where the combination logic will be executed,
 * such as on [Dispatchers.IO] for background operations or [Dispatchers.Main] for UI-related tasks.
 * @param other The other [LiveData] whose values (nullable) will be combined with this [LiveData].
 * @return A [LiveData] emitting pairs of values from both [LiveData] sources, with nullability allowed.
 *
 * This method is useful when you have two [LiveData] sources that you want to combine into a single [LiveData] emitting pairs of their values,
 * and you need to account for possible null values in either [LiveData].
 *
 * ### Success Case:
 * - When either this [LiveData] or the `other` [LiveData] emits a value (nullable or non-nullable),
 * a pair of these values is emitted by the resulting [LiveData].
 * - Both values in the pair can be nullable, reflecting the current states of the two [LiveData] sources.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int?> = MutableLiveData(1)
 * val liveData2: LiveData<String?> = MutableLiveData(null)
 * val combinedLiveData: LiveData<Pair<Int?, String?>> = liveData1.combine(
 *     context = Dispatchers.IO,
 *     other = liveData2
 * )
 *
 * // Observing the resulting LiveData
 * combinedLiveData.observe(this, Observer { pair ->
 *     println("Combined Value: ${pair.first}, ${pair.second}")
 * })
 * ```
 *
 * ### Error Handling:
 * - If either [LiveData] emits a null value, the combination still occurs, and a pair with one or both values as null is emitted.
 *
 * @param T The type of the value in this [LiveData].
 * @param R The type of the value in the other [LiveData].
 */
fun <T, R> LiveData<T>.combine(
    context: CoroutineContext,
    other: LiveData<R>
): LiveData<Pair<T?, R?>> = liveData(context) { internalCombine(other).collect(::emit) }

/**
 * Combines this [LiveData] with another [LiveData], applies a transformation, and emits the transformed result, allowing for nullable values.
 *
 * @param context The [CoroutineContext] to use for the coroutine. This context determines where the combination and transformation logic
 * will be executed, allowing you to control the threading model.
 * @param other The other [LiveData] whose values (nullable) will be combined with this [LiveData].
 * @param transform A [Transform.Nullable] object that contains the transformation logic, including the dispatcher on which the transformation
 * will occur, the function to apply to the paired values, and optional error handling logic.
 * @return A [LiveData] emitting the transformed values from the possibly nullable values of both [LiveData] sources.
 *
 * This method is useful when you want to combine two [LiveData] sources and apply a custom transformation to their paired values,
 * while handling nullable values appropriately. The transformation is performed asynchronously, and the method provides options
 * for error handling through the [Transform.Nullable] object.
 *
 * ### Success Case:
 * - When either this [LiveData] or the `other` [LiveData] emits a value (nullable or non-nullable),
 * the `transform` function is applied to these values, and the resulting transformed value is emitted by the resulting [LiveData].
 *
 * ### Failure Case:
 * - If both values are null, the method does not emit any value unless specifically handled by the `transform` function.
 * - If the transformation function throws an exception and error handling is not provided, the method omits the emission for that combination.
 *
 * ### Transform Parameter:
 * The `transform` parameter is an instance of [Transform.Nullable], allowing you to define how the possibly
 * nullable values from the two [LiveData] sources are transformed and how errors are handled during the transformation.
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
 * // Example of combining two LiveData sources, applying a transformation, and handling nullable values
 * val liveData1: LiveData<Int?> = MutableLiveData(1)
 * val liveData2: LiveData<String?> = MutableLiveData(null)
 * val transformedLiveData: LiveData<String?> = liveData1.combine(
 *     context = Dispatchers.IO,
 *     other = liveData2,
 *     transform = Transform.Nullable.NullFail(
 *         func = { intValue, stringValue -> "$intValue: $stringValue" }
 *     )
 * )
 *
 * // Observing the resulting LiveData
 * transformedLiveData.observe(this, Observer { result ->
 *     println("Transformed Value: $result")
 * })
 * ```
 *
 * ### Error Handling:
 * - If the transformation function throws an exception and `onErrorReturn` is provided, the fallback result is emitted.
 * - If no error handling is provided, the emission is omitted for that combination.
 * - Null values are handled according to the specified `failMode` in the `transform` parameter.
 *
 * @param T The type of the value in this [LiveData].
 * @param R The type of the value in the other [LiveData].
 * @param X The type of the value after applying the transformation.
 */
fun <T, R, X> LiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: LiveData<R>,
    transform: Transform.Nullable<T, R, X>
): LiveData<X?> = liveData(context) {
    internalCombine(other).applyTransformation(context, transform).collect(::emit)
}
/* endregion ------------------------------------------------------------------------------------ */

/* region Non Nullable -------------------------------------------------------------------------- */
/**
 * Combines this [LiveData] with another non-nullable [LiveData] and emits a pair of their values.
 *
 * @param context The [CoroutineContext] to use for the coroutine. This context dictates where the combination logic will be executed, such as
 * on [Dispatchers.IO] for background operations or [Dispatchers.Main] for UI-related tasks.
 * @param other The other [LiveData] whose non-nullable values will be combined with this [LiveData].
 * @return A [LiveData] emitting pairs of non-nullable values from both [LiveData] sources.
 *
 * This method is useful when you have two [LiveData] sources that you want to combine into a single [LiveData] emitting pairs of their values.
 * It ensures that both [LiveData] sources emit non-null values before combining and emitting the result.
 *
 * ### Success Case:
 * - When both this [LiveData] and the `other` [LiveData] emit non-null values, a pair of these values is emitted by the resulting [LiveData].
 *
 * ### Failure Case:
 * - If either this [LiveData] or the `other` [LiveData] emits a null value, the method does not emit any value for that emission.
 * - No combination is performed if either [LiveData] is in a null state, ensuring that only non-null pairs are emitted.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int, String>> = liveData1.combineNotNull(
 *     context = Dispatchers.IO,
 *     other = liveData2
 * )
 *
 * // Observing the resulting LiveData
 * combinedLiveData.observe(this, Observer { pair ->
 *     println("Combined Value: ${pair.first}, ${pair.second}")
 * })
 * ```
 *
 * @param T The type of the value in this [LiveData].
 * @param R The type of the value in the other [LiveData].
 */
fun <T, R> LiveData<T>.combineNotNull(
    context: CoroutineContext,
    other: LiveData<R>
): LiveData<Pair<T, R>> = liveData(context) { internalCombineNotNull(other).collect(::emit) }

/**
 * Combines this [LiveData] with another non-nullable [LiveData], applies a transformation, and emits the transformed result.
 *
 * @param context The [CoroutineContext] to use for the coroutine. This context determines where the combination and transformation logic
 * will be executed, allowing you to control the threading model.
 * @param other The other [LiveData] whose non-nullable values will be combined with this [LiveData].
 * @param transform A [Transform.NotNull] object that contains the transformation logic, including the dispatcher on which the transformation
 * will occur, the function to apply to the paired values, and optional error handling logic.
 * @return A [LiveData] emitting the transformed values from the non-nullable values of both [LiveData] sources.
 *
 * This method is useful when you want to combine two non-nullable [LiveData] sources and apply a custom transformation to their paired values.
 * The transformation is performed asynchronously, and the method provides options for error handling through the [Transform.NotNull] object.
 *
 * ### Success Case:
 * - When both this [LiveData] and the `other` [LiveData] emit non-null values, the `transform` function is applied to these values,
 * and the resulting transformed value is emitted by the resulting [LiveData].
 *
 * ### Failure Case:
 * - If either this [LiveData] or the `other` [LiveData] emits a null value, the method does not emit any value for that emission.
 * - If the transformation function throws an exception and error handling is not provided, the method omits the emission for that combination.
 *
 * ### Transform Parameter:
 * The `transform` parameter is an instance of [Transform.NotNull], allowing you to define how the values from the two [LiveData]
 * sources are transformed and how errors are handled during the transformation.
 *
 * ### The [Transform.NotNull] class offers several variations:
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
 * // Example of combining two LiveData sources, applying a transformation, and handling errors
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String> = liveData1.combineNotNull(
 *     context = Dispatchers.IO,
 *     other = liveData2,
 *     transform = Transform.NotNull.OmitFail(
 *         func = { intValue, stringValue -> "$intValue: $stringValue" }
 *     )
 * )
 *
 * // Observing the resulting LiveData
 * transformedLiveData.observe(this, Observer { result ->
 *     println("Transformed Value: $result")
 * })
 * ```
 *
 * ### Error Handling:
 * - If the transformation function throws an exception and `onErrorReturn` is provided, the fallback result is emitted.
 * - If no error handling is provided, the emission is omitted for that combination.
 * - Null values in either [LiveData] are ignored, ensuring that the combination only occurs for non-null values.
 *
 * @param T The type of the value in this [LiveData].
 * @param R The type of the value in the other [LiveData].
 * @param X The type of the value after applying the transformation.
 */
fun <T, R, X> LiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: LiveData<R>,
    transform: Transform.NotNull<T, R, X>
): LiveData<X> = liveData(context) {
    internalCombineNotNull(other).applyTransformation(context, transform).collect(::emit)
}
/* endregion ------------------------------------------------------------------------------------ */

/* region Auxiliary Functions ------------------------------------------------------------------- */
private suspend inline fun <T, R> LiveData<T>.internalCombineNotNull(other: LiveData<R>) =
    internalCombine(other).mapNotNull { it.onlyWithValues() }

internal suspend inline fun <T, R> LiveData<T>.internalCombine(other: LiveData<R>) = channelFlow {
    val aFlow: Flow<T> = asFlow()
    val bFlow: Flow<R> = other.asFlow()
    val cFlow: Flow<Pair<T?, R?>> = aFlow.combine(bFlow) { a, b -> a to b }

    withContext(currentCoroutineContext()) {
        launch { aFlow.collect { if (other.isInitialized.not()) trySend(it to other.value) else cancel() } }
        launch { bFlow.collect { if (isInitialized.not()) trySend(value to it) else cancel() } }
        cFlow.collect(::trySend)
    }
}
/* endregion ------------------------------------------------------------------------------------ */
