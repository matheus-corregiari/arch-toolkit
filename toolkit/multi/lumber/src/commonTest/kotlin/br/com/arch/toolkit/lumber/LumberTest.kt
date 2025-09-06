package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level
import br.com.arch.toolkit.lumber.Lumber.Oak
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

sealed class LumberTest {

    abstract val level: Level
    abstract fun Oak.runLog(message: String, vararg args: Any?)
    abstract fun Oak.runLog(throwable: Throwable)
    abstract fun Oak.runLog(throwable: Throwable, message: String, vararg args: Any?)

    @BeforeTest
    @AfterTest
    fun reset() {
        Lumber.uprootAll()
    }

    private fun newTree() = TestTree().also {
        Lumber.uproot(it)
        Lumber.plant(it)
    }

    @Test
    fun `message short - quiet false - tag default - error null - args empty`() {
        val tree = newTree()
        Lumber.runLog("hello")
        tree.assertAll(
            TestTree.Data(level, defaultTag(), "hello", null)
        )
    }

    @Test
    fun `message short - quiet false - tag default - error null - args present`() {
        val tree = newTree()
        Lumber.runLog("value=%s", 123)
        tree.assertAll(
            TestTree.Data(level, defaultTag(), "value=123", null)
        )
    }

    @Test
    fun `message empty - quiet false - tag default - error null - args empty`() {
        val tree = newTree()
        Lumber.runLog("")
        tree.assertAll() // não loga nada
    }

    @Test
    fun `message long - quiet false - tag default - error null - args empty`() {
        val tree = newTree()
        val msg = "a".repeat(MAX_LOG_LENGTH + 10)
        Lumber.runLog(msg)
        tree.assertAll(
            TestTree.Data(
                level = level,
                tag = "${defaultTag()} #0",
                message = "a".repeat(MAX_LOG_LENGTH),
                error = null
            ),
            TestTree.Data(
                level = level,
                tag = "${defaultTag()} #1",
                message = "a".repeat(10),
                error = null
            )
        )
    }

    @Test
    fun `message long - quiet false - tag default - error null - args present`() {
        val tree = newTree()
        val msg = "a".repeat(MAX_LOG_LENGTH) + "%s"
        Lumber.runLog(msg, "extra")
        tree.assertAll(
            TestTree.Data(
                level = level,
                tag = "${defaultTag()} #0",
                message = "a".repeat(MAX_LOG_LENGTH),
                error = null
            ),
            TestTree.Data(
                level = level,
                tag = "${defaultTag()} #1",
                message = "extra",
                error = null
            )
        )
    }

    @Test
    fun `message short - quiet false - tag default - error present - args empty`() {
        val tree = newTree()
        val ex = Exception("boom")
        Lumber.runLog(ex, "fail")
        tree.assertAll(
            TestTree.Data(
                level = level,
                tag = defaultTag(),
                message = "fail\n\n${ex.stackTraceToString()}",
                error = ex
            )
        )
    }

    @Test
    fun `message empty - quiet false - tag default - error present - args empty`() {
        val tree = newTree()
        val ex = Exception("fail")
        Lumber.runLog(ex, "")
        tree.assertAll(
            TestTree.Data(level, defaultTag(), ex.stackTraceToString(), ex)
        )
    }

    @Test
    fun `message none - quiet false - tag default - error present - args empty`() {
        val tree = newTree()
        val ex = Exception("fail")
        Lumber.runLog(ex)
        tree.assertAll(
            TestTree.Data(level, defaultTag(), ex.stackTraceToString(), ex)
        )
    }

    @Test
    fun `message short - quiet false - tag default - error present - args present`() {
        val tree = newTree()
        val ex = Exception("boom")
        Lumber.runLog(ex, "code %d", 500)
        tree.assertAll(
            TestTree.Data(
                level = level,
                tag = defaultTag(),
                message = "code 500\n\n${ex.stackTraceToString()}",
                error = ex
            )
        )
    }

    @Test
    fun `message short - quiet false - tag custom - error null - args empty`() {
        val tree = newTree()
        Lumber.tag("Custom").runLog("tagged")
        tree.assertAll(
            TestTree.Data(level, "Custom", "tagged", null)
        )
        // pro próximo log volta pro default
        Lumber.runLog("next")
        tree.assertAll(
            TestTree.Data(level, "Custom", "tagged", null),
            TestTree.Data(level, defaultTag(), "next", null)
        )
    }

    @Test
    fun `message short - quiet true - tag default - error null - args empty`() {
        val tree = newTree()
        Lumber.quiet(true).runLog("muted")
        tree.assertAll() // não loga
        Lumber.runLog("next")
        tree.assertAll(
            TestTree.Data(level, defaultTag(), "next", null)
        )
    }

    @Test
    fun `message short - quiet true - tag custom - error null - args empty`() {
        val tree = newTree()
        Lumber.tag("Custom").quiet(true).runLog("skipped")
        tree.assertAll() // não loga
        // próximo volta ao normal
        Lumber.runLog("active")
        tree.assertAll(
            TestTree.Data(level, defaultTag(), "active", null)
        )
    }

    @Test
    fun `message empty - quiet false - tag default - error null - args present`() {
        val tree = newTree()
        Lumber.runLog("", "ignored")
        tree.assertAll() // nada logado, string vazia sempre suprime
    }

    @Test
    fun `message long - quiet false - tag default - error present - args empty`() {
        val tree = newTree()
        val ex = Exception("long boom")
        val msg = "a".repeat(MAX_LOG_LENGTH + 20)

        Lumber.runLog(ex, msg)

        tree.assertAll(
            TestTree.Data(
                level = level,
                tag = "${defaultTag()} #0",
                message = "a".repeat(MAX_LOG_LENGTH),
                error = ex
            ),
            TestTree.Data(
                level = level,
                tag = "${defaultTag()} #1",
                message = "a".repeat(20) + "\n\n${ex.stackTraceToString()}",
                error = ex
            )
        )
    }

    @Test
    fun `message long - quiet false - tag default - error present - args present`() {
        val tree = newTree()
        val ex = Exception("long boom")
        val msg = "a".repeat(MAX_LOG_LENGTH) + "%s"

        Lumber.runLog(ex, msg, "XYZ")

        tree.assertAll(
            TestTree.Data(
                level = level,
                tag = "${defaultTag()} #0",
                message = "a".repeat(MAX_LOG_LENGTH),
                error = ex
            ),
            TestTree.Data(
                level = level,
                tag = "${defaultTag()} #1",
                message = "XYZ\n\n${ex.stackTraceToString()}",
                error = ex
            )
        )
    }

    @Test
    fun `message short - quiet true - tag default - error present - args empty`() {
        val tree = newTree()
        val ex = Exception("quiet")
        Lumber.quiet(true).runLog(ex, "muted")
        tree.assertAll() // quiet cancela
    }

    @Test
    fun `message short - quiet false - tag custom - error present - args empty`() {
        val tree = newTree()
        val ex = Exception("tagged")
        Lumber.tag("Custom").runLog(ex, "oops")
        tree.assertAll(
            TestTree.Data(level, "Custom", "oops\n\n${ex.stackTraceToString()}", ex)
        )
        // próximo volta pro default
        Lumber.runLog("next")
        tree.assertAll(
            TestTree.Data(
                level = level,
                tag = "Custom",
                message = "oops\n\n${ex.stackTraceToString()}",
                error = ex
            ),
            TestTree.Data(
                level = level,
                tag = defaultTag(),
                message = "next",
                error = null
            )
        )
    }

    @Test
    fun `message short - quiet true - tag custom - error present - args empty`() {
        val tree = newTree()
        val ex = Exception("combo")
        Lumber.tag("Custom").quiet(true).runLog(ex, "hidden")
        tree.assertAll() // nada
        Lumber.runLog("next")
        tree.assertAll(
            TestTree.Data(level, defaultTag(), "next", null)
        )
    }

    @Test
    fun `message short - quiet false - tag default - error null - args empty - isLoggable false`() {
        val tree = TestTree(level) // força não loggable
        Lumber.plant(tree)

        Lumber.runLog("ignored")

        tree.assertAll() // não loga nada
    }

    @Test
    fun `message short - quiet false - tag empty - error null - args empty`() {
        val tree = newTree()

        // seta tag vazia
        Lumber.tag("").runLog("empty tag")

        // como tag vazia não é aceita, deve cair pro defaultTag()
        tree.assertAll(
            TestTree.Data(level = level, tag = defaultTag(), message = "empty tag", error = null)
        )

        // pro próximo log continua default
        Lumber.runLog("next")
        tree.assertAll(
            TestTree.Data(level = level, tag = defaultTag(), message = "empty tag", error = null),
            TestTree.Data(level = level, tag = defaultTag(), message = "next", error = null)
        )
    }
}
