@file:Suppress("Filename", "TooManyFunctions", "LongParameterList", "unused")

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

/* LiveData + Response Functions ---------------------------------------------------------------- */
/* Nullable ------------------------------------------------------------------------------------- */
fun <T, R> LiveData<T>.chainWith(
    context: CoroutineContext,
    other: suspend (T?) -> ResponseLiveData<R>,
    condition: suspend (T?) -> Boolean,
) = responseLiveData(context = context) {
    internalChainWith(other, condition)
        .mapNotNull { (data, result) -> data?.let(::dataResultSuccess) + result }
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext,
    other: suspend (T?) -> ResponseLiveData<R>,
    condition: suspend (T?) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>>
): ResponseLiveData<X> = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalChainWith(other, condition)
        .mapNotNull { (data, result) -> data?.let(::dataResultSuccess) + result }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext,
    other: suspend (T?) -> ResponseLiveData<R>,
    condition: suspend (T?) -> Boolean,
    transform: suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>
): ResponseLiveData<X> = chainWith(context, other, condition, Dispatchers.IO to transform)

/* Non Nullable --------------------------------------------------------------------------------- */

fun <T, R> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> ResponseLiveData<R>,
    condition: suspend (T) -> Boolean,
) = responseLiveData(context = context) {
    internalChainNotNullWith(other, condition)
        .mapNotNull { (data, result) -> (dataResultSuccess(data) + result).onlyWithValues() }
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> ResponseLiveData<R>,
    condition: suspend (T) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T, R>>) -> DataResult<X>>
): ResponseLiveData<X> = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalChainNotNullWith(other, condition)
        .mapNotNull { (data, result) -> (dataResultSuccess(data) + result).onlyWithValues() }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext,
    other: suspend (T) -> ResponseLiveData<R>,
    condition: suspend (T) -> Boolean,
    transform: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
): ResponseLiveData<X> = chainNotNullWith(context, other, condition, Dispatchers.IO to transform)
