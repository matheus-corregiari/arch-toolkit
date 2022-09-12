package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.common.exception.DataTransformationException
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class SwapResponseLiveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var owner = object : LifecycleOwner {
        private val registry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle {
            registry.currentState = Lifecycle.State.RESUMED
            return registry
        }
    }

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun whenSwapSource_withoutTransformation_shouldReplicateData() = runTest {
        val mockedObserver: (String) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)

        advanceUntilIdle()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.swapSource(liveData)
        advanceUntilIdle()
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = "data"
        liveData.setData(data)
        swapLiveData.observeData(owner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(data) }
    }

    @Test
    fun whenSwapSource_withoutTransformation_discardAfterLoading_shouldReplicateData_nullDataBefore() =
        runTest {
            val mockedObserver: (String) -> Unit = mock()

            val liveData = MutableResponseLiveData<String>()
                .transformDispatcher(Dispatchers.Main)
            val swapLiveData = SwapResponseLiveData<String>()
                .transformDispatcher(Dispatchers.Main)
            Assert.assertFalse(swapLiveData.hasDataSource)

            swapLiveData.swapSource(liveData, true)
            Assert.assertTrue(swapLiveData.hasDataSource)

            val data = "data"
            liveData.setData(data)
            swapLiveData.observeSingleData(owner, mockedObserver)
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(data) }

            advanceUntilIdle()
            Assert.assertNull(swapLiveData.value)

            LiveData::class.java.declaredMethods.find { it.name == "postValue" }?.let {
                it.isAccessible = true
                it.invoke(liveData, null)
            }
            swapLiveData.observeSingleData(owner, mockedObserver)
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(data) }

            advanceUntilIdle()
            Assert.assertNull(swapLiveData.value)
        }

    @Test
    fun whenSwapSource_withSynchronousTransformation_shouldReplicateTransformedDataWithoutStartingThreads() =
        runTest {

            val mockedTransformation: (String) -> Int = mock()
            val mockedObserver: (Int) -> Unit = mock()

            val liveData = MutableResponseLiveData<String>()
                .transformDispatcher(Dispatchers.Main)
            val swapLiveData = SwapResponseLiveData<Int>()
                .transformDispatcher(Dispatchers.Main)

            advanceUntilIdle()
            Assert.assertFalse(swapLiveData.hasDataSource)

            swapLiveData.observeData(owner, mockedObserver)
            swapLiveData.swapSource(liveData, mockedTransformation)
            advanceUntilIdle()
            Assert.assertTrue(swapLiveData.hasDataSource)

            val data = "data"
            val result = 0
            given(mockedTransformation.invoke(data)).willReturn(result)

            liveData.setData(data)

            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(result) }

            liveData.value = DataResult(null, null, DataResultStatus.SUCCESS)

            advanceUntilIdle()
            Assert.assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
            advanceUntilIdle()
            Assert.assertNull(swapLiveData.data)

            liveData.setLoading()
            liveData.setError(IllegalStateException())

            advanceUntilIdle()
            Assert.assertEquals(DataResultStatus.ERROR, swapLiveData.status)
        }

    @Test
    fun whenSwapSource_withSynchronousTransformation_onError_shouldDeliverErrorResult() = runTest {

        val mockedTransformation: (String) -> Int = mock()
        val mockedObserver: (Int) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<Int>()
            .transformDispatcher(Dispatchers.Main)
        advanceUntilIdle()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observeData(owner, mockedObserver)
        swapLiveData.observeError(owner, mockedErrorObserver)

        swapLiveData.swapSource(liveData, mockedTransformation)
        advanceUntilIdle()
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = "data"
        val result = IllegalStateException("error")
        given(mockedTransformation.invoke(data)).willThrow(result)

        liveData.setData(data)

        advanceUntilIdle()
        Mockito.verifyNoInteractions(mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedErrorObserver) {
            invoke(
                DataTransformationException(
                    "Error performing swapSource, please check your transformations",
                    result
                )
            )
        }

        liveData.value = DataResult(null, null, DataResultStatus.SUCCESS)
        advanceUntilIdle()
        Assert.assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        advanceUntilIdle()
        Assert.assertNull(swapLiveData.data)

        liveData.setLoading()
        liveData.setError(IllegalStateException())

        advanceUntilIdle()
        Assert.assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }

    @Test
    fun whenSwapSource_withSynchronousTransformation_shouldDeliverTransformedResult() = runTest {

        val mockedTransformation: (DataResult<String>) -> DataResult<Int> = mock()
        val mockedDataObserver: (Int) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val swapLiveData = SwapResponseLiveData<Int>()
            .transformDispatcher(Dispatchers.Main)
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observeData(owner, mockedDataObserver)
        swapLiveData.swapSource(liveData, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = DataResult("data", null, DataResultStatus.SUCCESS)
        val result = DataResult(0, null, DataResultStatus.SUCCESS)
        given(mockedTransformation.invoke(data)).willReturn(result)

        liveData.setData("data")

        advanceUntilIdle()
        verifyBlocking(mockedTransformation, times(1)) { invoke(data) }
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver, times(1)) { invoke(0) }
    }

    @Test
    fun whenInitialize_withoutValue_shouldReturnAnInstanceWithEmptyValue() = runTest {
        val liveData = SwapResponseLiveData<Any>()
        Assert.assertNull(liveData.data)
        Assert.assertNull(liveData.status)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun whenInitialize_withValue_shouldReturnAnInstanceWithADefaultValue() = runTest {
        val liveData = SwapResponseLiveData(
            DataResult("value", null, DataResultStatus.SUCCESS)
        )
        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }
}