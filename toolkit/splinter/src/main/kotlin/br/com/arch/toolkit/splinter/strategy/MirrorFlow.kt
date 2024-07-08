package br.com.arch.toolkit.splinter.strategy

import androidx.annotation.WorkerThread
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.splinter.Splinter
import br.com.arch.toolkit.splinter.extension.emitError
import br.com.arch.toolkit.splinter.extension.emitLoading
import br.com.arch.toolkit.splinter.extension.invokeCatching
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch

/**
 * Strategy to mirror results from another flow using the DataResult
 *
 * @see Strategy
 */
class MirrorFlow<RESULT : Any> : Strategy<RESULT>() {

    /**
     * Block that configure how this Strategy will work ^^
     *
     * @see MirrorFlow.Config
     */
    private val config = Config()
    fun config(config: Config.() -> Unit) = apply { this.config.run(config) }

    @WorkerThread
    override suspend fun execute(
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>
    ) {
        val onError: suspend (Throwable) -> Unit = { error ->
            if (executor.status != DataResultStatus.SUCCESS) {
                executor.logError("\t[MirrorFlow] Emit - Error! - $error")
                flowError(error, collector, executor)
            } else {
                executor.logWarning("\t[MirrorFlow] Got some error but I did not emit it - $error")
            }
        }

        kotlin.runCatching {
            collector.emitLoading(executor.data)
            requireNotNull(config.flow) { "Flow value mist be set!" }.invoke()
                .catch { error -> onError.invoke(error) }
                .collect { data ->
                    executor.logInfo("\t[MirrorFlow] Received new data, data: $data")
                    executor.logInfo("\t[MirrorFlow] Emit - Loading Data! - $data")
                    collector.emitLoading(data)
                }
            executor.logInfo("\t[MirrorFlow] Finished flow!")
            if (executor.error == null) {
                executor.logInfo("\t[MirrorFlow] Emit - Success Data! - ${executor.data}")
                collector.emit(dataResultSuccess(executor.data))
            }
        }.onFailure { error -> onError.invoke(error) }
    }

    override suspend fun flowError(
        error: Throwable,
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>
    ) = collector.emitError(
        error = config.mapError?.invoke(error) ?: error,
        data = executor.data ?: config.fallback?.invokeCatching(error)?.getOrNull()
    )

    /**
     *
     */
    inner class Config internal constructor() {
        internal var mapError: (suspend (Throwable) -> Throwable)? = null
        internal var fallback: (suspend (Throwable) -> RESULT)? = null
        internal var flow: (suspend () -> Flow<RESULT>)? = null

        fun flow(flow: suspend () -> Flow<RESULT>) = apply { this.flow = flow }
    }
}
