@file:Suppress("LongMethod")
@file:OptIn(ExperimentalTime::class)

package br.com.arch.toolkit.splinter.strategy

import androidx.annotation.WorkerThread
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.splinter.ResponseDataHolder
import br.com.arch.toolkit.splinter.Splinter
import br.com.arch.toolkit.splinter.cache.CacheStrategy
import br.com.arch.toolkit.splinter.extension.error
import br.com.arch.toolkit.splinter.extension.info
import br.com.arch.toolkit.splinter.extension.invokeCatching
import br.com.arch.toolkit.splinter.extension.measureTimeResult
import br.com.arch.toolkit.splinter.strategy.OneShot.Config.Builder
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

/**
 * Strategy to make a single operation (one shot)
 *
 * This evil class can handle loading events, can handle cache strategies and treat errors
 *
 * @see Strategy
 */
class OneShot<T> private constructor(
    private val config: Config<T>
) : Strategy<T>() {

    @WorkerThread
    override suspend fun execute(
        holder: ResponseDataHolder<T>,
        dataChannel: Channel<DataResult<T>>,
        logChannel: Channel<Splinter.Message>
    ) {
        var remoteVersion: CacheStrategy.DataVersion? = null
        var localData: T? = null

        measureTimeResult(
            max = config.maxDuration,
            min = config.minDuration,
            log = { logChannel.info("[OneShot] $it") }
        ) {
            // Setup Cache config
            config.cacheStrategy?.let { cache ->
                logChannel.info("[OneShot] Cache - Set =)")
                val (version, data, interrupt) = handleCache(cache, logChannel)
                remoteVersion = version
                if (interrupt && data != null) return@measureTimeResult data
                else if (data != null) localData = data
            } ?: logChannel.info("[OneShot] Cache - Not set =(")

            // Emit first loading
            if (localData == null) logChannel.info("[OneShot] Emit - Loading")
            else logChannel.info("[OneShot] Emit - Loading with data! - $localData")
            dataChannel.send(dataResultLoading(localData))

            // Before Request
            config.beforeRequest?.invokeCatching()
                ?.onSuccess { logChannel.info("[OneShot] Before - Success!") }
                ?.onFailure { logChannel.error("[OneShot] Before - Error!", it) }

            // Request
            val data = requireNotNull(config.request) { "request() config is mandatory" }
                .invoke(Context(dataChannel = dataChannel, logChannel = logChannel))
            logChannel.info("[OneShot] Executed with success, data: $data")

            // After Request
            config.afterRequest?.invokeCatching(data)
                ?.onSuccess { logChannel.info("[OneShot] After - Success!") }
                ?.onFailure { logChannel.error("[OneShot] After - Error!", it) }

            // Save Cache
            remoteVersion?.runCatching { config.cacheStrategy?.update(this, data) }
                ?.onSuccess { logChannel.info("[OneShot] Save - Success!") }
                ?.onFailure { logChannel.error("[OneShot] Save - Error!", it) }

            return@measureTimeResult data
        }.onSuccess { data ->
            logChannel.info("[OneShot] Emit - Success - $data")
            dataChannel.send(dataResultSuccess(data))
        }.onFailure { error ->
            logChannel.error("[OneShot] Emit - Error", error)
            dataChannel.send(
                dataResultError(
                    error = config.mapError?.invokeCatching(error)?.getOrNull() ?: error,
                    data = localData ?: config.fallback?.invokeCatching(error)?.getOrNull(),
                )
            )
        }
    }

    @Suppress("ReturnCount")
    private suspend fun handleCache(
        cache: CacheStrategy<T>,
        logChannel: Channel<Splinter.Message>
    ): Triple<CacheStrategy.DataVersion?, T?, Boolean> {

        val remoteVersion = cache.newVersion()
        logChannel.info("[Cache] New version - $remoteVersion")

        cache.localData?.let { local ->
            logChannel.info("[Cache] Local Data - $local")

            val howToProceed = cache.howToProceed(remoteVersion, local)
            logChannel.info("[Cache] How to proceed - $howToProceed")
            when (howToProceed) {
                // This means the cache is still valid!
                CacheStrategy.HowToProceed.STOP_FLOW_AND_DISPATCH_CACHE -> {
                    logChannel.info(
                        "[Cache] Dispatching Local data and finish - Local version: ${cache.localVersion}"
                    )
                    return Triple(cache.localVersion, local, true)
                }

                // This means the cache is old, need to be refreshed, but we can still display it!
                CacheStrategy.HowToProceed.DISPATCH_CACHE -> {
                    logChannel.info("[Cache] Do some pre-load stuff")
                    return Triple(remoteVersion, local, false)
                }

                // This means the cache is to old, even to display it, so let's ignore it =(
                CacheStrategy.HowToProceed.IGNORE_CACHE ->
                    logChannel.info("[Cache] Not valid, let the show goes on")
            }
        } ?: logChannel.info("[Cache] No local data =(")

        return Triple(null, null, false)
    }

    companion object Creator {
        operator fun <T> invoke(config: Builder<T>.() -> Unit = {}) = OneShot(
            config = Builder<T>().apply(config).build()
        )
    }

    /**
     *
     */
    @ConsistentCopyVisibility
    data class Config<T> private constructor(
        val mapError: (suspend (Throwable) -> Throwable)?,
        val fallback: (suspend (Throwable) -> T)?,
        val beforeRequest: (suspend () -> Unit)?,
        val request: (suspend Context<T>.() -> T)?,
        val afterRequest: (suspend (T) -> Unit)?,
        val minDuration: Duration,
        val maxDuration: Duration,
        val cacheStrategy: CacheStrategy<T>?
    ) {

        /**
         *
         */
        class Builder<T> internal constructor() {

            private var mapError: (suspend (Throwable) -> Throwable)? = null
            private var fallback: (suspend (Throwable) -> T)? = null
            private var beforeRequest: (suspend () -> Unit)? = null
            private var request: (suspend Context<T>.() -> T)? = null
            private var afterRequest: (suspend (T) -> Unit)? = null
            private var minDuration: Duration = 200.milliseconds
            private var maxDuration: Duration = 10.minutes
            private var cache: CacheStrategy<T>? = null

            fun request(request: suspend Context<T>.() -> T) = apply { this.request = request }
            fun mapError(map: suspend (Throwable) -> Throwable) = apply { this.mapError = map }
            fun fallback(fallback: suspend (Throwable) -> T) = apply { this.fallback = fallback }
            fun beforeRequest(func: suspend () -> Unit) = apply { this.beforeRequest = func }
            fun afterRequest(func: suspend (T) -> Unit) = apply { this.afterRequest = func }
            fun minDuration(minDuration: Duration) = apply { this.minDuration = minDuration }
            fun maxDuration(maxDuration: Duration) = apply { this.maxDuration = maxDuration }
            fun cache(cache: CacheStrategy<T>) = apply { this.cache = cache }

            internal fun build() = Config(
                mapError = mapError,
                fallback = fallback,
                beforeRequest = beforeRequest,
                request = request,
                afterRequest = afterRequest,
                minDuration = minDuration,
                maxDuration = maxDuration,
                cacheStrategy = cache,
            )
        }
    }

    /**
     *
     */
    class Context<T> internal constructor(
        private val dataChannel: Channel<DataResult<T>>,
        private val logChannel: Channel<Splinter.Message>,
    ) {
        suspend fun sendSnapshot(data: T) {
            coroutineContext.ensureActive()
            logChannel.info("[OneShot] Emit Snapshot - $data")
            dataChannel.send(dataResultLoading(data))
        }

        suspend fun logInfo(message: String) {
            coroutineContext.ensureActive()
            logChannel.info("[OneShot] $message")
        }

        suspend fun logError(message: String, error: Throwable) {
            coroutineContext.ensureActive()
            logChannel.error("[OneShot] $message", error)
        }
    }
}
