package br.com.arch.toolkit.splinter.extension

import kotlinx.coroutines.CancellationException

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
fun <R> (() -> R).invokeCatching() = runCatching { invoke() }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
fun <A1, R> ((A1) -> R).invokeCatching(data: A1) = runCatching { invoke(data) }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
suspend fun <R> (suspend () -> R).invokeCatching() = catchingCancellable { invoke() }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
suspend fun <A1, R> (suspend (A1) -> R).invokeCatching(data: A1) =
    catchingCancellable { invoke(data) }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
suspend fun <A1, A2, R> (suspend (A1, A2) -> R).invokeCatching(
    data1: A1,
    data2: A2
) = catchingCancellable { invoke(data1, data2) }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
suspend fun <A1, A2, A3, R> (suspend (A1, A2, A3) -> R).invokeCatching(
    data1: A1,
    data2: A2,
    data3: A3
) = catchingCancellable { invoke(data1, data2, data3) }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
suspend fun <A1, A2, A3, A4, R> (suspend (A1, A2, A3, A4) -> R).invokeCatching(
    data1: A1,
    data2: A2,
    data3: A3,
    data4: A4
) = catchingCancellable { invoke(data1, data2, data3, data4) }

/** Captures request failures while preserving structured coroutine cancellation. */
internal inline fun <T> catchingCancellable(block: () -> T): Result<T> =
    runCatching(block).onFailure { if (it is CancellationException) throw it }
