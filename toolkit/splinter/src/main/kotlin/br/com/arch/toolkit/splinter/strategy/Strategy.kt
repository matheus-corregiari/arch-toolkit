package br.com.arch.toolkit.splinter.strategy

import androidx.annotation.WorkerThread
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.splinter.Splinter
import br.com.arch.toolkit.splinter.extension.emitError
import kotlinx.coroutines.flow.FlowCollector

sealed class Strategy<RESULT : Any> {
    @WorkerThread
    internal abstract suspend fun execute(
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>
    )

    @WorkerThread
    internal open suspend fun flowError(
        error: Throwable,
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>
    ) = collector.emitError(error)

    @WorkerThread
    internal open suspend fun majorError(
        error: Throwable,
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>
    ) = flowError(error, collector, executor)

    companion object {
        fun <T : Any> oneShot(config: OneShot<T>.Config.() -> Unit) = OneShot<T>().config(config)
    }
}

