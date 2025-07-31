@file:OptIn(ExperimentalTime::class)

package br.com.arch.toolkit.splinter.extension

import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

internal suspend inline fun <T> measureTimeResult(
    max: Duration,
    min: Duration,
    noinline log: suspend (String) -> Unit,
    crossinline func: suspend () -> T
): Result<T> {
    val (value, duration) = measureTimedValue {
        runCatching { withTimeout(max) { func() } }
    }
    handleMinDuration(
        duration = duration,
        min = min,
        log = log
    )
    return value
}

internal suspend fun handleMinDuration(
    duration: Duration,
    min: Duration?,
    log: suspend (String) -> Unit
) {
    val delta = min?.let { (min - duration) } ?: Duration.ZERO
    when {
        // This means that we need to wait the delta time to reach the minDuration set inside config
        delta > Duration.ZERO -> {
            log("Execution time ${duration.inWholeMilliseconds}ms - Wait more ${delta.inWholeMilliseconds}ms")
            delay(delta)
        }

        // This means that the operation already surpassed the minDuration, so we don't need to wait
        delta <= Duration.ZERO -> log("Execution time ${duration.inWholeMilliseconds}ms")
    }
}
