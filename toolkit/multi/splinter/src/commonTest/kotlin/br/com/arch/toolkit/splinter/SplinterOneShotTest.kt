@file:Suppress("OPT_IN_USAGE")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.splinter.strategy.OneShot
import br.com.arch.toolkit.splinter.strategy.Strategy
import br.com.arch.toolkit.splinter.util.TestTree
import br.com.arch.toolkit.splinter.util.logListAllDefault
import br.com.arch.toolkit.splinter.util.logListWithoutMinDuration
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Ignore
class SplinterOneShotTest {

    private val tree: TestTree = TestTree()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
        Lumber.plant(tree)
    }

    @BeforeTest
    @AfterTest
    fun setup() = tree.history.clear()

    @Test
    fun `Regular execution with default parameters`() = runTest {
        val splinter = successSplinter()
        assertEquals(0, splinter.resultHolder.fullColdFlow.count())
        assertEquals(0, tree.history.size)
        assertEquals(4, splinter.execute().fullColdFlow.count())
        assertEquals(dataResultSuccess("ccc"), splinter.resultHolder.get())
        tree.assertAll(logListAllDefault)
    }

    @Test
    fun `Without min execution should finish immediately after request`() = runTest {
        val splinter = successSplinter(oneShot = { minDuration(0.milliseconds) })
        assertEquals(0, splinter.resultHolder.fullColdFlow.count())
        assertEquals(0, tree.history.size)
        assertEquals(4, splinter.execute().fullColdFlow.count())
        assertEquals(dataResultSuccess("ccc"), splinter.resultHolder.get())
        tree.assertAll(logListWithoutMinDuration)
    }

    private fun TestScope.successSplinter(
        config: Splinter.Config.Builder<String>.() -> Unit = {},
        oneShot: OneShot.Config.Builder<String>.() -> Unit = {},
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
            scope(backgroundScope)
            config()
        }
    )
}
