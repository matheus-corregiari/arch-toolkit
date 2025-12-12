package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level
import br.com.arch.toolkit.lumber.Lumber.Oak
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

abstract class LumberTest {

    @Suppress("unused", "ConvertSecondaryConstructorToPrimary")
    // NOTE do NOT delete this or wasm tests will start to crash
    constructor()

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
                tag = defaultTag()?.let { "$it #0" } ?: "#0",
                message = "a".repeat(MAX_LOG_LENGTH),
                error = null
            ),
            TestTree.Data(
                level = level,
                tag = defaultTag()?.let { "$it #1" } ?: "#1",
                message = "a".repeat(10),
                error = null
            )
        )
    }

    @Test
    fun `message long - quiet false - tag custom - error null - args empty`() {
        val tree = newTree()
        val msg = "a".repeat(MAX_LOG_LENGTH + 10)
        Lumber.tag("Custom").runLog(msg)
        tree.assertAll(
            TestTree.Data(
                level = level,
                tag = "Custom #0",
                message = "a".repeat(MAX_LOG_LENGTH),
                error = null
            ),
            TestTree.Data(
                level = level,
                tag = "Custom #1",
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
                tag = defaultTag()?.let { "$it #0" } ?: "#0",
                message = "a".repeat(MAX_LOG_LENGTH),
                error = null
            ),
            TestTree.Data(
                level = level,
                tag = defaultTag()?.let { "$it #1" } ?: "#1",
                message = "extra",
                error = null
            )
        )
    }

    @Test
    fun `message short - quiet false - tag default - error present - args empty`() {
        val tree = newTree()
        val ex = Throwable("boom")
        Lumber.maxLogLength("fail\n\n${ex.stackTraceToString()}".length).runLog(ex, "fail")
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
        val ex = Throwable("fail")
        Lumber.maxLogLength(ex.stackTraceToString().length).runLog(ex, "")
        tree.assertAll(
            TestTree.Data(level, defaultTag(), ex.stackTraceToString(), ex)
        )
    }

    @Test
    fun `message none - quiet false - tag default - error present - args empty`() {
        val tree = newTree()
        val ex = Throwable("fail")
        Lumber.maxLogLength(ex.stackTraceToString().length).runLog(ex)
        tree.assertAll(
            TestTree.Data(level, defaultTag(), ex.stackTraceToString(), ex)
        )
    }

    @Test
    fun `message short - quiet false - tag default - error present - args present`() {
        val tree = newTree()
        val ex = Throwable("boom")
        Lumber.maxLogLength("code 500\n\n${ex.stackTraceToString()}".length)
            .runLog(ex, "code %d", 500)
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
        val ex = Throwable("long boom")
        val msg = "a".repeat(MAX_LOG_LENGTH + 20)

        Lumber.runLog(ex, msg)

        tree.assertAll(
            *"$msg\n\n${ex.stackTraceToString()}"
                .chunked(MAX_LOG_LENGTH).mapIndexed { index, log ->
                    TestTree.Data(
                        level = level,
                        tag = defaultTag()?.let { "$it #$index" } ?: "#$index",
                        message = log,
                        error = ex
                    )
                }.toTypedArray()
        )
    }

    @Test
    fun `message long - quiet false - tag default - error present - args present`() {
        val tree = newTree()
        val ex = Throwable("long boom")
        val msg = "a".repeat(MAX_LOG_LENGTH) + "%s"

        Lumber.runLog(ex, msg, "XYZ")
        tree.assertAll(
            *"${"a".repeat(MAX_LOG_LENGTH)}XYZ\n\n${ex.stackTraceToString()}"
                .chunked(MAX_LOG_LENGTH).mapIndexed { index, log ->
                    TestTree.Data(
                        level = level,
                        tag = defaultTag()?.let { "$it #$index" } ?: "#$index",
                        message = log,
                        error = ex
                    )
                }.toTypedArray()
        )
    }

    @Test
    fun `message short - quiet true - tag default - error present - args empty`() {
        val tree = newTree()
        val ex = Throwable("quiet")
        Lumber.quiet(true).runLog(ex, "muted")
        tree.assertAll() // quiet cancela
    }

    @Test
    fun `message short - quiet false - tag custom - error present - args empty`() {
        val tree = newTree()
        val ex = Throwable("tagged")
        Lumber.tag("Custom").maxLogLength("oops\n\n${ex.stackTraceToString()}".length)
            .runLog(ex, "oops")
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
        val ex = Throwable("combo")
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
