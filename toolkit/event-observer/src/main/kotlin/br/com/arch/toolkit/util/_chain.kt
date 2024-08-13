@file:Suppress("Filename", "TooManyFunctions", "LongParameterList")

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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun <T, R> LiveData<T>.chainWith(
    other: (T?) -> LiveData<R>,
    condition: (T?) -> Boolean
): LiveData<Pair<T?, R?>> {
    val mediator = MediatorLiveData<Pair<T?, R?>>()
    var bLiveData: LiveData<R>? = null

    fun onAReceived(aValue: T?) {
        bLiveData?.let(mediator::removeSource)
        val isConditionMet = condition.runCatching { invoke(aValue) }.getOrDefault(false)
        bLiveData = if (isConditionMet) other.runCatching { invoke(aValue) }.getOrNull() else null
        bLiveData?.let { liveData ->
            if (liveData.isInitialized.not()) mediator.value = aValue to null
            mediator.addSource(liveData) { bValue -> (aValue to bValue).let(mediator::setValue) }
        }
    }

    mediator.addSource(this, ::onAReceived)
    return mediator
}

fun <T, R> LiveData<T>.chainNotNullWith(
    other: (T) -> LiveData<R>,
    condition: (T) -> Boolean
): LiveData<Pair<T, R>> = chainWith(
    other = { it?.let(other) ?: error("Data null in chainNotNullWith") },
    condition = { it?.let(condition) ?: false }
).mapNotNull { it.toNotNull() }

fun <T, R> LiveData<T>.chainWith(
    context: CoroutineContext,
    other: suspend (T?) -> LiveData<R>,
    condition: suspend (T?) -> Boolean,
) = liveData(context) { internalChainWith(other, condition).collect(::emit) }

fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext,
    other: suspend (T?) -> LiveData<R>,
    condition: suspend (T?) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (T?, R?) -> X?>
): LiveData<X?> = liveData(context) {
    val (dispatcher, block) = transform
    internalChainWith(other, condition)
        .flowOn(dispatcher)
        .map { (a, b) -> runCatching { block(a, b) }.getOrNull() }
        .flowOn(context)
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext,
    other: suspend (T?) -> LiveData<R>,
    condition: suspend (T?) -> Boolean,
    transform: suspend (T?, R?) -> X?
): LiveData<X?> = chainWith(context, other, condition, Dispatchers.IO to transform)

fun <T, R> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
) = liveData<Pair<T, R>>(context) {
    internalChainNotNullWith(other, condition).collect(::emit)
}

fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (T, R) -> X>
): LiveData<X> = liveData(context) {
    val (dispatcher, block) = transform
    internalChainNotNullWith(other, condition)
        .flowOn(dispatcher)
        .mapNotNull { (a, b) -> runCatching { block(a, b) }.getOrNull() }
        .flowOn(context)
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> LiveData<R>,
    condition: suspend (T) -> Boolean,
    transform: suspend (T, R) -> X
): LiveData<X> = chainNotNullWith(context, other, condition, Dispatchers.IO to transform)

private suspend inline fun <T, R> LiveData<T>.internalChainNotNullWith(
    noinline other: suspend (T) -> LiveData<R>,
    noinline condition: suspend (T) -> Boolean,
) = internalChainWith(
    other = { data -> data?.let { other(it) } ?: error("Data null in chainNotNullWith") },
    condition = { data -> data?.let { condition(it) } ?: false }
).mapNotNull { it.toNotNull() }

private suspend inline fun <T, R> LiveData<T>.internalChainWith(
    noinline other: suspend (T?) -> LiveData<R>,
    noinline condition: suspend (T?) -> Boolean,
) = channelFlow {
    val aFlow = asFlow()
    var bJob: Job? = null

    aFlow.onCompletion { bJob?.cancel() }.collect { aValue ->
        bJob?.cancel("New Data Arrived, so, should cancel previous job")
        val isConditionMet = condition.runCatching { invoke(aValue) }.getOrDefault(false)
        val liveData =
            if (isConditionMet) other.runCatching { invoke(aValue) }.getOrNull() else null

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
