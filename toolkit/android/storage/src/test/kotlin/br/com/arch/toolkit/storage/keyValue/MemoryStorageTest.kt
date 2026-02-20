package br.com.arch.toolkit.storage.keyValue

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MemoryStorageTest {

    private val subject = MemoryStorage("test")

    @Test
    fun `set should persist values for valid key`() {
        subject["token"] = "abc"

        assertEquals("abc", subject.get("token"))
        assertEquals(1, subject.size())
        assertTrue(subject.contains("token"))
    }

    @Test
    fun `set should remove key when value is blank`() {
        subject["token"] = "abc"
        subject["token"] = "   "

        assertNull(subject.get<String>("token"))
        assertFalse(subject.contains("token"))
    }

    @Test
    fun `remove regex should delete all matching keys`() {
        subject["user_name"] = "A"
        subject["user_email"] = "a@a.com"
        subject["token"] = "123"

        subject.remove(Regex("user_.*"))

        assertEquals(listOf("token"), subject.keys())
    }
}
