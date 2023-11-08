@file:Suppress("ClassNaming", "ClassName")

package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class _liveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `01 - observeNotNull`() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeNotNull(alwaysOnOwner, mockedObserver)

        liveData.value = null
        Mockito.verifyNoInteractions(mockedObserver)

        liveData.value = "nonNullData"
        verifyBlocking(mockedObserver) { invoke("nonNullData") }
    }

    @Test
    fun `02 - observeNull`() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeNull(alwaysOnOwner, mockedObserver)

        liveData.value = null
        verifyBlocking(mockedObserver) { invoke() }

        liveData.value = "nonNullData"
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun `03 - observeSingle`() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeSingle(alwaysOnOwner, mockedObserver)

        liveData.value = null
        Mockito.verifyNoInteractions(mockedObserver)
        Assert.assertTrue(liveData.hasObservers())

        liveData.value = "nonNullData"
        verifyBlocking(mockedObserver) { invoke("nonNullData") }
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun `04 - observeUntil`() {
        val mockedObserver: (Any?) -> Boolean = mock()
        whenever(mockedObserver.invoke(null)) doReturn false
        whenever(mockedObserver.invoke("FALSE")) doReturn false
        whenever(mockedObserver.invoke("TRUE")) doReturn true

        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeUntil(alwaysOnOwner, mockedObserver)

        liveData.value = null
        Assert.assertTrue(liveData.hasObservers())
        verifyBlocking(mockedObserver) { invoke(null) }

        liveData.value = "FALSE"
        Assert.assertTrue(liveData.hasObservers())
        verifyBlocking(mockedObserver) { invoke("FALSE") }

        liveData.value = "TRUE"
        verifyBlocking(mockedObserver) { invoke("TRUE") }
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun `05 - responseLiveDataOf - value`() = runTest {
        val liveData = responseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun `06 - responseLiveDataOf - error`() = runTest {
        val liveData = responseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun `07 - mutableResponseLiveDataOf - value`() = runTest {
        val liveData = mutableResponseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun `08 - mutableResponseLiveDataOf - error`() = runTest {
        val liveData = mutableResponseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun `09 - swapResponseLiveDataOf - value`() = runTest {
        val liveData = swapResponseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun `10 - swapResponseLiveDataOf - error`() = runTest {
        val liveData = swapResponseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun `11 - LiveData mapList`() {
        val mockedTransformer: (String) -> Int = mock()
        val mockedObserver: (List<Int>?) -> Unit = mock()

        whenever(mockedTransformer.invoke("String")) doReturn 123

        val liveData = MutableLiveData<List<String>>()
        val transformedLiveData = liveData.mapList(mockedTransformer)

        transformedLiveData.observe(alwaysOnOwner, mockedObserver)
        verifyNoInteractions(mockedTransformer)
        verifyNoInteractions(mockedObserver)

        Assert.assertNull(liveData.value)
        Assert.assertNull(transformedLiveData.value)

        liveData.value = null
        verifyNoInteractions(mockedTransformer)
        verifyBlocking(mockedObserver) { invoke(null) }

        liveData.value = listOf("String")
        verifyBlocking(mockedTransformer) { invoke("String") }
        verifyBlocking(mockedObserver) { invoke(listOf(123)) }
    }

    @Test
    fun `12 - ResponseLiveData mapList`() = runTest {
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
