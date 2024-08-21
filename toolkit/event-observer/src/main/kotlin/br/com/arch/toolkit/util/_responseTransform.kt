@file:Suppress("Filename", "unused")

package br.com.arch.toolkit.util

import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlin.coroutines.CoroutineContext

@Experimental
internal fun <T, R, X> Flow<DataResult<Pair<T, R>>>.applyTransformation(
    context: CoroutineContext,
    transform: ResponseTransform<T, R, X>
) = flowOn(transform.dispatcher).mapNotNull(transform::apply).flowOn(context)

@Experimental
sealed class ResponseTransform<T, R, X> {
    abstract val dispatcher: CoroutineDispatcher
    abstract val failMode: Mode
    abstract val func: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
    abstract val onErrorReturn: (suspend (Throwable) -> DataResult<X>)?

    internal suspend fun apply(data: DataResult<Pair<T, R>>) =
        runCatching { func(data) }.let { result ->
            val finalResult = result.exceptionOrNull()?.let { error ->
                onErrorReturn?.runCatching { invoke(error) }
            } ?: result

            when {
                finalResult.isFailure -> when (failMode) {
                    Mode.OMIT_WHEN_FAIL -> null
                    Mode.ERROR_STATUS_WHEN_FAIL ->
                        dataResultError(finalResult.exceptionOrNull())
                }

                else -> finalResult.getOrNull()
            }
        }

    @Experimental
    enum class Mode {
        ERROR_STATUS_WHEN_FAIL,
        OMIT_WHEN_FAIL
    }

    @Experimental
    class StatusFail<T, R, X>(
        override val dispatcher: CoroutineDispatcher,
        override val func: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
    ) : ResponseTransform<T, R, X>() {
        override val failMode: Mode = Mode.ERROR_STATUS_WHEN_FAIL
        override val onErrorReturn: (suspend (Throwable) -> DataResult<X>)? = null
    }

    @Experimental
    class OmitFail<T, R, X>(
        override val dispatcher: CoroutineDispatcher,
        override val func: suspend (DataResult<Pair<T, R>>) -> DataResult<X>
    ) : ResponseTransform<T, R, X>() {
        override val failMode: Mode = Mode.OMIT_WHEN_FAIL
        override val onErrorReturn: (suspend (Throwable) -> DataResult<X>)? = null
    }

    @Experimental
    class Fallback<T, R, X>(
        override val dispatcher: CoroutineDispatcher,
        override val func: suspend (DataResult<Pair<T, R>>) -> DataResult<X>,
        override val onErrorReturn: suspend (Throwable) -> DataResult<X>
    ) : ResponseTransform<T, R, X>() {
        override val failMode: Mode = Mode.ERROR_STATUS_WHEN_FAIL
    }

    @Experimental
    class Custom<T, R, X>(
        override val dispatcher: CoroutineDispatcher,
        override val failMode: Mode,
        override val func: suspend (DataResult<Pair<T, R>>) -> DataResult<X>,
        override val onErrorReturn: suspend (Throwable) -> DataResult<X>
    ) : ResponseTransform<T, R, X>()
}
