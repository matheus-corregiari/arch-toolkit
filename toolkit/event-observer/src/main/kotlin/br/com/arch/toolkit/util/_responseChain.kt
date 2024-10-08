@file:Suppress("Filename", "TooManyFunctions", "LongParameterList", "unused")

package br.com.arch.toolkit.util

import androidx.lifecycle.LiveData
import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.livedata.responseLiveData
import br.com.arch.toolkit.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapNotNull
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/* region LiveData + Response Functions --------------------------------------------------------- */
/* Nullable ------------------------------------------------------------------------------------- */
@FunctionalInterface
fun interface WithResponse<T, R> {
    suspend fun invoke(result: T?): ResponseLiveData<R>
}

@Experimental
fun <T, R> LiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: WithResponse<T, R>,
    condition: suspend (T?) -> Boolean,
): ResponseLiveData<Pair<T?, R?>> = responseLiveData(context = context) {
    internalChainWith(other::invoke, condition)
        .mapNotNull { (data, result) -> data?.let(::dataResultSuccess) + result }
        .collect(::emit)
}

@Experimental
fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: WithResponse<T, R>,
    condition: suspend (T?) -> Boolean,
    transform: ResponseTransform<T?, R?, X>
): ResponseLiveData<X> = responseLiveData(context = context) {
    internalChainWith(other::invoke, condition)
        .mapNotNull { (data, result) -> data?.let(::dataResultSuccess) + result }
        .applyTransformation(context, transform)
        .collect(::emit)
}

@Experimental
fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: WithResponse<T, R>,
    condition: suspend (T?) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>>
): ResponseLiveData<X> = chainWith(
    context = context,
    other = other,
    condition = condition,
    transform = ResponseTransform.StatusFail(transform.first, transform.second)
)

@Experimental
fun <T, R, X> LiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: WithResponse<T, R>,
    condition: suspend (T?) -> Boolean,
    transform: suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>
): ResponseLiveData<X> = chainWith(
    context = context,
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)

/* Non Nullable --------------------------------------------------------------------------------- */
@FunctionalInterface
fun interface NotNullWithResponse<T, R> {
    suspend fun invoke(result: T): ResponseLiveData<R>
}

@Experimental
fun <T, R> LiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: NotNullWithResponse<T, R>,
    condition: suspend (T) -> Boolean,
): ResponseLiveData<Pair<T, R>> = responseLiveData(context = context) {
    internalChainNotNullWith(other::invoke, condition)
        .mapNotNull { (data, result) -> (dataResultSuccess(data) + result).onlyWithValues() }
        .collect(::emit)
}

@Experimental
fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: NotNullWithResponse<T, R>,
    condition: suspend (T) -> Boolean,
    transform: ResponseTransform<T, R, X>
): ResponseLiveData<X> = responseLiveData(context = context) {
    internalChainNotNullWith(other::invoke, condition)
        .mapNotNull { (data, result) -> (dataResultSuccess(data) + result).onlyWithValues() }
        .applyTransformation(context, transform)
        .collect(::emit)
}

@Experimental
fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: NotNullWithResponse<T, R>,
    condition: suspend (T) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T, R>>) -> DataResult<X>>
): ResponseLiveData<X> = chainNotNullWith(
    context = context,
    other = other,
    condition = condition,
    transform = ResponseTransform.StatusFail(transform.first, transform.second)
)

@Experimental
fun <T, R, X> LiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: NotNullWithResponse<T, R>,
    condition: suspend (T) -> Boolean,
    transform: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
): ResponseLiveData<X> = chainNotNullWith(
    context = context,
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)
/* endregion ------------------------------------------------------------------------------------ */

/* region Response + LiveData Functions ---------------------------------------------------------------- */
/* Nullable ------------------------------------------------------------------------------------- */
@FunctionalInterface
fun interface ResponseWith<T, R> {
    suspend fun invoke(result: DataResult<T>): LiveData<R>
}

@Experimental
fun <T, R> ResponseLiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWith<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
): ResponseLiveData<Pair<T?, R?>> = responseLiveData(context = context) {
    internalChainWith(
        condition = { result -> result?.let { condition(it) } == true },
        other = { result -> other.invoke(requireNotNull(result)) },
    ).mapNotNull { (result, data) -> result + data?.let(::dataResultSuccess) }.collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWith<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: ResponseTransform<T?, R?, X>
): ResponseLiveData<X> = responseLiveData(context = context) {
    internalChainWith(
        condition = { result -> result?.let { condition(it) } == true },
        other = { result -> other.invoke(requireNotNull(result)) },
    ).mapNotNull { (result, data) -> result + data?.let(::dataResultSuccess) }
        .applyTransformation(context, transform)
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWith<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>>
): ResponseLiveData<X> = chainWith(
    context = context,
    other = other,
    condition = condition,
    transform = ResponseTransform.StatusFail(transform.first, transform.second)
)

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWith<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>
): ResponseLiveData<X> = chainWith(
    context = context,
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)

/* Non Nullable --------------------------------------------------------------------------------- */
@FunctionalInterface
fun interface ResponseNotNullWith<T, R> {
    suspend fun invoke(result: DataResult<T>): LiveData<R>
}

@Experimental
fun <T, R> ResponseLiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseNotNullWith<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
): ResponseLiveData<Pair<T, R>> = responseLiveData(context = context) {
    internalChainNotNullWith(other::invoke, condition)
        .mapNotNull { (result, data) -> (result + dataResultSuccess(data)).onlyWithValues() }
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseNotNullWith<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: ResponseTransform<T, R, X>
): ResponseLiveData<X> = responseLiveData(context = context) {
    internalChainNotNullWith(other::invoke, condition)
        .mapNotNull { (result, data) -> (result + dataResultSuccess(data)).onlyWithValues() }
        .applyTransformation(context, transform)
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseNotNullWith<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T, R>>) -> DataResult<X>>
): ResponseLiveData<X> = chainNotNullWith(
    context = context,
    other = other,
    condition = condition,
    transform = ResponseTransform.StatusFail(transform.first, transform.second)
)

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseNotNullWith<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
): ResponseLiveData<X> = chainNotNullWith(
    context = context,
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)
/* endregion ------------------------------------------------------------------------------------ */

/* region Response + Response Functions ---------------------------------------------------------------- */
/* Nullable ------------------------------------------------------------------------------------- */
@FunctionalInterface
fun interface ResponseWithResponse<T, R> {
    suspend fun invoke(result: DataResult<T>): ResponseLiveData<R>
}

@Experimental
fun <T, R> ResponseLiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWithResponse<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
): ResponseLiveData<Pair<T?, R?>> = responseLiveData(context = context) {
    internalChainWith(
        condition = { result -> result?.let { condition(it) } == true },
        other = { result -> other.invoke(requireNotNull(result)) },
    ).mapNotNull { (resultA, resultB) -> resultA + resultB }.collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWithResponse<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: ResponseTransform<T?, R?, X>
): ResponseLiveData<X> = responseLiveData(context = context) {
    internalChainWith(
        condition = { result -> result?.let { condition(it) } == true },
        other = { result -> other.invoke(requireNotNull(result)) },
    ).mapNotNull { (resultA, resultB) -> resultA + resultB }
        .applyTransformation(context, transform)
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWithResponse<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>>
): ResponseLiveData<X> = chainWith(
    context = context,
    other = other,
    condition = condition,
    transform = ResponseTransform.StatusFail(transform.first, transform.second)
)

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWithResponse<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: suspend (DataResult<Pair<T?, R?>>) -> DataResult<X>
): ResponseLiveData<X> = chainWith(
    context = context,
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)

/* Non Nullable --------------------------------------------------------------------------------- */
@Experimental
fun <T, R> ResponseLiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWithResponse<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
): ResponseLiveData<Pair<T, R>> = responseLiveData(context = context) {
    internalChainNotNullWith(other::invoke, condition)
        .mapNotNull { (resultA, resultB) -> (resultA + resultB).onlyWithValues() }
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWithResponse<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: ResponseTransform<T, R, X>
): ResponseLiveData<X> = responseLiveData(context = context) {
    internalChainNotNullWith(other::invoke, condition)
        .mapNotNull { (resultA, resultB) -> (resultA + resultB).onlyWithValues() }
        .applyTransformation(context, transform)
        .collect(::emit)
}

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWithResponse<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: Pair<CoroutineDispatcher, suspend (DataResult<Pair<T, R>>) -> DataResult<X>>
): ResponseLiveData<X> = chainNotNullWith(
    context = context,
    other = other,
    condition = condition,
    transform = ResponseTransform.StatusFail(transform.first, transform.second)
)

@Experimental
fun <T, R, X> ResponseLiveData<T>.chainNotNullWith(
    context: CoroutineContext = EmptyCoroutineContext,
    other: ResponseWithResponse<T, R>,
    condition: suspend (DataResult<T>) -> Boolean,
    transform: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
): ResponseLiveData<X> = chainNotNullWith(
    context = context,
    other = other,
    condition = condition,
    transform = Dispatchers.IO to transform
)
/* endregion ------------------------------------------------------------------------------------ */
