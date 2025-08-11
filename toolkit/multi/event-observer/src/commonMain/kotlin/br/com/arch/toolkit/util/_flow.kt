package br.com.arch.toolkit.util

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.ObserveWrapper
import kotlinx.coroutines.flow.Flow

suspend fun <T> Flow<DataResult<T>>.unwrap(
    config: ObserveWrapper<T>.() -> Unit
) {
    val wrapper = ObserveWrapper<T>().apply(config)
    collect { wrapper.suspendFunc { handleResult(it) } }
}
