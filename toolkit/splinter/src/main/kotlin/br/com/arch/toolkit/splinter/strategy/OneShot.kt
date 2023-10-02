package br.com.arch.toolkit.splinter.strategy

import androidx.annotation.WorkerThread
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.splinter.Splinter
import br.com.arch.toolkit.splinter.cache.CacheStrategy
import br.com.arch.toolkit.splinter.extension.emitData
import br.com.arch.toolkit.splinter.extension.emitError
import br.com.arch.toolkit.splinter.extension.emitLoading
import br.com.arch.toolkit.splinter.extension.invokeCatching
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.withTimeout

class OneShot<RESULT : Any> : Strategy<RESULT>() {

    private val config = Config()
    fun config(config: Config.() -> Unit) = apply { this.config.run(config) }

    @WorkerThread
    override suspend fun execute(
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>
    ) {
        var remoteVersion: CacheStrategy.DataVersion? = null
        var localData = executor.data
        val timeInMillisBeforeStart = System.currentTimeMillis()

        kotlin.runCatching {
            withTimeout(config.maxDuration.inWholeMilliseconds) {
                config.cacheStrategy?.let { cache ->
                    remoteVersion = cache.newVersion()

                    cache.localData?.let { local ->
                        val howToProceed = cache.howToProceed(remoteVersion, local)
                        //LOG
                        when (howToProceed) {

                            /**/
                            CacheStrategy.HowToProceed.STOP_FLOW_AND_DISPATCH_CACHE -> {
                                //LOG
                                remoteVersion = cache.localVersion
                                return@withTimeout local
                            }

                            /**/
                            CacheStrategy.HowToProceed.DISPATCH_CACHE -> {
                                //LOG
                                localData = local
                            }

                            /**/
                            CacheStrategy.HowToProceed.IGNORE_CACHE -> {
                                //LOG
                                /* Nothing */
                            }
                        }
                    } //?: LOG
                } //?: LOG

                collector.emitLoading(localData)

                config.beforeRequest?.invokeCatching()?.onFailure { /* LOG */ }

                requireNotNull(config.request) { " " }.invoke()
            }
        }.onSuccess { data ->
            config.afterRequest?.invokeCatching(data)?.onFailure { /* LOG */ }

            handleMinDuration(timeInMillisBeforeStart, System.currentTimeMillis())

            collector.emitData(data)

            remoteVersion?.runCatching { config.cacheStrategy?.update(this, data) } // ?: LOG
        }.onFailure { error ->
            handleMinDuration(timeInMillisBeforeStart, System.currentTimeMillis())
            if (executor.status != DataResultStatus.SUCCESS) {
                flowError(error, collector, executor)
            }
        }
    }

    override suspend fun flowError(
        error: Throwable,
        collector: FlowCollector<DataResult<RESULT>>,
        executor: Splinter<RESULT>
    ) = collector.emitError(
        error = config.mapError?.invoke(error) ?: error,
        data = executor.data ?: config.fallback?.invoke(error)
    )

    private suspend fun handleMinDuration(start: Long, end: Long) {
        val operationDuration = end - start
        val delta = config.minDuration.inWholeMilliseconds - operationDuration
        when {
            /* */
            delta > 0 -> delay(delta) /*LOG*/
            /* */
            delta < 0 -> Unit /*LOG*/
            /* */
            else -> Unit /*LOG*/
        }
    }

    inner class Config internal constructor() {
        internal var mapError: (suspend (Throwable) -> Throwable)? = null
        internal var fallback: (suspend (Throwable) -> RESULT)? = null
        internal var beforeRequest: (suspend () -> Unit)? = null
        internal var request: (suspend () -> RESULT)? = null
        internal var afterRequest: (suspend (RESULT) -> Unit)? = null
        internal var minDuration: Duration = 300.milliseconds
        internal var maxDuration: Duration = 10.minutes
        internal var cacheStrategy: CacheStrategy<RESULT>? = null

        fun request(request: suspend () -> RESULT) = apply { this.request = request }
    }

}