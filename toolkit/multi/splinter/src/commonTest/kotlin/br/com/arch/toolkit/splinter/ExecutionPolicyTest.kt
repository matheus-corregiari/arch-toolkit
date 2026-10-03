package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.splinter.strategy.Strategy
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.time.Duration

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ExecutionPolicyTest {
    @Test
    fun pollingCanRunAgainAfterSuccess() = runTest {
        var calls = 0
        val operation = splinter(
            "polling",
            Strategy.polling {
                request { ++calls }
            }
        ) {
            scope(this@runTest)
            logging(false)
            stop(Splinter.StopPolicy.UntilRequest)
        }
        try {
            repeat(2) {
                operation.execute()
                runCurrent()
                advanceTimeBy(200)
                runCurrent()
                assertEquals(it + 1, calls)
            }
        } finally {
            operation.kill()
        }
    }

    @Test
    fun overlappingRequestsRespectEachExecutionPolicy() = runTest {
        for (policy in Splinter.ExecutionPolicy.entries) {
            var calls = 0
            val release = CompletableDeferred<Unit>()
            val operation = splinter(
                "policy",
                Strategy.oneShot {
                    minDuration(Duration.ZERO)
                    request {
                        val call = ++calls
                        release.await()
                        call
                    }
                }
            ) {
                scope(this@runTest)
                logging(false)
                stop(Splinter.StopPolicy.UntilRequest)
                policy(policy)
            }
            try {
                operation.execute()
                runCurrent()
                operation.execute()
                runCurrent()
                val expectedRunning = when (policy) {
                    Splinter.ExecutionPolicy.ParallelQueue,
                    Splinter.ExecutionPolicy.CancelWhenHasRunningBeforeStart -> 2
                    else -> 1
                }
                assertEquals(expectedRunning, calls, policy.name)
                release.complete(Unit)
                runCurrent()
                val expectedTotal = if (policy ==
                    Splinter.ExecutionPolicy.IgnoreWhenHasRunningOperations
                ) {
                    1
                } else {
                    2
                }
                assertEquals(expectedTotal, calls, policy.name)
                assertFalse(operation.isRunning, policy.name)
            } finally {
                operation.kill()
            }
        }
    }

    @Test
    fun historyIsBoundedAndStillKeepsTheCurrentResult() = runTest {
        val operation = splinter(
            "history",
            Strategy.oneShot {
                minDuration(Duration.ZERO)
                request {
                    repeat(10) { sendSnapshot(it) }
                    10
                }
            }
        ) {
            scope(this@runTest)
            logging(false)
            stop(Splinter.StopPolicy.UntilRequest)
            history(data = 2, logs = 3, extraBufferCapacity = 0)
        }
        try {
            operation.execute()
            runCurrent()
            assertEquals(listOf(9, 10), operation.dataFlow.replayCache.map { it.data })
            assertEquals(3, operation.logFlow.replayCache.size)
            assertEquals(10, operation.resultHolder.data)
        } finally {
            operation.kill()
        }
    }
}
