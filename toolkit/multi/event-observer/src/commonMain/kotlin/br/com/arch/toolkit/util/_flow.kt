package br.com.arch.toolkit.util

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.ObserveWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

suspend fun <T> Flow<DataResult<T>>.unwrap(
    config: ObserveWrapper<T>.() -> Unit
) {
    val wrapper = ObserveWrapper<T>().apply(config)
    collect { wrapper.suspendFunc { handleResult(it) } }
}

fun <T> Flow<T>.valueOrNull(): T? = when (this) {
    is StateFlow<T> -> value
    is SharedFlow<T> -> replayCache.lastOrNull()
    else -> null
}
