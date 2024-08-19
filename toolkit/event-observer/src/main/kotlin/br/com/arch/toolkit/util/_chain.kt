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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/* Regular Functions ---------------------------------------------------------------------------- */

/**
 * Chains this [LiveData] with another [LiveData] based on a condition.
 *
 * @param other A function that returns another [LiveData] based on the value of this [LiveData].
 * @param condition A function that determines whether to chain with the other [LiveData].
 * @return A [LiveData] emitting pairs of values from this and the chained [LiveData].
 *
 * This method observes the current [LiveData] and chains it with another [LiveData] when the provided condition is met.
 * It combines the values emitted by both [LiveData] sources into a pair and emits them.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<Pair<Int?, String?>> = liveData1.chainWith(
 *     other = { liveData2 },
 *     condition = { it != null }
 * )
 * ```
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
 * Chains this [LiveData] with another non-nullable [LiveData] based on a condition.
 *
 * @param other A function that returns another [LiveData] based on the non-null value of this [LiveData].
 * @param condition A function that determines whether to chain with the other [LiveData] based on a non-null value.
 * @return A [LiveData] emitting pairs of non-nullable values from this and the chained [LiveData].
 *
 * This method is a variant of `chainWith` that only works with non-nullable values. It throws an error if the value is null.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<Pair<Int, String>> = liveData1.chainNotNullWith(
 *     other = { liveData2 },
 *     condition = { it != null }
 * )
 * ```
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

/* Coroutine Functions -------------------------------------------------------------------------- */

/**
 * Chains this [LiveData] with another [LiveData] based on a condition, using coroutines.
 *
 * @param context The [CoroutineContext] to use for the coroutine.
 * @param other A suspend function that returns another [LiveData] based on the value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData].
 * @return A [LiveData] emitting pairs of values from this and the chained [LiveData].
 *
 * This coroutine-based method allows for asynchronous operations when chaining [LiveData] sources.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<Pair<Int?, String?>> = liveData1.chainWith(
 *     context = Dispatchers.IO,
 *     other = { liveData2 },
 *     condition = { it != null }
 * )
 * ```
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
 * @param context The [CoroutineContext] to use for the coroutine.
 * @param other A suspend function that returns another [LiveData] based on the value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData].
 * @param transform A pair consisting of a [CoroutineDispatcher] and a suspend function to transform the values.
 * @return A [LiveData] emitting the transformed values.
 *
 * This method allows for chaining two [LiveData] sources and applying a transformation function to the combined values.
 * The transformation is executed in the provided [CoroutineDispatcher].
 *
 * The transformation function is applied within the specified [CoroutineContext] and uses
 * `Dispatchers.IO` by default for the transformation process. If the [transform] function throws
 * an exception during its execution, the `LiveData` will simply omit the emission for that combination.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<String?> = liveData1.chainWith(
 *     context = Dispatchers.Default,
 *     other = { liveData2 },
 *     condition = { it != null },
 *     transform = Dispatchers.IO to { a, b -> "$a$b" }
 * )
 * ```
 */
fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext,
    other: suspend (T?) -> LiveData<R>,
    condition: suspend (T?) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (T?, R?) -> X?>
): LiveData<X?> = liveData(context) {
    val (dispatcher, block) = transform
    internalChainWith(other, condition)
        .flowOn(dispatcher)
        .map { (a, b) -> runCatching { block(a, b) }.let { it.getOrNull() to it.isFailure } }
        .filter { (_, isFailure) -> isFailure.not() }
        .map { (result, _) -> result }
        .flowOn(context)
        .collect(::emit)
}

/**
 * Chains this [LiveData] with another [LiveData] based on a condition, using a simple transformation function.
 *
 * @param context The [CoroutineContext] to use for the coroutine.
 * @param other A suspend function that returns another [LiveData] based on the value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData].
 * @param transform A suspend function to transform the values from this and the chained [LiveData].
 * @return A [LiveData] emitting the transformed values.
 *
 * This method allows for chaining two [LiveData] sources and applying a simple transformation function to the combined values.
 * The transformation is executed in the provided [CoroutineContext].
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<String?> = liveData1.chainWith(
 *     context = Dispatchers.IO,
 *     other = { liveData2 },
 *     condition = { it != null },
 *     transform = { a, b -> "$a$b" }
 * )
 * ```
 */
fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext,
    other: suspend (T?) -> LiveData<R>,
    condition: suspend (T?) -> Boolean,
    transform: suspend (T?, R?) -> X?
): LiveData<X?> = chainWith(
    context = context,
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)

/**
 * Chains this [LiveData] with another [LiveData] based on a condition, using a simple transformation function and the default [CoroutineContext].
 *
 * @param other A suspend function that returns another [LiveData] based on the value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData].
 * @param transform A pair consisting of a [CoroutineDispatcher] and a suspend function to transform the values.
 * @return A [LiveData] emitting the transformed values.
 *
 * This method allows for chaining two [LiveData] sources and applying a transformation function to the combined values.
 * The transformation is executed in the provided [CoroutineDispatcher] and uses the default [CoroutineContext].
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<String?> = liveData1.chainWith(
 *     other = { liveData2 },
 *     condition = { it != null },
 *     transform = Dispatchers.IO to { a, b -> "$a$b" }
 * )
 * ```
 */
fun <T, R, X> LiveData<T>.chainWith(
    other: suspend (T?) -> LiveData<R>,
    condition: suspend (T?) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (T?, R?) -> X?>
): LiveData<X?> = chainWith(
    context = EmptyCoroutineContext,
    other = other,
    condition = condition,
    transform = transform
)

/**
 * Chains this [LiveData] with another [LiveData] based on a condition,
 * using a simple transformation function and the default [CoroutineContext] and [CoroutineDispatcher].
 *
 * @param other A suspend function that returns another [LiveData] based on the value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData].
 * @param transform A suspend function to transform the values from this and the chained [LiveData].
 * @return A [LiveData] emitting the transformed values.
 *
 * This method allows for chaining two [LiveData] sources and applying a simple transformation function to the combined values.
 * The transformation is executed using the default [CoroutineContext] and [CoroutineDispatcher].
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<String?> = liveData1.chainWith(
 *     other = { liveData2 },
 *     condition = { it != null },
 *     transform = { a, b -> "$a$b" }
 * )
 * ```
 */
fun <T, R, X> LiveData<T>.chainWith(
    other: suspend (T?) -> LiveData<R>,
    condition: suspend (T?) -> Boolean,
    transform: suspend (T?, R?) -> X?
): LiveData<X?> = chainWith(
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)

/**
 * Chains this [LiveData] with another non-nullable [LiveData] based on a condition, using coroutines.
 *
 * @param context The [CoroutineContext] to use for the coroutine.
 * @param other A suspend function that returns another [LiveData] based on the non-null value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData] based on a non-null value.
 * @return A [LiveData] emitting pairs of non-nullable values from this and the chained [LiveData].
 *
 * This coroutine-based method allows for asynchronous operations when chaining non-nullable [LiveData] sources.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<Pair<Int, String>> = liveData1.chainNotNullWith(
 *     context = Dispatchers.IO,
 *     other = { liveData2 },
 *     condition = { it != null }
 * )
 * ```
 */
fun <T, R> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
): LiveData<Pair<T, R>> = liveData(context) {
    internalChainNotNullWith(other, condition).collect(::emit)
}

/**
 * Chains this non-nullable [LiveData] with another non-nullable [LiveData] based on a condition, using a simple transformation function.
 *
 * @param context The [CoroutineContext] to use for the coroutine.
 * @param other A suspend function that returns another non-nullable [LiveData] based on the value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData] based on a non-nullable value.
 * @param transform A pair consisting of a [CoroutineDispatcher] and a suspend function to transform the non-nullable values.
 * @return A [LiveData] emitting the transformed values.
 *
 * This coroutine-based method allows for chaining two non-nullable [LiveData] sources and
 * applying a simple transformation function to the combined values.
 * The transformation is executed in the provided [CoroutineDispatcher].
 *
 * The transformation function is applied within the specified [CoroutineContext] and uses
 * `Dispatchers.IO` by default for the transformation process. If the [transform] function throws
 * an exception during its execution, the `LiveData` will simply omit the emission for that combination.
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<String> = liveData1.chainNotNullWith(
 *     context = Dispatchers.Default,
 *     other = { liveData2 },
 *     condition = { it != null },
 *     transform = Dispatchers.IO to { a, b -> "$a$b" }
 * )
 * ```
 */
fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (T, R) -> X>
): LiveData<X> = liveData(context) {
    val (dispatcher, block) = transform
    internalChainNotNullWith(other, condition)
        .flowOn(dispatcher)
        .map { (a, b) -> runCatching { block(a, b) }.let { it.getOrNull() to it.isFailure } }
        .filter { (_, isFailure) -> isFailure.not() }
        .mapNotNull { (result, _) -> result }
        .flowOn(context)
        .collect(::emit)
}

/**
 * Chains this non-nullable [LiveData] with another non-nullable [LiveData] based on a condition,
 * using a simple transformation function and the default [CoroutineContext].
 *
 * @param other A suspend function that returns another non-nullable [LiveData] based on the value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData] based on a non-nullable value.
 * @param transform A pair consisting of a [CoroutineDispatcher] and a suspend function to transform the non-nullable values.
 * @return A [LiveData] emitting the transformed values.
 *
 * This method allows for chaining two non-nullable [LiveData] sources and applying a simple transformation function to the combined values.
 * The transformation is executed in the provided [CoroutineDispatcher] and uses the default [CoroutineContext].
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<String> = liveData1.chainNotNullWith(
 *     other = { liveData2 },
 *     condition = { it != null },
 *     transform = Dispatchers.IO to { a, b -> "$a$b" }
 * )
 * ```
 */
fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
    transform: suspend (T, R) -> X
): LiveData<X> = chainNotNullWith(
    context = context,
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)

/**
 * Chains this non-nullable [LiveData] with another non-nullable [LiveData] based on a condition,
 * using a simple transformation function and the default [CoroutineContext].
 *
 * @param other A suspend function that returns another non-nullable [LiveData] based on the value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData] based on a non-nullable value.
 * @param transform A pair consisting of a [CoroutineDispatcher] and a suspend function to transform the non-nullable values.
 * @return A [LiveData] emitting the transformed values.
 *
 * This method allows for chaining two non-nullable [LiveData] sources and applying a transformation function to the combined values.
 * The transformation is executed in the provided [CoroutineDispatcher] and uses the default [CoroutineContext].
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<String> = liveData1.chainNotNullWith(
 *     other = { liveData2 },
 *     condition = { it != null },
 *     transform = Dispatchers.IO to { a, b -> "$a$b" }
 * )
 * ```
 */
fun <T, R, X> LiveData<T>.chainNotNullWith(
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (T, R) -> X>
): LiveData<X> = chainNotNullWith(
    context = EmptyCoroutineContext,
    other = other,
    condition = condition,
    transform = transform
)

/**
 * Chains this non-nullable [LiveData] with another non-nullable [LiveData] based on a condition,
 * using a simple transformation function and the default [CoroutineContext] and [CoroutineDispatcher].
 *
 * @param other A suspend function that returns another non-nullable [LiveData] based on the value of this [LiveData].
 * @param condition A suspend function that determines whether to chain with the other [LiveData] based on a non-nullable value.
 * @param transform A suspend function to transform the non-nullable values from this and the chained [LiveData].
 * @return A [LiveData] emitting the transformed values.
 *
 * This method allows for chaining two non-nullable [LiveData] sources and applying a transformation function to the combined values.
 * The transformation is executed using the default [CoroutineContext] and [CoroutineDispatcher].
 *
 * Example usage:
 * ```
 * val liveData1: LiveData<Int> = MutableLiveData(1)
 * val liveData2: LiveData<String> = MutableLiveData("A")
 * val chainedLiveData: LiveData<String> = liveData1.chainNotNullWith(
 *     other = { liveData2 },
 *     condition = { it != null },
 *     transform = { a, b -> "$a$b" }
 * )
 * ```
 */
fun <T, R, X> LiveData<T>.chainNotNullWith(
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
    transform: suspend (T, R) -> X
): LiveData<X> = chainNotNullWith(
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)

/* Auxiliary Functions -------------------------------------------------------------------------- */

internal suspend inline fun <T, R> LiveData<T>.internalChainNotNullWith(
    noinline other: suspend (T) -> LiveData<R>,
    noinline condition: suspend (T) -> Boolean,
) = internalChainWith(
    other = { data -> data?.let { other(it) } ?: error("Data null in chainNotNullWith") },
    condition = { data -> data?.let { condition(it) } ?: false }
).mapNotNull { it.onlyWithValues() }

internal suspend inline fun <T, R> LiveData<T>.internalChainWith(
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
