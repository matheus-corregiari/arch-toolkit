package br.com.arch.toolkit.storage.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds

class ThresholdDataTest {

    @Test
    fun `get returns value when keys match and data is fresh`() {
        val subject = ThresholdData<String>(50.milliseconds)
        subject.set(storageName = "repo", name = "page-1", data = "content")

        assertEquals("content", subject.get(storageName = "repo", name = "page-1"))
    }

    @Test
    fun `get clears value when storage key changes`() {
        val subject = ThresholdData<String>(50.milliseconds)
        subject.set(storageName = "repo", name = "page-1", data = "content")

        assertNull(subject.get(storageName = "another", name = "page-1"))
        assertNull(subject.get(storageName = "repo", name = "page-1"))
    }

    @Test
    fun `get returns null when entry has expired`() {
        val subject = ThresholdData<String>(1.milliseconds)
        subject.set(storageName = "repo", name = "page-1", data = "content")
        Thread.sleep(5)

        assertNull(subject.get(storageName = "repo", name = "page-1"))
    }
}
