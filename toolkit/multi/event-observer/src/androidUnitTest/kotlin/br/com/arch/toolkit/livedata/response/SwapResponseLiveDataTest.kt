package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.exception.DataResultTransformationException
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.livedata.SwapResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.util.dataResultLoading
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SwapResponseLiveDataTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `00 - init without param, should init with null value`() = runTest {
        val liveData = SwapResponseLiveData<Any>()
        liveData.scope(this)
        liveData.transformDispatcher(Dispatchers.Main.immediate)

        assertNull(liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertNull(liveData.status)
    }

    @Test
    fun `00 - init with param, should init with param value`() = runTest {
        val value = DataResult("String", null, DataResultStatus.SUCCESS)
        val liveData = SwapResponseLiveData(value)

        assertEquals(value, liveData.value)
        assertNull(liveData.error)
        assertEquals("String", liveData.data)
        assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }

    @Test
    fun `01 - swapResponse`() = runTest {
        val mockedObserver: (String) -> Unit = mockk(relaxed = true)

        val liveData = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main.immediate)
        val swapLiveData = SwapResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main.immediate)

        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }
        verify(exactly = 0) { mockedObserver.invoke(any()) }
        assertFalse(swapLiveData.hasDataSource)

        swapLiveData.swapSource(liveData)
        assertTrue(swapLiveData.hasDataSource)
        verify(exactly = 0) { mockedObserver.invoke(any()) }

        liveData.setData("data")
        advanceUntilIdle()
        verify(exactly = 1) { mockedObserver.invoke("data") }
    }

    @Test
    fun `02 - swapResponse, with discard after loading`() = runTest {
        val mockedObserver: (String) -> Unit = mockk(relaxed = true)

        val liveData = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main.immediate)
        val swapLiveData = SwapResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main.immediate)

        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }
        verify(exactly = 0) { mockedObserver.invoke(any()) }
        assertFalse(swapLiveData.hasDataSource)

        swapLiveData.swapSource(liveData, true)
        assertTrue(swapLiveData.hasDataSource)
        verify(exactly = 0) { mockedObserver.invoke(any()) }

        liveData.setLoading()
        advanceUntilIdle()
        assertTrue(swapLiveData.hasDataSource)
        verify(exactly = 0) { mockedObserver.invoke(any()) }
        assertEquals(dataResultLoading<String>(), swapLiveData.value)

        liveData.setData("data")
        advanceUntilIdle()
        verify(exactly = 1) { mockedObserver.invoke("data") }
        assertTrue(swapLiveData.hasDataSource)
        assertNull(swapLiveData.value)

        liveData.value = null
        advanceUntilIdle()
        verify(exactly = 1) { mockedObserver.invoke("data") }
        assertTrue(swapLiveData.hasDataSource)
        assertNull(swapLiveData.value)
    }

    @Test
    fun `03 - swapResponse, with transformation`() = runTest {
        val mockedTransformation: (String) -> Int = mockk()
        val mockedObserver: (Int) -> Unit = mockk(relaxed = true)
        every { mockedTransformation.invoke("data") } returns 0

        val liveData = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<Int>()
            .transformDispatcher(Dispatchers.Main)

        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }

        assertFalse(swapLiveData.hasDataSource)
        swapLiveData.swapSource(liveData, mockedTransformation)
        assertTrue(swapLiveData.hasDataSource)

        liveData.setData("data")

        advanceUntilIdle()
        verify(exactly = 1) { mockedTransformation.invoke("data") }
        verify(exactly = 1) { mockedObserver.invoke(0) }

        liveData.value = DataResult(null, null, DataResultStatus.SUCCESS)
        advanceUntilIdle()

        assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        assertNull(swapLiveData.data)

        liveData.setLoading()
        advanceUntilIdle()

        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }

    @Test
    fun `04 - swapResponse, with transformation with error`() = runTest {
        val result = IllegalStateException("error")

        val mockedTransformation: (String) -> Int = mockk()
        val mockedObserver: (Int) -> Unit = mockk(relaxed = true)
        val mockedErrorObserver: (Throwable) -> Unit = mockk(relaxed = true)
        every { mockedTransformation.invoke("data") } throws result

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<Int>().transformDispatcher(Dispatchers.Main)

        assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observe(alwaysOnOwner) {
            data(observer = mockedObserver)
            error(observer = mockedErrorObserver)
        }

        swapLiveData.swapSource(liveData, mockedTransformation)
        assertTrue(swapLiveData.hasDataSource)

        liveData.setData("data")
        advanceUntilIdle()
        verify(exactly = 0) { mockedObserver.invoke(any()) }
        verify(exactly = 1) {
            mockedErrorObserver.invoke(
                DataResultTransformationException(
                    message = "Error performing swapSource, please check your transformations",
                    error = result
                )
            )
        }

        liveData.setSuccess()
        advanceUntilIdle()
        assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        assertNull(swapLiveData.data)

        liveData.setLoading()
        advanceUntilIdle()
        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }

    @Test
    fun `05 - swapResponse, with result transformation`() = runTest {
        val data = DataResult("data", null, DataResultStatus.SUCCESS)
        val result = DataResult(0, null, DataResultStatus.SUCCESS)

        val mockedTransformation: (DataResult<String>) -> DataResult<Int> = mockk()
        val mockedDataObserver: (Int) -> Unit = mockk(relaxed = true)
        every { mockedTransformation.invoke(data) } returns result

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<Int>().transformDispatcher(Dispatchers.Main)

        assertFalse(swapLiveData.hasDataSource)
        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedDataObserver) }
        swapLiveData.swapSource(liveData, mockedTransformation)
        assertTrue(swapLiveData.hasDataSource)

        liveData.setData("data")
        advanceUntilIdle()

        verify(exactly = 1) { mockedTransformation.invoke(data) }
        verify(exactly = 1) { mockedDataObserver.invoke(0) }
    }

    @Test
    fun `06 - needsRefresh`() = runTest {
        val mockedDataObserver: (String) -> Unit = mockk(relaxed = true)

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<String>().transformDispatcher(Dispatchers.Main)

        assertTrue(swapLiveData.needsRefresh())
        swapLiveData.observe(alwaysOnOwner) { data(observer = mockedDataObserver) }
        swapLiveData.swapSource(liveData)
        assertFalse(swapLiveData.needsRefresh())
        assertTrue(swapLiveData.hasDataSource)

        liveData.setLoading()
        advanceUntilIdle()
        assertFalse(swapLiveData.needsRefresh())

        liveData.setError(IllegalStateException("error"))
        advanceUntilIdle()
        assertTrue(swapLiveData.needsRefresh())

        liveData.setData("data")
        advanceUntilIdle()
        assertFalse(swapLiveData.needsRefresh())
        verify(exactly = 1) { mockedDataObserver.invoke("data") }
    }
}
