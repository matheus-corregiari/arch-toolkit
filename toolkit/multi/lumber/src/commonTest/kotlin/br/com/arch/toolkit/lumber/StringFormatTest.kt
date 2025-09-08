package br.com.arch.toolkit.lumber

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class StringFormatTest {
    @Test
    fun `zero format - zero args - should return same string`() {
        val formatted = "normal text with no arguments".format()
        val expected = "normal text with no arguments"
        assertEquals(expected, formatted)
    }

    @Test
    fun `one format - one arg - should return formatted string`() {
        val formatted = "normal text with %s argument".format("one")
        val expected = "normal text with one argument"
        assertEquals(expected, formatted)
    }

    @Test
    fun `one format - zero args - should return same string`() {
        val formatted = "normal text with %s argument".format()
        val expected = "normal text with %s argument"
        assertEquals(expected, formatted)
    }

    @Test
    fun `two format - two args - should return formatted string`() {
        val formatted = "normal text with %s and %s arguments".format("one", 2)
        val expected = "normal text with one and 2 arguments"
        assertEquals(expected, formatted)
    }

    @Test
    fun `two format - two null args - should return formatted string`() {
        val formatted = "normal text with %s and %d arguments".format(null, null)
        val expected = "normal text with null and null arguments"
        assertEquals(expected, formatted)
    }

    @Test
    fun `three format - three args - should return formatted string`() {
        val formatted = "normal text with %s, %d and %s arguments".format("one", 2, true)
        val expected = "normal text with one, 2 and true arguments"
        assertEquals(expected, formatted)
    }

    @Test
    fun `more arguments than placeholders - should return formatted string`() {
        val formatted = "normal text with %s, %d and %s arguments".format("one", 2, true, "extra")
        val expected = "normal text with one, 2 and true arguments"
        assertEquals(expected, formatted)
    }

    @Test
    fun `less arguments than placeholders - should return throw exception`() {
        val error = assertFails {
            "normal text with %s, %d and %s arguments".format("one", 2)
        }
        val expected = "Wrong number of arguments, expected 3, actual 2"
        assertEquals(expected, error.message)
    }

    @Test
    fun `unknown placeholder - should keep placeholder`() {
        val formatted = "value is %f".format(3.14)
        val expected = "value is %f"
        assertEquals(expected, formatted)
    }

    @Test
    fun `percent d with non number - should return null string`() {
        val formatted = "value is %d".format("notANumber")
        val expected = "value is null"
        assertEquals(expected, formatted)
    }

    @Test
    fun `mixed placeholders - should respect order`() {
        val formatted = "%s%d%s".format("a", 1, "b")
        val expected = "a1b"
        assertEquals(expected, formatted)
    }

    @Test
    fun `only placeholder - with null arg`() {
        val formatted = "%s".format(null)
        val expected = "null"
        assertEquals(expected, formatted)
    }

    @Test
    fun `empty string with args - should return empty`() {
        val formatted = "".format("ignored")
        assertEquals("", formatted)
    }

    @Test
    fun `much more args than placeholders - should ignore extras`() {
        val formatted = "hello %s".format("world", 1, 2, 3)
        val expected = "hello world"
        assertEquals(expected, formatted)
    }

    @Test
    fun `percent sign alone - should remain unchanged`() {
        val formatted = "100% sure".format("ignored")
        val expected = "100% sure"
        assertEquals(expected, formatted)
    }
}
