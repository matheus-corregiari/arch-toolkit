@file:Suppress("ClassNaming", "ClassName")

package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.testSetValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class _responseLiveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `01 - responseLiveDataOf - value`() = runTest {
        val liveData = responseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun `02 - responseLiveDataOf - error`() = runTest {
        val liveData = responseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun `03 - mutableResponseLiveDataOf - value`() = runTest {
        val liveData = mutableResponseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun `04 - mutableResponseLiveDataOf - error`() = runTest {
        val liveData = mutableResponseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun `05 - swapResponseLiveDataOf - value`() = runTest {
        val liveData = swapResponseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun `06 - swapResponseLiveDataOf - error`() = runTest {
        val liveData = swapResponseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun `07 - ResponseLiveData mapList`() = runTest {
        val mockMap: (Int) -> String = mock()
        whenever(mockMap.invoke(123)) doReturn "String"
        val liveData = ResponseLiveData<List<Int>>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.mapList(mockMap)

        Assert.assertNull(liveData.value)
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockMap)

        // Will change the value only after the observe
        liveData.testSetValue(dataResultSuccess(listOf(123)))
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultSuccess(listOf(123)),
            liveData.value
        )
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockMap)

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultSuccess(listOf("String")),
            onErrorLiveData.value
        )
        verifyBlocking(mockMap) { invoke(123) }
    }
}
