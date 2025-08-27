@file:Suppress("ClassNaming", "ClassName")

package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.result.DataResultStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class _responseLiveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `01 - responseLiveDataOf - value`() = runTest {
        val liveData = responseLiveDataOf("value")

        assertTrue(liveData.data == "value")
        assertEquals(liveData.status, DataResultStatus.SUCCESS)
        assertNull(liveData.error)
    }

    @Test
    fun `02 - responseLiveDataOf - error`() = runTest {
        val liveData = responseLiveDataOf<Any>(Throwable())

        assertNull(liveData.data)
        assertEquals(liveData.status, DataResultStatus.ERROR)
        assertNotNull(liveData.error)
    }

    @Test
    fun `03 - mutableResponseLiveDataOf - value`() = runTest {
        val liveData = mutableResponseLiveDataOf("value")

        assertTrue(liveData.data == "value")
        assertEquals(liveData.status, DataResultStatus.SUCCESS)
        assertNull(liveData.error)
    }

    @Test
    fun `04 - mutableResponseLiveDataOf - error`() = runTest {
        val liveData = mutableResponseLiveDataOf<Any>(Throwable())

        assertNull(liveData.data)
        assertEquals(liveData.status, DataResultStatus.ERROR)
        assertNotNull(liveData.error)
    }

    @Test
    fun `05 - swapResponseLiveDataOf - value`() = runTest {
        val liveData = swapResponseLiveDataOf("value")

        assertTrue(liveData.data == "value")
        assertEquals(liveData.status, DataResultStatus.SUCCESS)
        assertNull(liveData.error)
    }

    @Test
    fun `06 - swapResponseLiveDataOf - error`() = runTest {
        val liveData = swapResponseLiveDataOf<Any>(Throwable())

        assertNull(liveData.data)
        assertEquals(liveData.status, DataResultStatus.ERROR)
        assertNotNull(liveData.error)
    }

    @Test
    fun `07 - ResponseLiveData mapList`() = runTest {
        val mockMap: (Int) -> String = mockk()
        every { mockMap.invoke(123) } returns "String"

        val liveData = MutableResponseLiveData<List<Int>>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.mapList(mockMap)

        assertNull(liveData.value)
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockMap.invoke(123) }

        // Will change the value only after the observe
        liveData.setValue(dataResultSuccess(listOf(123)))
        advanceUntilIdle()
        assertEquals(
            dataResultSuccess(listOf(123)),
            liveData.value
        )
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockMap.invoke(123) }

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        assertEquals(
            dataResultSuccess(listOf("String")),
            onErrorLiveData.value
        )
        verify(exactly = 1) { mockMap.invoke(123) }
    }
}
