@file:Suppress("OPT_IN_USAGE")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.splinter.strategy.OneShot
import br.com.arch.toolkit.splinter.util.TestTree
import br.com.arch.toolkit.splinter.util.logListAllDefault
import br.com.arch.toolkit.splinter.util.logListWithoutMinDuration
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@RunWith(JUnit4::class)
class SplinterOneShotTest {

    private val tree: TestTree = TestTree()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
        Lumber.plant(tree)
    }

    @Before
    @After
    fun setup() = tree.history.clear()

    @Test
    fun `Regular execution with default parameters`() = runTest {
        val splinter = successSplinter()
        Assert.assertEquals(0, splinter.coldFlow.count())
        Assert.assertEquals(0, tree.history.size)
        Assert.assertEquals(3, splinter.execute().coldFlow.count())
        Assert.assertEquals(dataResultSuccess("ccc"), splinter.coldFlow.single())
        tree.assertAll(logListAllDefault)
    }

    @Test
    fun `Without min execution should finish immediately after request`() = runTest {
        val splinter = successSplinter(oneShot = { minDuration(0.milliseconds) })
        Assert.assertEquals(0, splinter.coldFlow.count())
        Assert.assertEquals(0, tree.history.size)
        Assert.assertEquals(3, splinter.execute().coldFlow.count())
        Assert.assertEquals(dataResultSuccess("ccc"), splinter.coldFlow.single())
        tree.assertAll(logListWithoutMinDuration)
    }

    private fun TestScope.successSplinter(
        config: Splinter<String>.Config.() -> Unit = {},
        oneShot: OneShot<String>.Config.() -> Unit = {},
    ) = splinter("test") {
        scope(backgroundScope)
        config()
        oneShotStrategy {
            oneShot()
            operation {
                sendSnapshot("aaa")
                logError("Error", IllegalStateException())
                delay(1.seconds)
                sendSnapshot("bbb")
                logInfo("Snapshot info")
                delay(1.seconds)
                "ccc"
            }
        }
    }
}
