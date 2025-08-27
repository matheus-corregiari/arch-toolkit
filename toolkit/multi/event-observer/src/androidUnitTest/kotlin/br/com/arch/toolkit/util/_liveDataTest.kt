@file:Suppress("ClassNaming", "ClassName")

package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.alwaysOnOwner
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class _liveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `01 - observeNotNull`() {
        val mockedObserver: (Any) -> Unit = mockk(relaxed = true)
        val liveData = MutableLiveData<Any>()
        assertNull(liveData.value)

        liveData.observeNotNull(alwaysOnOwner, mockedObserver)

        liveData.value = null
        verify(exactly = 0) { mockedObserver.invoke(any()) }

        liveData.value = "nonNullData"
        verify(exactly = 1) { mockedObserver.invoke("nonNullData") }
    }

    @Test
    fun `02 - observeNull`() {
        val mockedObserver: () -> Unit = mockk(relaxed = true)
        val liveData = MutableLiveData<Any>()
        assertNull(liveData.value)

        liveData.observeNull(alwaysOnOwner, mockedObserver)

        liveData.value = null
        verify(exactly = 1) { mockedObserver.invoke() }

        liveData.value = "nonNullData"
        verify(exactly = 1) { mockedObserver.invoke() }
    }

    @Test
    fun `03 - observeSingle`() {
        val mockedObserver: (Any) -> Unit = mockk(relaxed = true)
        val liveData = MutableLiveData<Any>()
        assertNull(liveData.value)

        liveData.observeSingle(alwaysOnOwner, mockedObserver)

        liveData.value = null
        verify(exactly = 0) { mockedObserver.invoke(any()) }
        assertTrue(liveData.hasObservers())

        liveData.value = "nonNullData"
        verify(exactly = 1) { mockedObserver.invoke("nonNullData") }
        assertFalse(liveData.hasObservers())
    }

    @Test
    fun `04 - observeUntil`() {
        val mockedObserver: (Any?) -> Boolean = mockk()
        every { mockedObserver.invoke(null) } returns false
        every { mockedObserver.invoke("FALSE") } returns false
        every { mockedObserver.invoke("TRUE") } returns true

        val liveData = MutableLiveData<Any>()
        assertNull(liveData.value)

        liveData.observeUntil(alwaysOnOwner, mockedObserver)

        liveData.value = null
        assertTrue(liveData.hasObservers())
        verify(exactly = 1) { mockedObserver.invoke(null) }

        liveData.value = "FALSE"
        assertTrue(liveData.hasObservers())
        verify(exactly = 1) { mockedObserver.invoke("FALSE") }

        liveData.value = "TRUE"
        verify(exactly = 1) { mockedObserver.invoke("TRUE") }
        assertFalse(liveData.hasObservers())
    }

    @Test
    fun `05 - LiveData mapList`() {
        val mockedTransformer: (String) -> Int = mockk()
        val mockedObserver: (List<Int>?) -> Unit = mockk(relaxed = true)

        every { mockedTransformer.invoke("String") } returns 123

        val liveData = MutableLiveData<List<String>>()
        val transformedLiveData = liveData.mapList(mockedTransformer)

        transformedLiveData.observe(alwaysOnOwner, mockedObserver)
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedObserver.invoke(any()) }

        assertNull(liveData.value)
        assertNull(transformedLiveData.value)

        liveData.value = null
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 1) { mockedObserver.invoke(null) }

        liveData.value = listOf("String")
        verify(exactly = 1) { mockedTransformer.invoke("String") }
        verify(exactly = 1) { mockedObserver.invoke(listOf(123)) }
    }

    @Test
    fun `06 - LiveData mapNotNull`() {
        val mockedTransformer: (String?) -> Int? = mockk()
        val mockedObserver: (Int) -> Unit = mockk(relaxed = true)

        every { mockedTransformer.invoke(null) } returns 321
        every { mockedTransformer.invoke("String") } returns 123
        every { mockedTransformer.invoke("null") } returns null

        val liveData = MutableLiveData<String>()
        val transformedLiveData = liveData.mapNotNull(mockedTransformer)

        transformedLiveData.observe(alwaysOnOwner, mockedObserver)
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedObserver.invoke(any()) }

        assertNull(liveData.value)
        assertNull(transformedLiveData.value)

        liveData.value = null
        verify(exactly = 1) { mockedTransformer.invoke(null) }
        verify(exactly = 1) { mockedObserver.invoke(321) }

        liveData.value = "null"
        verify(exactly = 1) { mockedTransformer.invoke("null") }
        verify(exactly = 1) { mockedObserver.invoke(321) }

        liveData.value = "String"
        verify(exactly = 1) { mockedTransformer.invoke("String") }
        verify(exactly = 1) { mockedObserver.invoke(123) }
    }
}
