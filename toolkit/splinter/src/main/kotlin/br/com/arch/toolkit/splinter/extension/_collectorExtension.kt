package br.com.arch.toolkit.splinter.extension

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.dataResultError
import br.com.arch.toolkit.result.dataResultLoading
import br.com.arch.toolkit.result.dataResultNone
import br.com.arch.toolkit.result.dataResultSuccess
import kotlinx.coroutines.flow.FlowCollector

/**
 * Emits a success data
 *
 * @see dataResultSuccess
 */
suspend fun <T> FlowCollector<DataResult<T>>.emitData(data: T) =
    emit(dataResultSuccess(data))

/**
 * Emits a loading data
 *
 * @see dataResultLoading
 */
suspend fun <T> FlowCollector<DataResult<T>>.emitLoading(data: T? = null) =
    emit(dataResultLoading(data))

/**
 * Emits a error data
 *
 * @see dataResultError
 */
suspend fun <T> FlowCollector<DataResult<T>>.emitError(error: Throwable, data: T? = null) =
    emit(dataResultError(error, data))

/**
 * Emits a none
 *
 * @see dataResultNone
 */
suspend fun <T> FlowCollector<DataResult<T>>.emitNone() = emit(dataResultNone())
