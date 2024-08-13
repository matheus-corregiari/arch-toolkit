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

/**
 * Combines two [LiveData] objects using the `+` operator.
 * This function can be used to merge the emissions of two LiveData sources into a single LiveData.
 *
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] containing pairs of values from both LiveData sources.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int?, String?>> = liveData1 + liveData2
 * ```
 */
operator fun <T, R> LiveData<T>.plus(other: LiveData<R>) = combine(other = other)

/**
 * Combines two [LiveData] objects using the `+` operator with a specified [CoroutineContext].
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] containing pairs of values from both LiveData sources.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int?, String?>> = liveData1.plus(Dispatchers.IO, liveData2)
 * ```
 */
fun <T, R> LiveData<T>.plus(context: CoroutineContext, other: LiveData<R>) =
    combine(context = context, other = other)

/**
 * Combines two [LiveData] objects without using coroutines.
 *
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] containing pairs of values from both LiveData sources.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int?, String?>> = liveData1.combine(liveData2)
 * ```
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
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] containing non-null pairs of values from both LiveData sources.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int, String>> = liveData1.combineNotNull(liveData2)
 * ```
 */
fun <T, R> LiveData<T>.combineNotNull(other: LiveData<R>): LiveData<Pair<T, R>> {
    val ignoreA = AtomicBoolean(isInitialized)
    val ignoreB = AtomicBoolean(other.isInitialized)
    val initial = (value to other.value).takeIf { isInitialized || other.isInitialized }
    val mediator = when {
        initial == null -> MediatorLiveData()
        initial.toNotNull() == null -> MediatorLiveData()
        else -> MediatorLiveData<Pair<T, R>>(initial.toNotNull())
    }

    mediator.addSource(this) {
        (it to other.value).takeUnless { ignoreA.compareAndSet(true, false) }
            ?.toNotNull()
            ?.let(mediator::setValue)
    }
    mediator.addSource(other) {
        (value to it).takeUnless { ignoreB.compareAndSet(true, false) }
            ?.toNotNull()
            ?.let(mediator::setValue)
    }
    return mediator
}

/**
 * Combines two [LiveData] objects using coroutines.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] emitting pairs of values from both LiveData sources.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int?, String?>> = liveData1.combine(Dispatchers.IO, liveData2)
 * ```
 */
fun <T, R> LiveData<T>.combine(context: CoroutineContext, other: LiveData<R>) =
    liveData(context) { internalCombine(other).collect(::emit) }

/**
 * Combines two [LiveData] objects with a transformation function using coroutines.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @param transformDispatcher The [CoroutineDispatcher] for performing the transformation.
 * @param transform A suspend function that transforms the pair of values.
 * @return A [LiveData] emitting the transformed values.
 *
 * If the [transform] function throws an exception during its execution, the `LiveData` will emit `null` for that combination.
 * The use of `runCatching` ensures that any exceptions thrown by the `transform` function are captured and handled gracefully.
 * This prevents the flow from being terminated by exceptions, allowing the rest of the processing pipeline to continue.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String> = liveData1.combine(
 *     Dispatchers.IO, liveData2, Dispatchers.Default
 * ) { a, b -> "$a$b" }
 * ```
 */
fun <T, R, X> LiveData<T>.combine(
    context: CoroutineContext,
    other: LiveData<R>,
    transformDispatcher: CoroutineDispatcher,
    transform: suspend (T?, R?) -> X?
): LiveData<X?> = liveData(context) {
    internalCombine(other)
        .flowOn(transformDispatcher)
        .map { (a, b) -> runCatching { transform(a, b) }.getOrNull() }
        .flowOn(context)
        .collect(::emit)
}

/**
 * Combines two [LiveData] objects with a transformation function using coroutines.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @param transform A suspend function that transforms the pair of values.
 * @return A [LiveData] emitting the transformed values.
 *
 * If the [transform] function throws an exception during its execution, the `LiveData` will emit `null` for that combination.
 * The use of `runCatching` ensures that any exceptions thrown by the `transform` function are captured and handled gracefully.
 * This prevents the flow from being terminated by exceptions, allowing the rest of the processing pipeline to continue.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String> = liveData1.combine(Dispatchers.IO, liveData2) { a, b -> "$a$b" }
 * ```
 */
fun <T, R, X> LiveData<T>.combine(
    context: CoroutineContext,
    other: LiveData<R>,
    transform: suspend (T?, R?) -> X?
): LiveData<X?> = combine(context, other, Dispatchers.IO, transform)

/**
 * Combines two non-null [LiveData] objects using coroutines.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @return A [LiveData] emitting pairs of non-null values from both LiveData sources.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val combinedLiveData: LiveData<Pair<Int, String>> = liveData1.combineNotNull(Dispatchers.IO, liveData2)
 * ```
 */
fun <T, R> LiveData<T>.combineNotNull(context: CoroutineContext, other: LiveData<R>) =
    liveData<Pair<T, R>>(context) {
        internalCombine(other).mapNotNull { it.toNotNull() }.collect(::emit)
    }

/**
 * Combines two non-null [LiveData] objects with a transformation function using coroutines.
 *
 * @param context The [CoroutineContext] in which to perform the combination.
 * @param other The other [LiveData] to combine with.
 * @param transformDispatcher The [CoroutineDispatcher] for performing the transformation.
 * @param transform A suspend function that transforms the pair of values.
 * @return A [LiveData] emitting the transformed non-null values.
 *
 * If the [transform] function throws an exception during its execution, the `LiveData` will emit `null` for that combination.
 * The use of `runCatching` ensures that any exceptions thrown by the `transform` function are captured and handled gracefully.
 * This prevents the flow from being terminated by exceptions, allowing the rest of the processing pipeline to continue.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String> = liveData1.combineNotNull(
 *     Dispatchers.IO, liveData2, Dispatchers.Default
 * ) { a, b -> "$a$b" }
 * ```
 */
fun <T, R, X> LiveData<T>.combineNotNull(
    context: CoroutineContext,
    other: LiveData<R>,
    transformDispatcher: CoroutineDispatcher,
    transform: suspend (T, R) -> X
): LiveData<X> = liveData(context) {
    internalCombine(other)
        .mapNotNull { it.toNotNull() }
        .flowOn(transformDispatcher)
        .mapNotNull { (a, b) -> runCatching { transform(a, b) }.getOrNull() }
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
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val transformedLiveData: LiveData<String> = liveData1.combineNotNull(Dispatchers.IO, liveData2) { a, b -> "$a$b" }
 * ```
 */
fun <T, R, X> LiveData<T>.combineNotNull(
    context: CoroutineContext,
    other: LiveData<R>,
    transform: suspend (T, R) -> X
): LiveData<X> = combineNotNull(context, other, Dispatchers.IO, transform)

/**
 * Helper function to combine two [LiveData] objects internally using coroutines.
 *
 * @param other The other [LiveData] to combine with.
 * @return A [Flow] emitting pairs of values from both LiveData sources.
 *
 * This function collects values from both [LiveData] sources and emits pairs of values.
 * It handles cases where one or both [LiveData] sources are initially uninitialized by using
 * `trySend` to send values only when both sources are initialized.
 */
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
