package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.splinter.cache.CacheStrategy
import br.com.arch.toolkit.splinter.extension.invokeCatching
import br.com.arch.toolkit.splinter.strategy.Strategy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CancellationTest {
    @Test
    fun suspendedCallbacksPropagateCancellation() = runTest {
        val callback: suspend () -> Unit = { throw CancellationException("stop") }
        assertFailsWith<CancellationException> { callback.invokeCatching() }
        val withArgument: suspend (Int) -> Unit = { throw CancellationException("stop") }
        assertFailsWith<CancellationException> { withArgument.invokeCatching(1) }
    }

    @Test
    fun cacheCancellationMustNotBecomeACacheMiss() = runTest {
        val cache = object : CacheStrategy<Int>("test") {
            override val localData = 1
            override val localVersion = DataVersion("old")
            override suspend fun save(version: DataVersion, data: Int) = Unit
            override suspend fun delete() = Unit
            override suspend fun newVersion() = DataVersion("new")
            override suspend fun isLocalDisplayable(version: DataVersion, data: Int) = true
            override suspend fun isLocalValid(version: DataVersion, data: Int): Boolean =
                throw CancellationException("stop cache")
        }
        assertFailsWith<CancellationException> { cache.howToProceed(cache.newVersion(), 1) }
    }

    @Test
    fun cancellationInBeforeRequestPreventsTheRequest() = runTest {
        var requested = false
        val operation = operation(
            Strategy.oneShot {
                beforeRequest { throw CancellationException("stop") }
                request {
                    requested = true
                    1
                }
            }
        )
        try {
            operation.execute()
            runCurrent()
            assertFalse(requested)
            assertFalse(operation.resultHolder.get().isError)
            assertFalse(operation.isRunning)
        } finally {
            operation.kill()
        }
    }

    @Test
    fun cancelledCallbacksDoNotPublishTerminalResults() = runTest {
        for (stage in listOf("after", "mapper", "fallback")) {
            val operation = operation(
                Strategy.oneShot {
                    minDuration(Duration.ZERO)
                    request { if (stage == "after") 1 else error("request failure") }
                    afterRequest { throw CancellationException("after") }
                    mapError {
                        if (stage == "mapper") throw CancellationException("mapper")
                        it
                    }
                    fallback { throw CancellationException("fallback") }
                }
            )
            try {
                operation.execute()
                runCurrent()
                assertFalse(operation.resultHolder.get().isSuccess, stage)
                assertFalse(operation.resultHolder.get().isError, stage)
                assertFalse(operation.isRunning, stage)
            } finally {
                operation.kill()
            }
        }
    }

    @Test
    fun configuredErrorMinimumUsesVirtualTime() = runTest {
        val operation = operation(
            Strategy.oneShot {
                minDuration(onSuccess = Duration.ZERO, onError = 100.milliseconds)
                request { error("request failure") }
            }
        )
        try {
            operation.execute()
            runCurrent()
            advanceTimeBy(99)
            runCurrent()
            assertFalse(operation.resultHolder.get().isError)
            advanceTimeBy(1)
            runCurrent()
            assertTrue(operation.resultHolder.get().isError)
        } finally {
            operation.kill()
        }
    }

    @Test
    fun cancelDuringRequestThenRestart() = runTest {
        var calls = 0
        val operation = operation(
            Strategy.oneShot {
                minDuration(Duration.ZERO)
                request {
                    calls++
                    delay(100)
                    calls
                }
            }
        )
        try {
            operation.execute()
            runCurrent()
            operation.cancel()
            runCurrent()
            assertFalse(operation.isRunning)
            assertFalse(operation.resultHolder.get().isError)
            operation.execute()
            runCurrent()
            advanceTimeBy(100)
            runCurrent()
            assertEquals(2, operation.resultHolder.data)
            assertTrue(operation.resultHolder.get().isSuccess)
        } finally {
            operation.kill()
        }
    }

    @Test
    fun minimumSuccessDurationIsConfigurableAndCancellable() = runTest {
        val operation = operation(Strategy.oneShot { request { 1 } })
        try {
            operation.execute()
            runCurrent()
            assertTrue(operation.resultHolder.get().isLoading)
            advanceTimeBy(100)
            operation.cancel()
            runCurrent()
            advanceTimeBy(200)
            runCurrent()
            assertFalse(operation.resultHolder.get().isSuccess)
            operation.execute()
            runCurrent()
            advanceTimeBy(200)
            runCurrent()
            assertTrue(operation.resultHolder.get().isSuccess)
        } finally {
            operation.kill()
        }
        val immediate = operation(
            Strategy.oneShot {
                minDuration(Duration.ZERO)
                request { 2 }
            }
        )
        try {
            immediate.execute()
            runCurrent()
            assertEquals(2, immediate.resultHolder.data)
        } finally {
            immediate.kill()
        }
    }

    @Test
    fun cancellingAwaitCancelsTheWaiterWithoutCancellingTheRequest() = runTest {
        val release = CompletableDeferred<Unit>()
        val operation = operation(
            Strategy.oneShot {
                minDuration(Duration.ZERO)
                request {
                    release.await()
                    1
                }
            }
        )
        try {
            operation.execute()
            runCurrent()
            val waiter = async { operation.await() }
            runCurrent()
            waiter.cancel()
            assertFailsWith<CancellationException> { waiter.await() }
            assertTrue(operation.isRunning)
            release.complete(Unit)
            runCurrent()
            assertEquals(1, operation.resultHolder.data)
        } finally {
            operation.kill()
        }
    }

    @Test
    fun pollingCancellationDoesNotBecomeAnErrorOrRetry() = runTest {
        var calls = 0
        val operation = operation(
            Strategy.polling {
                request {
                    calls++
                    throw CancellationException("stop")
                }
                stopOnError { false }
                limitLoopCount(3)
            }
        )
        try {
            operation.execute()
            runCurrent()
            assertEquals(1, calls)
            assertFalse(operation.resultHolder.get().isError)
            assertFalse(operation.isRunning)
        } finally {
            operation.kill()
        }
    }

    @Test
    fun requestTimeoutStillProducesAnError() = runTest {
        val operation = operation(
            Strategy.oneShot {
                maxDuration(50.milliseconds)
                request {
                    delay(100)
                    1
                }
            }
        )
        try {
            operation.execute()
            runCurrent()
            advanceTimeBy(50)
            runCurrent()
            assertTrue(operation.resultHolder.get().isError)
        } finally {
            operation.kill()
        }
    }

    private fun TestScope.operation(strategy: Strategy<Int>) = splinter("test", strategy) {
        scope(this@operation)
        logging(false)
        stop(Splinter.StopPolicy.UntilRequest)
    }
}
