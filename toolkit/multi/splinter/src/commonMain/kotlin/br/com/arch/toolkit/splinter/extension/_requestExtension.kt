package br.com.arch.toolkit.splinter.extension

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.measureTimedValue

internal suspend fun <T> measureTimeResult(
    max: Duration,
    minSuccess: Duration,
    minError: Duration,
    log: suspend (String) -> Unit,
    func: suspend () -> T
): Result<T> = coroutineScope {
    // Deadlines share the coroutine clock used by the request's delays and timeout.
    val successDeadline = launch(start = CoroutineStart.UNDISPATCHED) { delay(minSuccess) }
    val errorDeadline = if (minError == minSuccess) {
        successDeadline
    } else {
        launch(start = CoroutineStart.UNDISPATCHED) { delay(minError) }
    }
    try {
        val (value, duration) = measureTimedValue {
            runCatching { withTimeout(max) { func() } }.onFailure { error ->
                if (error is CancellationException) {
                    // A request timeout is a failure; cancellation of the caller must escape.
                    currentCoroutineContext().ensureActive()
                    if (error !is TimeoutCancellationException) throw error
                }
            }
        }
        val minimum = if (value.isSuccess) minSuccess else minError
        log("Execution time ${duration.inWholeMilliseconds}ms - Minimum $minimum")
        (if (value.isSuccess) successDeadline else errorDeadline).join()
        value
    } finally {
        successDeadline.cancel()
        errorDeadline.cancel()
    }
}
