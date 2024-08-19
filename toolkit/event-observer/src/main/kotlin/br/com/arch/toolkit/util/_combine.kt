@file:Suppress("Filename", "TooManyFunctions", "unused")

package br.com.arch.toolkit.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/* Operator Functions --------------------------------------------------------------------------- */

/**
 * Combines two [LiveData] objects using the `+` operator.
 *
 * This function merges the emissions of two [LiveData] sources into a single [LiveData] object containing pairs of values.
 *
 * @param other The other [LiveData] to combine with this [LiveData].
 * @return A [LiveData] that emits pairs of values from both [LiveData] sources.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int?, String?>> = liveData1 + liveData2
 * ```
 *
 * @see [combine]
 * @see [plus]
 */
operator fun <T, R> LiveData<T>.plus(other: LiveData<R>) = combine(other = other)

/* Regular Functions ---------------------------------------------------------------------------- */

/**
 * Combines two [LiveData] objects without using coroutines.
 *
 * This function creates a [MediatorLiveData] that merges the emissions from two [LiveData] sources into a single [LiveData].
 *
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] containing pairs of values from both [LiveData] sources.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int?, String?>> = liveData1.combine(liveData2)
 * ```
 *
 * @see [combineNotNull]
 * @see [plus]
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
 * Combines two [LiveData] objects, ensuring both are non-null.
 *
 * This function merges the emissions of two [LiveData] sources into a single [LiveData] only if both values are non-null.
 *
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] containing non-null pairs of values from both [LiveData] sources.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int, String>> = liveData1.combineNotNull(liveData2)
 * ```
 *
 * @see [combine]
 * @see [combineNotNull]
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

/* Coroutine Functions -------------------------------------------------------------------------- */

/**
 * Combines two [LiveData] objects using coroutines.
 *
 * This method allows you to merge two [LiveData] sources using a coroutine context, which is useful for asynchronous operations.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] emitting pairs of values from both LiveData sources.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int?, String?>> = liveData1.combine(Dispatchers.IO, liveData2)
 * ```
 *
 * @see [combineNotNull]
 * @see [plus]
 */
fun <T, R> LiveData<T>.combine(
    context: CoroutineContext,
    other: LiveData<R>
): LiveData<Pair<T?, R?>> = liveData(context) { internalCombine(other).collect(::emit) }

/**
 * Combines two [LiveData] objects with a transformation function using coroutines.
 *
 * This method merges two [LiveData] sources and applies a transformation function to the pair of values.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @param transform A pair of [CoroutineDispatcher] and a suspend function that transforms the pair of values.
 * @return A [LiveData] emitting the transformed values.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String?> = liveData1.combine(Dispatchers.IO, liveData2) { a, b -> "$a$b" }
 * ```
 *
 * @see [combine]
 * @see [combineNotNull]
 */
fun <T, R, X> LiveData<T>.combine(
    context: CoroutineContext,
    other: LiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (T?, R?) -> X?>
): LiveData<X?> = liveData(context) {
    val (dispatcher, block) = transform
    internalCombine(other)
        .flowOn(dispatcher)
        .map { (a, b) -> runCatching { block(a, b) }.getOrNull() }
        .flowOn(context)
        .collect(::emit)
}

/**
 * Combines two [LiveData] objects with a transformation function using coroutines.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @param transform A suspend function that transforms the pair of values.
 * @return A [LiveData] emitting the transformed values or `null` if the transformation fails.
 *
 * The transformation function is applied within the specified [CoroutineContext] and uses
 * `Dispatchers.IO` by default for the transformation process. If the [transform] function throws
 * an exception during its execution, the `LiveData` will emit `null` for that combination.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String?> = liveData1.combine(Dispatchers.Main, liveData2) { a, b ->
 *     if (a != null && b != null) "$a$b" else null
 * }
 * ```
 *
 * In this example, the `combine` method merges `liveData1` and `liveData2`, and the transformation
 * function concatenates the values into a string. The result is a new `LiveData` that emits
 * `"1A"`.
 *
 * @see [LiveData.combine]
 * @see [LiveData.combineNotNull]
 */
fun <T, R, X> LiveData<T>.combine(
    context: CoroutineContext,
    other: LiveData<R>,
    transform: suspend (T?, R?) -> X?
): LiveData<X?> = combine(
    context = context,
    other = other,
    transform = Dispatchers.IO to transform
)

/**
 * Combines two [LiveData] objects with a transformation function and an optional [CoroutineContext].
 *
 * @param other The other [LiveData] to combine with.
 * @param transform A pair consisting of a [CoroutineDispatcher] and a suspend function that transforms the pair of nullable values.
 * @return A [LiveData] emitting the transformed nullable values.
 *
 * This method combines two `LiveData` sources, where either or both may emit nullable values, and applies the provided transformation function.
 * The transformation function is executed within the specified [CoroutineDispatcher], and the combination is handled in the provided
 * [CoroutineContext] or `EmptyCoroutineContext` by default. If the transformation function throws an exception, the emission for that
 * combination is omitted, allowing the flow to continue without interruption.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int?> = MutableLiveData(1)
 * val liveData2: LiveData<String?> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String?> = liveData1.combine(liveData2, Dispatchers.Default to { a, b -> "$a$b" })
 * ```
 *
 * @see combine
 */
fun <T, R, X> LiveData<T>.combine(
    other: LiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (T?, R?) -> X?>
): LiveData<X?> = combine(
    context = EmptyCoroutineContext,
    other = other,
    transform = transform
)

/**
 * Combines two [LiveData] objects with a transformation function using a default [CoroutineDispatcher].
 *
 * @param other The other [LiveData] to combine with.
 * @param transform A suspend function that transforms the pair of nullable values.
 * @return A [LiveData] emitting the transformed nullable values.
 *
 * This method combines two `LiveData` sources, where either or both may emit nullable values, and applies the provided transformation function.
 * The transformation is executed on the default [CoroutineDispatcher] (`Dispatchers.IO`). If the transformation function throws an exception,
 * the emission for that combination is omitted, allowing the flow to continue without interruption.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int?> = MutableLiveData(1)
 * val liveData2: LiveData<String?> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String?> = liveData1.combine(liveData2) { a, b -> "$a$b" }
 * ```
 *
 * @see combine
 */
fun <T, R, X> LiveData<T>.combine(
    other: LiveData<R>,
    transform: suspend (T?, R?) -> X?
): LiveData<X?> = combine(
    other = other,
    transform = Dispatchers.IO to transform
)

/**
 * Combines two non-null [LiveData] objects using coroutines.
 *
 * This method merges two [LiveData] sources, ensuring that both are non-null, using a coroutine context.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] emitting pairs of non-null values from both LiveData sources.
 *
 * ### Example Usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int, String>> = liveData1.combineNotNull(Dispatchers.IO, liveData2)
 * ```
 *
 * @see [combine]
 * @see [plus]
 */
fun <T, R> LiveData<T>.combineNotNull(
    context: CoroutineContext,
    other: LiveData<R>
): LiveData<Pair<T, R>> = liveData(context) { internalCombineNotNull(other).collect(::emit) }

/**
 * Combines two non-null [LiveData] objects with a transformation function using coroutines.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @param transform A pair consisting of a [CoroutineDispatcher] and a suspend function that transforms the pair of non-null values.
 * @return A [LiveData] emitting the transformed non-null values.
 *
 * This method ensures that both [LiveData] sources emit non-null values before applying the transformation function.
 * The transformation function is applied in the specified [CoroutineDispatcher] and the flow is managed within the
 * provided [CoroutineContext]. If the transformation function throws an exception, the `LiveData` will simply omit
 * the emission for that combination.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String> = liveData1.combineNotNull(Dispatchers.Main, liveData2, Dispatchers.IO to { a, b ->
 *     "$a$b"
 * })
 * ```
 *
 * In this example, `liveData1` and `liveData2` are combined only if both emit non-null values.
 * The transformation function concatenates the two values, resulting in `"1A"` being emitted by `transformedLiveData`.
 *
 * @see [LiveData.combine]
 * @see [LiveData.combineNotNull]
 * @see [LiveData.combineNotNull]
 */
fun <T, R, X> LiveData<T>.combineNotNull(
    context: CoroutineContext,
    other: LiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (T, R) -> X>
): LiveData<X> = liveData(context) {
    val (dispatcher, block) = transform
    internalCombineNotNull(other)
        .flowOn(dispatcher)
        .mapNotNull { (a, b) -> runCatching { block(a, b) }.getOrNull() }
        .flowOn(context)
        .collect(::emit)
}

/**
 * Combines two non-null [LiveData] objects with a transformation function using coroutines.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @param transform A suspend function that transforms the pair of non-null values.
 * @return A [LiveData] emitting the transformed non-null values.
 *
 * This method ensures that both [LiveData] sources emit non-null values before applying the transformation function.
 * The transformation function is applied in the specified [CoroutineContext] using `Dispatchers.IO` by default for
 * the transformation process. If the [transform] function throws an exception during its execution, the `LiveData`
 * will simply omit the emission for that combination.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String> = liveData1.combineNotNull(Dispatchers.Main, liveData2) { a, b ->
 *     "$a$b"
 * }
 * ```
 *
 * In this example, the `combineNotNull` method merges `liveData1` and `liveData2`, applying the transformation function
 * to concatenate the non-null values. The result is `"1A"` being emitted by `transformedLiveData`.
 *
 * @see [LiveData.combine]
 * @see [LiveData.combineNotNull]
 * @see [LiveData.combineNotNull]
 */
fun <T, R, X> LiveData<T>.combineNotNull(
    context: CoroutineContext,
    other: LiveData<R>,
    transform: suspend (T, R) -> X
): LiveData<X> = combineNotNull(
    context = context,
    other = other,
    transform = Dispatchers.IO to transform
)

/**
 * Combines two non-null [LiveData] objects with a transformation function and an optional [CoroutineContext].
 *
 * @param other The other [LiveData] to combine with.
 * @param transform A pair consisting of a [CoroutineDispatcher] and a suspend function that transforms the pair of non-null values.
 * @return A [LiveData] emitting the transformed non-null values.
 *
 * This method combines two `LiveData` sources, ensuring that both emit non-null values before applying the transformation function.
 * The transformation function is executed within the specified [CoroutineDispatcher], and the combination is handled in the provided
 * [CoroutineContext] or `EmptyCoroutineContext` by default. If the transformation function throws an exception, the emission for that
 * combination is omitted, allowing the flow to continue without interruption.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String> = liveData1.combineNotNull(liveData2, Dispatchers.Default to { a, b -> "$a$b" })
 * ```
 *
 * @see combineNotNull
 */
fun <T, R, X> LiveData<T>.combineNotNull(
    other: LiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (T, R) -> X>
): LiveData<X> = combineNotNull(
    context = EmptyCoroutineContext,
    other = other,
    transform = transform
)

/**
 * Combines two non-null [LiveData] objects with a transformation function using a default [CoroutineDispatcher].
 *
 * @param other The other [LiveData] to combine with.
 * @param transform A suspend function that transforms the pair of non-null values.
 * @return A [LiveData] emitting the transformed non-null values.
 *
 * This method combines two `LiveData` sources, ensuring that both emit non-null values before applying the transformation function.
 * The transformation is executed on the default [CoroutineDispatcher] (`Dispatchers.IO`). If the transformation function throws an exception,
 * the emission for that combination is omitted, allowing the flow to continue without interruption.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String> = liveData1.combineNotNull(liveData2) { a, b -> "$a$b" }
 * ```
 *
 * @see combineNotNull
 */
fun <T, R, X> LiveData<T>.combineNotNull(
    other: LiveData<R>,
    transform: suspend (T, R) -> X
): LiveData<X> = combineNotNull(
    other = other,
    transform = Dispatchers.IO to transform
)

/* Auxiliary Functions -------------------------------------------------------------------------- */

internal suspend inline fun <T, R> LiveData<T>.internalCombineNotNull(other: LiveData<R>) =
    internalCombine(other).mapNotNull { it.onlyWithValues() }

internal suspend inline fun <T, R> LiveData<T>.internalCombine(other: LiveData<R>) = channelFlow {
    val aFlow = this@internalCombine.asFlow()
    val bFlow = other.asFlow()
    val cFlow: Flow<Pair<T?, R?>> = aFlow.combine(bFlow) { a, b -> (a to b) }

    withContext(currentCoroutineContext()) {
        launch { aFlow.collect { if (other.isInitialized.not()) trySend(it to other.value) else cancel() } }
        launch { bFlow.collect { if (isInitialized.not()) trySend(value to it) else cancel() } }
        cFlow.collect(::trySend)
    }
}
