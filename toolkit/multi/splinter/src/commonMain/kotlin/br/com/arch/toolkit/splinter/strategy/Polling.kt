@file:Suppress(
    "LongMethod",
    "ComplexMethod",
    "TooManyFunctions",
    "ComplexCondition",
    "unused"
)

package br.com.arch.toolkit.splinter.strategy

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.splinter.ResponseDataHolder
import br.com.arch.toolkit.splinter.Splinter
import br.com.arch.toolkit.splinter.exception.PollingLimitLoopReachedException
import br.com.arch.toolkit.splinter.exception.PollingMaxErrorStreakReachedException
import br.com.arch.toolkit.splinter.extension.error
import br.com.arch.toolkit.splinter.extension.info
import br.com.arch.toolkit.splinter.extension.invokeCatching
import br.com.arch.toolkit.splinter.extension.measureTimeResult
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class Polling<T> private constructor(
    private val config: Config<T>
) : Strategy<T>() {

    override suspend fun execute(
        holder: ResponseDataHolder<T>,
        dataChannel: Channel<DataResult<T>>,
        logChannel: Channel<Splinter.Message>
    ) {
        // Auxiliary Variables
        var shouldStop = false
        var loopCounter = 0
        var requestErrorCounter = 0

        /**
         * Emit first loading with the previous last known data and error
         */
        logChannel.info("[MirrorFlow] Emit - Loading")
        dataChannel.trySend(dataResultLoading())

        /**
         * Start the request loop!
         */
        while (shouldStop.not() && coroutineContext.isActive && holder.get().isSuccess.not()) {
            /**
             * Verify loop limit before start loop!
             */
            if (loopCounter >= config.limitLoopCount) {
                logChannel.info("Limit Loop count reached without match condition!")
                val error = PollingLimitLoopReachedException(
                    message = "Polling stopped! Limit loop count reached! ${config.limitLoopCount}"
                )
                val errorData = dataResultError<T>(
                    error = config.mapError?.invokeCatching(error)?.getOrNull() ?: error,
                    data = holder.data ?: config.fallback?.invokeCatching(error)?.getOrNull()
                )
                dataChannel.send(errorData)
                shouldStop = true
                continue
            }

            val result = measureTimeResult(
                max = config.maxExecutionTime,
                min = config.minExecutionTime,
                log = { logChannel.info("[Polling] $it") }) {
                loopCounter++

                logChannel.info("Request looped - $loopCounter")

                // Delay Before Request
                config.delayStrategy.delayIfPossible(DelayStrategy.BEFORE_REQUEST, config.delay)

                // Before Request
                config.beforeRequest?.invokeCatching()
                    ?.onSuccess { logChannel.info("[OneShot] Before - Success!") }
                    ?.onFailure { logChannel.error("[OneShot] Before - Error!", it) }

                // Request
                val data =
                    requireNotNull(config.request) { "request() config is mandatory" }.invoke()
                logChannel.info("[OneShot] Executed with success, data: $data")

                // After Request
                config.afterRequest?.invokeCatching(data)
                    ?.onSuccess { logChannel.info("[OneShot] After - Success!") }
                    ?.onFailure { logChannel.error("[OneShot] After - Error!", it) }

                // Delay After Request
                config.delayStrategy.delayIfPossible(DelayStrategy.AFTER_REQUEST, config.delay)

                return@measureTimeResult data
            }

            with(result) {
                /**
                 * The request executed with success!! Yeeeey!
                 */
                onSuccess {
                    /**
                     * Evaluate if we must stop
                     */
                    shouldStop = config.shouldStopAfterLoad.invokeCatching(it)
                        .onFailure { logChannel.info("Error inside ShouldStopAfterLoad block!") }
                        .getOrDefault(false)

                    if (shouldStop.not()) {
                        logChannel.info("[MirrorFlow] Emit - Still Loading")
                        dataChannel.trySend(dataResultLoading(it))
                    } else {
                        logChannel.info("[MirrorFlow] Emit - Success")
                        dataChannel.trySend(dataResultSuccess(it))
                    }

                    if (requestErrorCounter > 0) {
                        logChannel.info("Error streak invalidated!")
                        requestErrorCounter = 0
                    }
                }

                /**
                 * Oh Oh... Something goes wrong... sorry...
                 */
                onFailure { error ->
                    requestErrorCounter++
                    logChannel.info("Flow got some error inside loop[$loopCounter] - $error")
                    logChannel.info("Error streak number: $requestErrorCounter")

                    /**
                     * Evaluate if we must stop
                     */
                    shouldStop =
                        config.stopOnError.runCatching { invoke(error) }.getOrDefault(false)

                    when {
                        shouldStop.not() && config.maxErrorStreak > 0 && requestErrorCounter >= config.maxErrorStreak -> {
                            logChannel.info("Max error streak reached: $requestErrorCounter")
                            val newError = PollingMaxErrorStreakReachedException(
                                message = "Polling stopped! Max error streak reached! $requestErrorCounter",
                                cause = config.mapError?.invokeCatching(error)?.getOrNull() ?: error
                            )
                            val errorData = dataResultError(
                                error = config.mapError?.invokeCatching(newError)?.getOrNull()
                                    ?: newError,
                                data = holder.data ?: config.fallback?.invokeCatching(newError)
                                    ?.getOrNull()
                            )
                            dataChannel.send(errorData)
                            shouldStop = true
                        }

                        holder.get().isSuccess.not() -> {
                            val data =
                                holder.data ?: config.fallback?.invokeCatching(error)?.getOrNull()
                            val formattedError = config.mapError?.invoke(error) ?: error
                            if (shouldStop) {
                                logChannel.info("Loop stopped: $formattedError")
                                dataChannel.send(dataResultError(formattedError, data))
                            } else {
                                logChannel.info("Emit still loading!")
                                dataChannel.send(dataResultLoading(data, formattedError))
                            }
                        }

                        else -> {
                            logChannel.info("Something really awkward is going on, prey!")
                            shouldStop = true
                        }
                    }
                }
            }
        }
        logChannel.info("[Polling] Stopped at loop - $loopCounter")
    }

    /**
     *
     */
    companion object Creator {
        operator fun <T> invoke(config: Config.Builder<T>.() -> Unit = {}) = Polling(
            config = Config.Builder<T>().apply(config).build()
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
        val request: (suspend () -> T)?,
        val afterRequest: (suspend (T) -> Unit)?,
        val stopOnError: (error: Throwable) -> Boolean,
        val maxErrorStreak: Int,
        val delay: Duration,
        val minExecutionTime: Duration,
        val maxExecutionTime: Duration,
        val delayStrategy: DelayStrategy,
        val shouldStopAfterLoad: suspend (T) -> Boolean,
        val limitLoopCount: Long,
    ) {

        /**
         *
         */
        class Builder<T> internal constructor() {

            private var mapError: (suspend (Throwable) -> Throwable)? = null
            private var fallback: (suspend (Throwable) -> T)? = null
            private var beforeRequest: (suspend () -> Unit)? = null
            private var request: (suspend () -> T)? = null
            private var afterRequest: (suspend (T) -> Unit)? = null
            private var stopOnError: (error: Throwable) -> Boolean = { true }
            private var maxErrorStreak: Int = 0
            private var delay: Duration = Duration.ZERO
            private var minExecutionTime: Duration = 200.milliseconds
            private var maxExecutionTime: Duration = 5.minutes
            private var delayStrategy: DelayStrategy = DelayStrategy.AFTER_REQUEST
            private var shouldStopAfterLoad: suspend (T) -> Boolean = { true }
            private var limitLoopCount: Long = Long.MAX_VALUE

            fun request(request: suspend () -> T) = apply { this.request = request }
            fun mapError(map: suspend (Throwable) -> Throwable) = apply { this.mapError = map }
            fun fallback(fallback: suspend (Throwable) -> T) = apply { this.fallback = fallback }
            fun beforeRequest(func: suspend () -> Unit) = apply { this.beforeRequest = func }
            fun afterRequest(func: suspend (T) -> Unit) = apply { this.afterRequest = func }

            fun stopOnError(stopOnError: (error: Throwable) -> Boolean) =
                apply { this.stopOnError = stopOnError }

            fun maxErrorStreak(maxErrorStreak: Int) = apply {
                require(maxErrorStreak >= 0) { "maxErrorStreak must be >= 0" }
                this.maxErrorStreak = maxErrorStreak
            }

            fun delay(millis: Long) = delay(millis.milliseconds)
            fun delay(delay: Duration) = apply { this.delay = delay }

            fun minExecutionPerRequest(duration: Long) =
                minExecutionPerRequest(duration.milliseconds)

            fun minExecutionPerRequest(duration: Duration) {
                require(duration > Duration.ZERO) { "Timeout duration should be > 0" }
                minExecutionTime = duration
            }

            fun timeoutPerRequest(duration: Long) = timeoutPerRequest(duration.milliseconds)

            fun timeoutPerRequest(duration: Duration) = apply {
                require(duration > Duration.ZERO) { "Timeout duration should be > 0" }
                maxExecutionTime = duration
            }

            fun delayStrategy(strategy: DelayStrategy) = apply { this.delayStrategy = strategy }

            fun shouldStop(shouldStop: suspend (T) -> Boolean) =
                apply { this.shouldStopAfterLoad = shouldStop }

            fun limitLoopCount(limit: Long) = apply { this.limitLoopCount = limit }

            internal fun build() = Config(
                mapError = mapError,
                fallback = fallback,
                beforeRequest = beforeRequest,
                request = request,
                afterRequest = afterRequest,
                stopOnError = stopOnError,
                maxErrorStreak = maxErrorStreak,
                delay = delay,
                minExecutionTime = minExecutionTime,
                maxExecutionTime = maxExecutionTime,
                delayStrategy = delayStrategy,
                shouldStopAfterLoad = shouldStopAfterLoad,
                limitLoopCount = limitLoopCount,
            )
        }
    }

    enum class DelayStrategy {
        AFTER_REQUEST, BEFORE_REQUEST;

        suspend fun delayIfPossible(targetStrategy: DelayStrategy, delay: Duration) {
            if (this == targetStrategy && delay.inWholeMilliseconds > 0) {
                kotlinx.coroutines.delay(timeMillis = delay.inWholeMilliseconds)
            }
        }
    }
}
