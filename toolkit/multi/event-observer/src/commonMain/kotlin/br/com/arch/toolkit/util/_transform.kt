@file:Suppress("Filename", "unused")

package br.com.arch.toolkit.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlin.coroutines.CoroutineContext

internal fun <T, R, X> Flow<Pair<T?, R?>>.applyTransformation(
    context: CoroutineContext,
    transform: Transform.Nullable<T, R, X>
) = flowOn(transform.dispatcher)
    .map(transform::apply)
    .filter { (_, isFailure) ->
        when (transform.failMode) {
            Transform.Mode.OMIT_WHEN_FAIL -> isFailure.not()
            Transform.Mode.NULL_WHEN_FAIL -> true
        }
    }
    .map { (result, _) -> result }
    .flowOn(context)

internal fun <T, R, X> Flow<Pair<T, R>>.applyTransformation(
    context: CoroutineContext,
    transform: Transform.NotNull<T, R, X>
) = flowOn(transform.dispatcher).mapNotNull { transform.apply(it).first }.flowOn(context)

sealed class Transform<T, R, X> {
    abstract val dispatcher: CoroutineDispatcher
    abstract val failMode: Mode
    abstract val func: suspend (T, R) -> X
    abstract val onErrorReturn: (suspend (Throwable) -> X)?

    internal suspend fun apply(data: Pair<T, R>) = runCatching { func(data.first, data.second) }
        .let { result ->
            val finalResult = result.exceptionOrNull()
                ?.let { error -> onErrorReturn?.runCatching { invoke(error) } }
                ?: result
            finalResult.getOrNull() to finalResult.isFailure
        }

    enum class Mode {
        OMIT_WHEN_FAIL,
        NULL_WHEN_FAIL
    }

    sealed class Nullable<T, R, X> : Transform<T?, R?, X?>() {
        class OmitFail<T, R, X>(
            override val dispatcher: CoroutineDispatcher= Dispatchers.IO,
            override val func: suspend (T?, R?) -> X?
        ) : Nullable<T, R, X>() {
            override val failMode: Mode = Mode.OMIT_WHEN_FAIL
            override val onErrorReturn: (suspend (Throwable) -> X?)? = null
        }

        class NullFail<T, R, X>(
            override val dispatcher: CoroutineDispatcher= Dispatchers.IO,
            override val func: suspend (T?, R?) -> X?
        ) : Nullable<T, R, X>() {
            override val failMode: Mode = Mode.NULL_WHEN_FAIL
            override val onErrorReturn: (suspend (Throwable) -> X?)? = null
        }

        class Fallback<T, R, X>(
            override val dispatcher: CoroutineDispatcher= Dispatchers.IO,
            override val func: suspend (T?, R?) -> X?,
            override val onErrorReturn: suspend (Throwable) -> X?
        ) : Nullable<T, R, X>() {
            override val failMode: Mode = Mode.OMIT_WHEN_FAIL
        }

        class Custom<T, R, X>(
            override val dispatcher: CoroutineDispatcher= Dispatchers.IO,
            override val failMode: Mode,
            override val func: suspend (T?, R?) -> X?,
            override val onErrorReturn: (suspend (Throwable) -> X?)?
        ) : Nullable<T, R, X>()
    }

    sealed class NotNull<T, R, X> : Transform<T, R, X>() {
        class OmitFail<T, R, X>(
            override val dispatcher: CoroutineDispatcher = Dispatchers.IO,
            override val func: suspend (T, R) -> X
        ) : NotNull<T, R, X>() {
            override val failMode: Mode = Mode.OMIT_WHEN_FAIL
            override val onErrorReturn: (suspend (Throwable) -> X)? = null
        }

        class Fallback<T, R, X>(
            override val dispatcher: CoroutineDispatcher= Dispatchers.IO,
            override val func: suspend (T, R) -> X,
            override val onErrorReturn: (suspend (Throwable) -> X)?
        ) : NotNull<T, R, X>() {
            override val failMode: Mode = Mode.OMIT_WHEN_FAIL
        }
    }
}
