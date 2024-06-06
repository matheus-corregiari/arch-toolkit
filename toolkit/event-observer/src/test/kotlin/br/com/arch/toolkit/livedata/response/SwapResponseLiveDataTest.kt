package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.exception.DataResultTransformationException
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.livedata.SwapResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.util.dataResultLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions

@OptIn(ExperimentalCoroutinesApi::class)
class SwapResponseLiveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `00 - init without param, should init with null value`() = runTest {
        val liveData = SwapResponseLiveData<Any>()
        liveData.scope(this)
        liveData.transformDispatcher(Dispatchers.Main.immediate)

        Assert.assertNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertNull(liveData.status)
    }

    @Test
    fun `00 - init with param, should init with param value`() = runTest {
        val value = DataResult("String", null, DataResultStatus.SUCCESS)
        val liveData = SwapResponseLiveData(value)

        Assert.assertEquals(value, liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals("String", liveData.data)
        Assert.assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }

    @Test
    fun `01 - swapResponse`() = runTest {
        val mockedObserver: (String) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main.immediate)
        val swapLiveData = SwapResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main.immediate)

        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }
        verifyNoInteractions(mockedObserver)
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.swapSource(liveData)
        Assert.assertTrue(swapLiveData.hasDataSource)
        verifyNoInteractions(mockedObserver)

        liveData.setData("data")
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke("data") }
    }

    @Test
    fun `02 - swapResponse, with discard after loading`() = runTest {
        val mockedObserver: (String) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main.immediate)
        val swapLiveData = SwapResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main.immediate)

        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }
        verifyNoInteractions(mockedObserver)
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.swapSource(liveData, true)
        Assert.assertTrue(swapLiveData.hasDataSource)
        verifyNoInteractions(mockedObserver)

        liveData.setLoading()
        advanceUntilIdle()
        Assert.assertTrue(swapLiveData.hasDataSource)
        verifyNoInteractions(mockedObserver)
        Assert.assertEquals(dataResultLoading<String>(), swapLiveData.value)

        liveData.setData("data")
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke("data") }
        Assert.assertTrue(swapLiveData.hasDataSource)
        Assert.assertNull(swapLiveData.value)

        liveData.value = null
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke("data") }
        Assert.assertTrue(swapLiveData.hasDataSource)
        Assert.assertNull(swapLiveData.value)
    }

    @Test
    fun `03 - swapResponse, with transformation`() = runTest {
        val mockedTransformation: (String) -> Int = mock()
        val mockedObserver: (Int) -> Unit = mock()
        given(mockedTransformation.invoke("data")).willReturn(0)

        val liveData = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<Int>()
            .transformDispatcher(Dispatchers.Main)

        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }

        Assert.assertFalse(swapLiveData.hasDataSource)
        swapLiveData.swapSource(liveData, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        liveData.setData("data")

        advanceUntilIdle()
        verifyBlocking(mockedTransformation) { invoke("data") }
        verifyBlocking(mockedObserver) { invoke(0) }

        liveData.value = DataResult(null, null, DataResultStatus.SUCCESS)
        advanceUntilIdle()

        Assert.assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        Assert.assertNull(swapLiveData.data)

        liveData.setLoading()
        advanceUntilIdle()

        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        Assert.assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }

    @Test
    fun `04 - swapResponse, with transformation with error`() = runTest {
        val result = IllegalStateException("error")

        val mockedTransformation: (String) -> Int = mock()
        val mockedObserver: (Int) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()
        given(mockedTransformation.invoke("data")).willThrow(result)

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<Int>().transformDispatcher(Dispatchers.Main)

        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observe(alwaysOnOwner) {
            data(observer = mockedObserver)
            error(observer = mockedErrorObserver)
        }

        swapLiveData.swapSource(liveData, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        liveData.setData("data")
        advanceUntilIdle()
        Mockito.verifyNoInteractions(mockedObserver)
        verifyBlocking(mockedErrorObserver) {
            invoke(
                DataResultTransformationException(
                    message = "Error performing swapSource, please check your transformations",
                    error = result
                )
            )
        }

        liveData.setSuccess()
        advanceUntilIdle()
        Assert.assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        Assert.assertNull(swapLiveData.data)

        liveData.setLoading()
        advanceUntilIdle()
        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        Assert.assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }

    @Test
    fun `05 - swapResponse, with result transformation`() = runTest {
        val data = DataResult("data", null, DataResultStatus.SUCCESS)
        val result = DataResult(0, null, DataResultStatus.SUCCESS)

        val mockedTransformation: (DataResult<String>) -> DataResult<Int> = mock()
        val mockedDataObserver: (Int) -> Unit = mock()
        given(mockedTransformation.invoke(data)).willReturn(result)

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<Int>().transformDispatcher(Dispatchers.Main)

        Assert.assertFalse(swapLiveData.hasDataSource)
        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedDataObserver) }
        swapLiveData.swapSource(liveData, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        liveData.setData("data")
        advanceUntilIdle()

        verifyBlocking(mockedTransformation, times(1)) { invoke(data) }
        verifyBlocking(mockedDataObserver, times(1)) { invoke(0) }
    }

    @Test
    fun `06 - needsRefresh`() = runTest {
        val mockedDataObserver: (String) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<String>().transformDispatcher(Dispatchers.Main)

        Assert.assertTrue(swapLiveData.needsRefresh())
        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedDataObserver) }
        swapLiveData.swapSource(liveData)
        Assert.assertFalse(swapLiveData.needsRefresh())
        Assert.assertTrue(swapLiveData.hasDataSource)

        liveData.setLoading()
        advanceUntilIdle()
        Assert.assertFalse(swapLiveData.needsRefresh())

        liveData.setError(IllegalStateException("error"))
        advanceUntilIdle()
        Assert.assertTrue(swapLiveData.needsRefresh())

        liveData.setData("data")
        advanceUntilIdle()
        Assert.assertFalse(swapLiveData.needsRefresh())
        verifyBlocking(mockedDataObserver, times(1)) { invoke("data") }
    }
}
