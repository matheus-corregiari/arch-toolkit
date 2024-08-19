@file:Suppress("Filename", "TooManyFunctions", "unused")

package br.com.arch.toolkit.util

import androidx.lifecycle.LiveData
import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.livedata.responseLiveData
import br.com.arch.toolkit.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/* region Operator Functions -------------------------------------------------------------------- */
@Experimental
operator fun <T, R> LiveData<T>.plus(other: ResponseLiveData<R>): ResponseLiveData<Pair<T?, R?>> =
    combine(context = EmptyCoroutineContext, other = other)

@Experimental
operator fun <T, R> ResponseLiveData<T>.plus(source: LiveData<R>): ResponseLiveData<Pair<T?, R?>> =
    combine(context = EmptyCoroutineContext, other = source)

@Experimental
operator fun <T, R> ResponseLiveData<T>.plus(source: ResponseLiveData<R>): ResponseLiveData<Pair<T?, R?>> =
    combine(context = EmptyCoroutineContext, other = source)
/* endregion ------------------------------------------------------------------------------------ */

/* region LiveData + Response Functions --------------------------------------------------------- */
/* Nullable ------------------------------------------------------------------------------------- */
@Experimental
fun <T, R> LiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>
): ResponseLiveData<Pair<T?, R?>> = responseLiveData(context = context) {
    internalCombine(other).mapNotNull { (data, result) -> data?.let(::dataResultSuccess) + result }
        .collect(::emit)
}

@Experimental
fun <T, R, X> LiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>>
): ResponseLiveData<X> = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalCombine(other).mapNotNull { (data, result) -> data?.let(::dataResultSuccess) + result }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

@Experimental
fun <T, R, X> LiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>,
    transform: suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>
): ResponseLiveData<X> = combine(
    context = context,
    other = other,
    transform = Dispatchers.IO to transform
)

/* Non Nullable --------------------------------------------------------------------------------- */
@Experimental
fun <T, R> LiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>
): ResponseLiveData<Pair<T, R>> = responseLiveData(context = context) {
    internalCombineNotNull(other)
        .mapNotNull { (data, result) -> (dataResultSuccess(data) + result).onlyWithValues() }
        .collect(::emit)
}

@Experimental
fun <T, R, X> LiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T, R>>) -> DataResult<X>>
): ResponseLiveData<X> = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalCombineNotNull(other)
        .mapNotNull { (data, result) -> (dataResultSuccess(data) + result).onlyWithValues() }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

@Experimental
fun <T, R, X> LiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>,
    transform: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
): ResponseLiveData<X> = combineNotNull(
    context = context,
    other = other,
    transform = Dispatchers.IO to transform
)
/* endregion ------------------------------------------------------------------------------------ */

/* region Response + LiveData Functions ---------------------------------------------------------------- */
/* Nullable ------------------------------------------------------------------------------------- */
@Experimental
fun <T, R> ResponseLiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: LiveData<R>
): ResponseLiveData<Pair<T?, R?>> = responseLiveData(context = context) {
    internalCombine(other).mapNotNull { (result, data) -> result + data?.let(::dataResultSuccess) }
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: LiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>>
): ResponseLiveData<X> = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalCombine(other)
        .mapNotNull { (result, data) -> result + data?.let(::dataResultSuccess) }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: LiveData<R>,
    transform: suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>
): ResponseLiveData<X> = combine(
    context = context,
    other = other,
    transform = Dispatchers.IO to transform
)

/* Non Nullable --------------------------------------------------------------------------------- */
@Experimental
fun <T, R> ResponseLiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: LiveData<R>
): ResponseLiveData<Pair<T, R>> = responseLiveData(context = context) {
    internalCombineNotNull(other)
        .mapNotNull { (result, data) -> (result + dataResultSuccess(data)).onlyWithValues() }
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: LiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T, R>>) -> DataResult<X>>
): ResponseLiveData<X> = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalCombineNotNull(other)
        .mapNotNull { (result, data) -> (result + dataResultSuccess(data)).onlyWithValues() }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: LiveData<R>,
    transform: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
): ResponseLiveData<X> = combineNotNull(
    context = context,
    other = other,
    transform = Dispatchers.IO to transform
)
/* endregion ------------------------------------------------------------------------------------ */

/* region Response + Response Functions --------------------------------------------------------- */
/* Nullable ------------------------------------------------------------------------------------- */
@Experimental
fun <T, R> ResponseLiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>
): ResponseLiveData<Pair<T?, R?>> = responseLiveData(context = context) {
    internalCombine(other).mapNotNull { (resultA, resultB) -> resultA + resultB }
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>>
): ResponseLiveData<X> = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalCombine(other).mapNotNull { (resultA, resultB) -> resultA + resultB }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.combine(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>,
    transform: suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>
): ResponseLiveData<X> = combine(
    context = context,
    other = other,
    transform = Dispatchers.IO to transform
)

/* Non Nullable --------------------------------------------------------------------------------- */
@Experimental
fun <T, R> ResponseLiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>
): ResponseLiveData<Pair<T, R>> = responseLiveData(context = context) {
    internalCombineNotNull(other)
        .mapNotNull { (resultA, resultB) -> (resultA + resultB).onlyWithValues() }
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T, R>>) -> DataResult<X>>
): ResponseLiveData<X> = responseLiveData(context = context) {
    val (dispatcher, block) = transform
    internalCombineNotNull(other)
        .mapNotNull { (resultA, resultB) -> (resultA + resultB).onlyWithValues() }
        .flowOn(dispatcher)
        .mapNotNull { result -> runCatching { block(result) }.getOrElse(::dataResultError) }
        .flowOn(context)
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.combineNotNull(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseLiveData<R>,
    transform: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
): ResponseLiveData<X> = combineNotNull(
    context = context,
    other = other,
    transform = Dispatchers.IO to transform
)
/* endregion ------------------------------------------------------------------------------------ */
