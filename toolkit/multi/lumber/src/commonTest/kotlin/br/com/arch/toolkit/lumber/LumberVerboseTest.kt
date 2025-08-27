package br.com.arch.toolkit.lumber

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class LumberVerboseTest {

    @BeforeTest
    @AfterTest
    fun setup() {
        Lumber.uprootAll()
    }

    @Test
    fun `verbose with simple message`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("normal text without arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "normal text without arguments",
                error = null
            )
        )
    }

    @Test
    fun `verbose with message and arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("this is a %s text with %d arguments", "formatted", 2)
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "this is a formatted text with 2 arguments",
                error = null
            )
        )
    }

    @Test
    fun `verbose with empty message and no arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll()
    }

    @Test
    fun `verbose with message and null arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("this is a %s text with %d arguments", null, null)
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "this is a null text with null arguments",
                error = null
            )
        )
    }

    @Test
    fun `verbose with message containing format specifiers but no arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("this is a %s text with %d arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "this is a %s text with %d arguments",
                error = null
            )
        )
    }

    @Test
    fun `verbose with more arguments than format specifiers`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("this is a %s text with %d arguments", "formatted", 2, "extra")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "this is a formatted text with 2 arguments",
                error = null
            )
        )
    }

    @Test
    fun `verbose with fewer arguments than format specifiers`() {
        val tree = TestTree()
        Lumber.plant(tree)
        assertFails {
            Lumber.verbose("this is a %s text with %d arguments", "formatted")
        }
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll()
    }

    @Test
    fun `verbose when isLoggable is false`() {
        val tree = TestTree(Lumber.Level.Verbose)
        Lumber.plant(tree)
        Lumber.verbose("normal text without arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll()
    }

    @Test
    fun `verbose when quiet is true`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.quiet(true)
        Lumber.verbose("normal text without arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll()
    }

    @Test
    fun `verbose with explicit tag`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.tag("MyTag").verbose("normal text without arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = "MyTag",
                message = "normal text without arguments",
                error = null
            )
        )
    }

    @Test
    fun `verbose with very long message exceeding MAX LOG LENGTH`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("a".repeat(MAX_LOG_LENGTH + 2000))
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag()?.let { "$it #0" } ?: "#0",
                message = "a".repeat(MAX_LOG_LENGTH),
                error = null
            ),
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag()?.let { "$it #1" } ?: "#1",
                message = "a".repeat(2000),
                error = null
            ))
    }

    @Test
    fun `verbose with message exactly MAX LOG LENGTH`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("a".repeat(MAX_LOG_LENGTH))
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "a".repeat(MAX_LOG_LENGTH),
                error = null
            )
        )
    }

    @Test
    fun `verbose with special characters in message`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("a\nb\tc")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "a\nb\tc",
                error = null
            )
        )
    }

    @Test
    fun `verbose with special characters in arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.verbose("a\nb\tc%s", "a\nb\tc")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "a\nb\tca\nb\tc",
                error = null
            )
        )
    }

    @Test
    fun `verbose with explicit tag and long message`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.tag("MyTag").verbose("a".repeat(MAX_LOG_LENGTH + 2000))
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = "MyTag #0",
                message = "a".repeat(MAX_LOG_LENGTH),
                error = null
            ),
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = "MyTag #1",
                message = "a".repeat(2000),
                error = null
            )
        )
    }

    @Test
    fun `verbose with message and exception`() {
        val tree = TestTree()
        val error = Exception("boom")
        Lumber.plant(tree)

        Lumber.verbose(error, "normal text without arguments")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "normal text without arguments\n\n${error.stackTraceToString()}",
                error = error
            )
        )
    }

    @Test
    fun `verbose with message arguments and exception`() {
        val tree = TestTree()
        val error = Exception("bad state")
        Lumber.plant(tree)

        Lumber.verbose(error, "this is a %s text with %d arguments", "formatted", 2)

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "this is a formatted text with 2 arguments\n\n${error.stackTraceToString()}",
                error = error
            )
        )
    }

    @Test
    fun `verbose with empty message and exception`() {
        val tree = TestTree()
        val error = Exception("x")
        Lumber.plant(tree)

        Lumber.verbose(error, "")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = error.stackTraceToString(),
                error = error
            )
        )
    }

    @Test
    fun `verbose with message and null arguments plus exception`() {
        val tree = TestTree()
        val error = Exception("npe")
        Lumber.plant(tree)

        Lumber.verbose(error, "this is a %s text with %d arguments", null, null)

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "this is a null text with null arguments\n\n${error.stackTraceToString()}",
                error = error
            )
        )
    }

    @Test
    fun `verbose with format specifiers but no arguments and exception`() {
        val tree = TestTree()
        val error = Exception("oops")
        Lumber.plant(tree)

        Lumber.verbose(error, "this is a %s text with %d arguments")

        assertEquals(listOf(tree), Lumber.forest())
        // Mirrors non-exception behavior: keep the raw message as-is
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "this is a %s text with %d arguments\n\n${error.stackTraceToString()}",
                error = error
            )
        )
    }

    @Test
    fun `verbose with more arguments than specifiers and exception`() {
        val tree = TestTree()
        val error = Exception("overflow")
        Lumber.plant(tree)

        Lumber.verbose(error, "this is a %s text with %d arguments", "formatted", 2, "extra")

        assertEquals(listOf(tree), Lumber.forest())
        // Extra args are ignored after formatting
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "this is a formatted text with 2 arguments\n\n${error.stackTraceToString()}",
                error = error
            )
        )
    }

    @Test
    fun `verbose with fewer arguments than specifiers and exception`() {
        val tree = TestTree()
        Lumber.plant(tree)

        assertFails {
            Lumber.verbose(
                Exception("missing"),
                "this is a %s text with %d arguments",
                "formatted"
            )
        }

        assertEquals(listOf(tree), Lumber.forest())
        // Nothing should be logged when formatting fails
        tree.assertAll()
    }

    @Test
    fun `verbose when isLoggable is false with exception`() {
        val tree = TestTree(Lumber.Level.Verbose)
        Lumber.plant(tree)

        Lumber.verbose(Exception("nope"), "normal text without arguments")

        assertEquals(listOf(tree), Lumber.forest())
        // isLoggable false -> do not log
        tree.assertAll()
    }

    @Test
    fun `verbose when quiet is true with exception`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.quiet(true)

        Lumber.verbose(Exception("quiet"), "normal text without arguments")

        assertEquals(listOf(tree), Lumber.forest())
        // quiet -> do not log
        tree.assertAll()
    }

    @Test
    fun `verbose with explicit tag and exception`() {
        val tree = TestTree()
        val error = Exception("tagged")
        Lumber.plant(tree)

        Lumber.tag("MyTag").verbose(error, "normal text without arguments")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = "MyTag",
                message = "normal text without arguments\n\n${error.stackTraceToString()}",
                error = error
            )
        )
    }

    @Test
    fun `verbose with special characters and exception`() {
        val tree = TestTree()
        val error = Exception("multiline")
        Lumber.plant(tree)

        Lumber.verbose(error, "a\nb\tc")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "a\nb\tc\n\n${error.stackTraceToString()}",
                error = error
            )
        )
    }

    @Test
    fun `verbose with special characters in arguments and exception`() {
        val tree = TestTree()
        val error = Exception("args")
        Lumber.plant(tree)

        Lumber.verbose(error, "a\nb\tc%s", "a\nb\tc")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = defaultTag(),
                message = "a\nb\tca\nb\tc\n\n${error.stackTraceToString()}",
                error = error
            )
        )
    }

    @Test
    fun `verbose with explicit tag message and arguments plus exception`() {
        val tree = TestTree()
        val error = Exception("bad-arg")
        Lumber.plant(tree)

        Lumber.tag("MyTag").verbose(error, "this is a %s text with %d arguments", "formatted", 2)

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            TestTree.Data(
                level = Lumber.Level.Verbose,
                tag = "MyTag",
                message = "this is a formatted text with 2 arguments\n\n${error.stackTraceToString()}",
                error = error
            )
        )
    }
}
