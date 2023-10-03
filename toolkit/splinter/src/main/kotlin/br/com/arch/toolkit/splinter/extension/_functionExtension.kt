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
fun <T, R> ((T) -> R).invokeCatching(data: T) = runCatching { invoke(data) }

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
suspend fun <T, R> (suspend (T) -> R).invokeCatching(data: T) = runCatching { invoke(data) }