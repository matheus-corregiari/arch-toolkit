@file:Suppress("OPT_IN_USAGE")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.splinter.strategy.OneShot
import br.com.arch.toolkit.splinter.strategy.Strategy
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SplinterOneShotTest {

    @Test
    fun `Regular execution with default parameters`() = runTest {
        val splinter = successSplinter()
        assertEquals(0, splinter.resultHolder.fullColdFlow.count())

        assertEquals(4, splinter.execute().fullColdFlow.count())
        assertEquals(2_000L, currentTime)
        assertEquals(dataResultSuccess("ccc"), splinter.resultHolder.get())
        assertEquals(
            listOf(
                null,
                "aaa",
                "bbb",
                "ccc"
            ),
            splinter.resultHolder.fullColdFlow.toList().map {
                it.data
            }
        )
        splinter.kill()
    }

    @Test
    fun `Without min execution should finish immediately after request`() = runTest {
        val splinter = successSplinter(oneShot = { minDuration(0.milliseconds) })
        assertEquals(0, splinter.resultHolder.fullColdFlow.count())

        assertEquals(4, splinter.execute().fullColdFlow.count())
        assertEquals(2_000L, currentTime)
        assertEquals(dataResultSuccess("ccc"), splinter.resultHolder.get())
        assertEquals(
            listOf(
                null,
                "aaa",
                "bbb",
                "ccc"
            ),
            splinter.resultHolder.fullColdFlow.toList().map {
                it.data
            }
        )
        splinter.kill()
    }

    private fun TestScope.successSplinter(
        config: Splinter.Config.Builder<String>.() -> Unit = {},
        oneShot: OneShot.Config.Builder<String>.() -> Unit = {}
    ) = splinter<String>(
        id = "test",
        strategy = Strategy.oneShot {
            oneShot()
            request {
                sendSnapshot("aaa")
                logError("Error", IllegalStateException())
                delay(1.seconds)
                sendSnapshot("bbb")
                logInfo("Snapshot info")
                delay(1.seconds)
                "ccc"
            }
        },
        config = {
            scope(this@successSplinter)
            config()
        }
    )
}
