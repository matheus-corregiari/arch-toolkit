package br.com.arch.toolkit.splinter.strategy

import androidx.annotation.WorkerThread
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.splinter.Splinter
import br.com.arch.toolkit.splinter.cache.CacheStrategy
import br.com.arch.toolkit.splinter.extension.emitData
import br.com.arch.toolkit.splinter.extension.emitError
import br.com.arch.toolkit.splinter.extension.emitLoading
import br.com.arch.toolkit.splinter.extension.invokeCatching
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

/**
 * Strategy to make a single request (one shot)
 *
 * This evil class can handle loading events, can handle cache strategies and treat errors
 *
 * @see Strategy
 */
class OneShot<RESULT : Any> : Strategy<RESULT>() {

    /**
     * Block that configure how this Strategy will work ^^
     *
     * @see OneShot.Config
     */
    private val config = Config()
    fun config(config: Config.() -> Unit) = apply { this.config.run(config) }

    @WorkerThread
    @Suppress("LongMethod")
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
                    executor.logger.info("\t[OneShot] Cache - Not set =(")

                    remoteVersion = cache.newVersion()
                    executor.logger.info("\t\t[Cache] - New version - $remoteVersion")

                    cache.localData?.let { local ->
                        executor.logger.info("\t\t[Cache] - Local Data - $local")

                        val howToProceed = cache.howToProceed(remoteVersion, local)
                        executor.logger.info("\t\t[Cache] - How to proceed - $howToProceed")
                        when (howToProceed) {
                            /* This means the cache is still valid! */
                            CacheStrategy.HowToProceed.STOP_FLOW_AND_DISPATCH_CACHE -> {
                                remoteVersion = cache.localVersion
                                executor.logger.info(
                                    "\t\t[Cache] - " +
                                        "Dispatching Local data and finish - " +
                                        "Local version: $remoteVersion"
                                )
                                return@withTimeout local
                            }

                            /* This means the cache is old, need to be refreshed, but we can still display it! */
                            CacheStrategy.HowToProceed.DISPATCH_CACHE -> {
                                executor.logger.info("\t\t[Cache] Do some pre-load stuff")
                                localData = local
                            }

                            /* This means the cache is to old, even to display it, so let's ignore it =( */
                            CacheStrategy.HowToProceed.IGNORE_CACHE -> {
                                executor.logger.info("\t\t[Cache] - Not valid, let the show goes on")
                                /* Nothing */
                            }
                        }
                    } ?: executor.logger.info("\t\t[OneShot] Cache - No local data =(")
                } ?: executor.logger.info("\t[OneShot] Cache - Not set =(")

                if (localData == null) {
                    executor.logger.info("\t[OneShot] Emit - Loading")
                } else {
                    executor.logger.info("\t[OneShot] Emit - Loading with data! - $localData")
                }
                collector.emitLoading(localData)

                config.beforeRequest?.invokeCatching()
                    ?.onSuccess { executor.logger.info("\t[OneShot] Before Request - Success!") }
                    ?.onFailure { executor.logger.error("\t[OneShot] Before Request - Error!", it) }

                requireNotNull(config.request) { " " }.invoke()
            }
        }.onSuccess { data ->
            executor.logger.info("\t[OneShot] Executed with success, data: $data")
            config.afterRequest?.invokeCatching(data)
                ?.onSuccess { executor.logger.info("\t[OneShot] After Request - Success!") }
                ?.onFailure { executor.logger.error("\t[OneShot] After Request - Error!", it) }

            handleMinDuration(timeInMillisBeforeStart, executor)

            executor.logger.info("\t[OneShot] Emit - Success Data! - $data")
            collector.emitData(data)

            remoteVersion?.runCatching { config.cacheStrategy?.update(this, data) }
                ?.onSuccess { executor.logger.info("\t\t[Cache] Save - Success!") }
                ?.onFailure { executor.logger.error("\t\t[Cache] Save - Error!", it) }
        }.onFailure { error ->
            handleMinDuration(timeInMillisBeforeStart, executor)
            if (executor.status != DataResultStatus.SUCCESS) {
                executor.logger.error("\t[OneShot] Emit - Error! - $error")
                flowError(error, collector, executor)
            } else {
                executor.logger.warn("\t[OneShot] Got some error but I did not emit it - $error")
            }
        }
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
    private suspend fun handleMinDuration(start: Long, executor: Splinter<RESULT>) {
        val operationDuration = System.currentTimeMillis() - start
        val delta = config.minDuration.inWholeMilliseconds - operationDuration

        when {
            /* This means that we need to wait the delta time to reach the minDuration set inside config*/
            delta > 0 -> {
                executor.logger.info("\t[OneShot] Execution time ${operationDuration}ms, need to wait more ${delta}ms")
                delay(delta)
            }

            /* This means that the operation already surpassed the minDuration, so we don't need to wait */
            delta <= 0 -> executor.logger.info("\t[OneShot] Execution time ${operationDuration}ms")
        }
    }

    /**
     *
     */
    inner class Config internal constructor() {
        internal var mapError: (suspend (Throwable) -> Throwable)? = null
        internal var fallback: (suspend (Throwable) -> RESULT)? = null
        internal var beforeRequest: (suspend () -> Unit)? = null
        internal var request: (suspend () -> RESULT)? = null
        internal var afterRequest: (suspend (RESULT) -> Unit)? = null
        internal var minDuration: Duration = 200.milliseconds
        internal var maxDuration: Duration = 10.minutes
        internal var cacheStrategy: CacheStrategy<RESULT>? = null

        fun request(request: suspend () -> RESULT) = apply { this.request = request }
    }
}
