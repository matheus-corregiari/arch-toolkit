package br.com.arch.toolkit.sample.feature.toolkit

import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SplinterDemoTest {
    @Test
    fun requestShowsSnapshotsFailureCancellationAndRetry() = runTest {
        val demo = SplinterDemo(backgroundScope)
        try {
            demo.load()
            runCurrent()
            assertTrue(demo.request.value.isLoading)
            assertEquals("1 / 2", demo.request.value.data)
            demo.cancelRequest()
            runCurrent()
            demo.load(fail = true)
            runCurrent()
            advanceTimeBy(600)
            runCurrent()
            assertTrue(demo.request.value.isError)
            demo.load()
            runCurrent()
            advanceTimeBy(600)
            runCurrent()
            assertEquals("2 / 2", demo.request.value.data)
            advanceTimeBy(600)
            runCurrent()
            assertTrue(demo.request.value.isSuccess)
            assertEquals("Arch Toolkit", demo.request.value.data)
        } finally {
            demo.close()
        }
    }

    @Test
    fun pollingCanBeCancelledAndRepeatedAfterSuccess() = runTest {
        val demo = SplinterDemo(backgroundScope)
        try {
            demo.poll()
            runCurrent()
            demo.cancelPolling()
            runCurrent()
            repeat(2) {
                demo.poll()
                runCurrent()
                advanceTimeBy(600)
                runCurrent()
                assertEquals("1 / 3", demo.polling.value.data)
                advanceTimeBy(1_200)
                runCurrent()
                assertTrue(demo.polling.value.isSuccess)
                assertEquals("3 / 3", demo.polling.value.data)
            }
        } finally {
            demo.close()
        }
    }
}
