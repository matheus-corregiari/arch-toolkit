package br.com.arch.toolkit.lumber

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class LumberInfoTest {

    @BeforeTest
    @AfterTest
    fun setup() {
        Lumber.uprootAll()
    }

    @Test
    fun `info with simple message`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("normal text without arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-simple-message",
                    message = "normal text without arguments",
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with message and arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("this is a %s text with %d arguments", "formatted", 2)
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-message-and-arguments",
                    message = "this is a formatted text with 2 arguments",
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with empty message and no arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(emptyList())
    }

    @Test
    fun `info with message and null arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("this is a %s text with %d arguments", null, null)
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-message-and-null-arguments",
                    message = "this is a null text with null arguments",
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with message containing format specifiers but no arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("this is a %s text with %d arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-message-containing-format-specifiers-but-no-arguments",
                    message = "this is a %s text with %d arguments",
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with more arguments than format specifiers`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("this is a %s text with %d arguments", "formatted", 2, "extra")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-more-arguments-than-format-specifiers",
                    message = "this is a formatted text with 2 arguments",
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with fewer arguments than format specifiers`() {
        val tree = TestTree()
        Lumber.plant(tree)
        assertFails {
            Lumber.info("this is a %s text with %d arguments", "formatted")
        }
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(emptyList())
    }

    @Test
    fun `info when isLoggable is false`() {
        val tree = TestTree(Lumber.Level.Info)
        Lumber.plant(tree)
        Lumber.info("normal text without arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(emptyList())
    }

    @Test
    fun `info when quiet is true`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.quiet(true)
        Lumber.info("normal text without arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(emptyList())
    }

    @Test
    fun `info with explicit tag`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.tag("MyTag").info("normal text without arguments")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "MyTag",
                    message = "normal text without arguments",
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with very long message exceeding MAX LOG LENGTH`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("a".repeat(6000))
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-very-long-message-exceeding-MAX-LOG-LENGTH #0",
                    message = "a".repeat(4000),
                    error = null
                ),
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-very-long-message-exceeding-MAX-LOG-LENGTH #1",
                    message = "a".repeat(2000),
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with message exactly MAX LOG LENGTH`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("a".repeat(4000))
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-message-exactly-MAX-LOG-LENGTH",
                    message = "a".repeat(4000),
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with special characters in message`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("a\nb\tc")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-special-characters-in-message",
                    message = "a\nb\tc",
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with special characters in arguments`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.info("a\nb\tc%s", "a\nb\tc")
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-special-characters-in-arguments",
                    message = "a\nb\tca\nb\tc",
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with explicit tag and long message`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.tag("MyTag").info("a".repeat(6000))
        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "MyTag #0",
                    message = "a".repeat(4000),
                    error = null
                ),
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "MyTag #1",
                    message = "a".repeat(2000),
                    error = null
                )
            )
        )
    }

    @Test
    fun `info with message and exception`() {
        val tree = TestTree()
        val error = IllegalStateException("boom")
        Lumber.plant(tree)

        Lumber.info(error, "normal text without arguments")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-message-and-exception",
                    message = "normal text without arguments\n\n${error.stackTraceToString()}",
                    error = error
                )
            )
        )
    }

    @Test
    fun `info with message arguments and exception`() {
        val tree = TestTree()
        val error = RuntimeException("bad state")
        Lumber.plant(tree)

        Lumber.info(error, "this is a %s text with %d arguments", "formatted", 2)

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-message-arguments-and-exception",
                    message = "this is a formatted text with 2 arguments\n\n${error.stackTraceToString()}",
                    error = error
                )
            )
        )
    }

    @Test
    fun `info with empty message and exception`() {
        val tree = TestTree()
        val error = IllegalArgumentException("x")
        Lumber.plant(tree)

        Lumber.info(error, "")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-empty-message-and-exception",
                    message = error.stackTraceToString(),
                    error = error
                )
            )
        )
    }

    @Test
    fun `info with message and null arguments plus exception`() {
        val tree = TestTree()
        val error = NullPointerException("npe")
        Lumber.plant(tree)

        Lumber.info(error, "this is a %s text with %d arguments", null, null)

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-message-and-null-arguments-plus-exception",
                    message = "this is a null text with null arguments\n\n${error.stackTraceToString()}",
                    error = error
                )
            )
        )
    }

    @Test
    fun `info with format specifiers but no arguments and exception`() {
        val tree = TestTree()
        val error = RuntimeException("oops")
        Lumber.plant(tree)

        Lumber.info(error, "this is a %s text with %d arguments")

        assertEquals(listOf(tree), Lumber.forest())
        // Mirrors non-exception behavior: keep the raw message as-is
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-format-specifiers-but-no-arguments-and-exception",
                    message = "this is a %s text with %d arguments\n\n${error.stackTraceToString()}",
                    error = error
                )
            )
        )
    }

    @Test
    fun `info with more arguments than specifiers and exception`() {
        val tree = TestTree()
        val error = RuntimeException("overflow")
        Lumber.plant(tree)

        Lumber.info(error, "this is a %s text with %d arguments", "formatted", 2, "extra")

        assertEquals(listOf(tree), Lumber.forest())
        // Extra args are ignored after formatting
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-more-arguments-than-specifiers-and-exception",
                    message = "this is a formatted text with 2 arguments\n\n${error.stackTraceToString()}",
                    error = error
                )
            )
        )
    }

    @Test
    fun `info with fewer arguments than specifiers and exception`() {
        val tree = TestTree()
        Lumber.plant(tree)

        assertFails {
            Lumber.info(
                IllegalStateException("missing"),
                "this is a %s text with %d arguments",
                "formatted"
            )
        }

        assertEquals(listOf(tree), Lumber.forest())
        // Nothing should be logged when formatting fails
        tree.assertAll(emptyList())
    }

    @Test
    fun `info when isLoggable is false with exception`() {
        val tree = TestTree(Lumber.Level.Info)
        Lumber.plant(tree)

        Lumber.info(IllegalStateException("nope"), "normal text without arguments")

        assertEquals(listOf(tree), Lumber.forest())
        // isLoggable false -> do not log
        tree.assertAll(emptyList())
    }

    @Test
    fun `info when quiet is true with exception`() {
        val tree = TestTree()
        Lumber.plant(tree)
        Lumber.quiet(true)

        Lumber.info(IllegalStateException("quiet"), "normal text without arguments")

        assertEquals(listOf(tree), Lumber.forest())
        // quiet -> do not log
        tree.assertAll(emptyList())
    }

    @Test
    fun `info with explicit tag and exception`() {
        val tree = TestTree()
        val error = IllegalStateException("tagged")
        Lumber.plant(tree)

        Lumber.tag("MyTag").info(error, "normal text without arguments")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "MyTag",
                    message = "normal text without arguments\n\n${error.stackTraceToString()}",
                    error = error
                )
            )
        )
    }

    @Test
    fun `info with special characters and exception`() {
        val tree = TestTree()
        val error = RuntimeException("multiline")
        Lumber.plant(tree)

        Lumber.info(error, "a\nb\tc")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-special-characters-and-exception",
                    message = "a\nb\tc\n\n${error.stackTraceToString()}",
                    error = error
                )
            )
        )
    }

    @Test
    fun `info with special characters in arguments and exception`() {
        val tree = TestTree()
        val error = RuntimeException("args")
        Lumber.plant(tree)

        Lumber.info(error, "a\nb\tc%s", "a\nb\tc")

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "LumberInfoTest:info-with-special-characters-in-arguments-and-exception",
                    message = "a\nb\tca\nb\tc\n\n${error.stackTraceToString()}",
                    error = error
                )
            )
        )
    }

    @Test
    fun `info with explicit tag message and arguments plus exception`() {
        val tree = TestTree()
        val error = IllegalArgumentException("bad-arg")
        Lumber.plant(tree)

        Lumber.tag("MyTag").info(error, "this is a %s text with %d arguments", "formatted", 2)

        assertEquals(listOf(tree), Lumber.forest())
        tree.assertAll(
            listOf(
                TestTree.Data(
                    level = Lumber.Level.Info,
                    tag = "MyTag",
                    message = "this is a formatted text with 2 arguments\n\n${error.stackTraceToString()}",
                    error = error
                )
            )
        )
    }
}
