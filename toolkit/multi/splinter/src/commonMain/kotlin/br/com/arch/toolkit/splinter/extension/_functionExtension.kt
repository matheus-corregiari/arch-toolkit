package br.com.arch.toolkit.splinter.extension

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
suspend fun <R> (suspend () -> R).invokeCatching() = runCatching { invoke() }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
suspend fun <A1, R> (suspend (A1) -> R).invokeCatching(data: A1) = runCatching { invoke(data) }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
suspend fun <A1, A2, R> (suspend (A1, A2) -> R).invokeCatching(
    data1: A1,
    data2: A2,
) = runCatching { invoke(data1, data2) }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
suspend fun <A1, A2, A3, R> (suspend (A1, A2, A3) -> R).invokeCatching(
    data1: A1,
    data2: A2,
    data3: A3,
) = runCatching { invoke(data1, data2, data3) }

/**
 * Calls invoke method inside a runCatching block
 *
 * @return kotlin.Result
 */
suspend fun <A1, A2, A3, A4, R> (suspend (A1, A2, A3, A4) -> R).invokeCatching(
    data1: A1,
    data2: A2,
    data3: A3,
    data4: A4,
) = runCatching { invoke(data1, data2, data3, data4) }
