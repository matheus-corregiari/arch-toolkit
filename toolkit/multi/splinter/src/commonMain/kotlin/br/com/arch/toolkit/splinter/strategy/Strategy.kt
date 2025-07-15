package br.com.arch.toolkit.splinter.strategy

import androidx.annotation.WorkerThread
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.splinter.Splinter
import br.com.arch.toolkit.splinter.extension.emitError
import kotlinx.coroutines.flow.FlowCollector

/**
 * Main class that defines how the Splinter will work to emit the events!
 */
abstract class Strategy<RESULT : Any> {

    /**
     * Here, all the magic happens
     *
     * This method will implement how the job inside splinter should run
     */
    @WorkerThread
    abstract suspend fun execute(
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>,
    )

    /**
     * In case of error inside the job, some uncaught exception on flow, this method will trigger inside splinter
     */
    @WorkerThread
    open suspend fun flowError(
        error: Throwable,
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>,
    ) = collector.emitError(error)

    /**
     * In case of major error inside the job, some uncaught exception on job, this method will trigger inside splinter
     */
    @WorkerThread
    open suspend fun majorError(
        error: Throwable,
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>,
    ) = flowError(error, collector, executor)

    companion object {
        /**
         * Helper method that creates a OneShot strategy and configure it
         *
         * @param config - Block that will configure the oneShot strategy
         *
         * @return br.com.arch.toolkit.splinter.strategy.OneShot
         */
        fun <T : Any> oneShot(config: OneShot<T>.Config.() -> Unit) = OneShot<T>().config(config)

        /**
         * Helper method that creates a MirrorFlow strategy and configure it
         *
         * @param config - Block that will configure the mirrorFlow strategy
         *
         * @return br.com.arch.toolkit.splinter.strategy.MirrorFlow
         */
        fun <T : Any> mirrorFlow(config: MirrorFlow<T>.Config.() -> Unit) =
            MirrorFlow<T>().config(config)
    }
}
