package br.com.arch.toolkit.splinter.extension

import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import kotlinx.coroutines.flow.FlowCollector

suspend fun <T> FlowCollector<DataResult<T>>.emitData(data: T) = emit(
    DataResult(data, null, DataResultStatus.SUCCESS)
)

suspend fun <T> FlowCollector<DataResult<T>>.emitLoading(data: T? = null) = emit(
    DataResult(data, null, DataResultStatus.LOADING)
)

suspend fun <T> FlowCollector<DataResult<T>>.emitError(error: Throwable, data: T? = null) = emit(
    DataResult(data, error, DataResultStatus.ERROR)
)