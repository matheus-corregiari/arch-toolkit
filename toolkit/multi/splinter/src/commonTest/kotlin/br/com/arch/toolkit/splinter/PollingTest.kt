package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.splinter.exception.PollingLimitLoopReachedException
import br.com.arch.toolkit.splinter.exception.PollingMaxErrorStreakReachedException
import br.com.arch.toolkit.splinter.strategy.Polling
import br.com.arch.toolkit.util.dataResultNone
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PollingTest {

    @Test
    fun retriesUntilThePredicateMatchesAndPreservesCallbackOrder() = runTest {
        var calls = 0
        val callbacks = mutableListOf<String>()
        val results = execute {
            beforeRequest { callbacks += "before" }
            request { ++calls }
            afterRequest { callbacks += "after:$it" }
            shouldStop { it == 2 }
        }
        assertEquals(listOf("before", "after:1", "before", "after:2"), callbacks)
        assertEquals(
            listOf(DataResultStatus.LOADING, DataResultStatus.LOADING, DataResultStatus.SUCCESS),
            results.map { it.status }
        )
        assertEquals(listOf(null, 1, 2), results.map { it.data })
    }

    @Test
    fun callbackFailuresDoNotDiscardASuccessfulRequest() = runTest {
        val results = execute {
            beforeRequest { error("before callback") }
            request { 1 }
            afterRequest { error("after callback") }
        }
        assertEquals(DataResultStatus.SUCCESS, results.last().status)
        assertEquals(1, results.last().data)
    }

    @Test
    fun predicateFailureContinuesUntilTheConfiguredLimit() = runTest {
        var calls = 0
        val results = execute {
            request { ++calls }
            shouldStop { error("predicate") }
            limitLoopCount(2)
        }
        assertEquals(2, calls)
        assertEquals(DataResultStatus.ERROR, results.last().status)
        assertIs<PollingLimitLoopReachedException>(results.last().error)
    }

    @Test
    fun successfulRequestsResetTheErrorStreak() = runTest {
        var calls = 0
        val results = execute {
            request {
                calls++
                check(calls % 2 == 0) { "retry" }
                calls
            }
            stopOnError { false }
            maxErrorStreak(2)
            shouldStop { it == 4 }
        }
        assertEquals(4, calls)
        assertEquals(DataResultStatus.SUCCESS, results.last().status)
    }

    @Test
    fun consecutiveFailuresStopAtTheLimitWithFallbackData() = runTest {
        var calls = 0
        val results = execute {
            request {
                calls++
                error("retry")
            }
            stopOnError { false }
            maxErrorStreak(2)
            fallback { 42 }
        }
        assertEquals(2, calls)
        assertIs<PollingMaxErrorStreakReachedException>(results.last().error)
        assertEquals(42, results.last().data)
    }

    private suspend fun execute(
        config: Polling.Config.Builder<Int>.() -> Unit
    ): List<DataResult<Int>> {
        val results = Channel<DataResult<Int>>(Channel.UNLIMITED)
        val logs = Channel<Splinter.Message>(Channel.UNLIMITED)
        val holder = object : ResponseDataHolder<Int> {
            override fun get() = dataResultNone<Int>()
            override val status get() = get().status
            override val data: Int? = null
            override val error: Throwable? = null
            override val liveFlow = emptyFlow<DataResult<Int>>()
            override val liveColdFlow = liveFlow
            override val fullFlow = liveFlow
            override val fullColdFlow = liveFlow
        }
        try {
            Polling(config).execute(holder, results, logs)
            results.close()
            return results.receiveAsFlow().toList()
        } finally {
            results.cancel()
            logs.cancel()
        }
    }
}
