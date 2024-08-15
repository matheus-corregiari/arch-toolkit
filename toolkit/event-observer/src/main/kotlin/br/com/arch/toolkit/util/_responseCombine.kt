@file:Suppress("Filename", "TooManyFunctions", "unused")

package br.com.arch.toolkit.util

import androidx.lifecycle.LiveData
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.livedata.responseLiveData
import br.com.arch.toolkit.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/* Operator Functions --------------------------------------------------------------------------- */

operator fun <T, R> LiveData<T>.plus(other: ResponseLiveData<R>) =
    combine(context = EmptyCoroutineContext, other = other)

/* LiveData + Response Functions ---------------------------------------------------------------- */
/* Nullable ------------------------------------------------------------------------------------- */

fun <T, R> LiveData<T>.combine(
    context: CoroutineContext,
    other: ResponseLiveData<R>
) = responseLiveData(context = context) {
    internalCombine(other).mapNotNull { (data, result) -> data?.let(::dataResultSuccess) + result }
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.combine(
    context: CoroutineContext,
    other: ResponseLiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>>
) = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalCombine(other).mapNotNull { (data, result) -> data?.let(::dataResultSuccess) + result }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.combine(
    context: CoroutineContext,
    other: ResponseLiveData<R>,
    transform: suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>
) = combine(context, other, Dispatchers.IO to transform)

/* Non Nullable --------------------------------------------------------------------------------- */
fun <T, R> LiveData<T>.combineNotNull(
    context: CoroutineContext,
    other: ResponseLiveData<R>
) = responseLiveData(context = context) {
    internalCombineNotNull(other)
        .mapNotNull { (data, result) -> (dataResultSuccess(data) + result).onlyWithValues() }
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.combineNotNull(
    context: CoroutineContext,
    other: ResponseLiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T, R>>) -> DataResult<X>>
) = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalCombineNotNull(other)
        .mapNotNull { (data, result) -> (dataResultSuccess(data) + result).onlyWithValues() }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.combineNotNull(
    context: CoroutineContext,
    other: ResponseLiveData<R>,
    transform: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
) = combineNotNull(context, other, Dispatchers.IO to transform)
