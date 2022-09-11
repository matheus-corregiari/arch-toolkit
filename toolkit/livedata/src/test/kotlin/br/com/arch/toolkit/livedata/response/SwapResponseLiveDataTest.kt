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
import net.vidageek.mirror.dsl.Mirror
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SwapResponseLiveDataTest {

    @Rule
    @get:Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var owner = object : LifecycleOwner {
        private val registry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle {
            registry.currentState = Lifecycle.State.RESUMED
            return registry
        }
    }

    @Test
    fun whenSwapSource_withoutTransformation_shouldReplicateData() {
        val mockedObserver: (String) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val swapLiveData = SwapResponseLiveData<String>()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.swapSource(liveData)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = "data"
        liveData.postData(data)
        swapLiveData.observeData(owner, mockedObserver)
        Mockito.verify(mockedObserver).invoke(data)
    }

    @Test
    fun whenSwapSource_withoutTransformation_discardAfterLoading_shouldReplicateData_nullDataBefore() {
        val mockedObserver: (String) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val swapLiveData = SwapResponseLiveData<String>()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.swapSource(liveData, true)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = "data"
        liveData.postData(data)
        swapLiveData.observeSingleData(owner, mockedObserver)
        Mockito.verify(mockedObserver).invoke(data)

        Assert.assertNull(swapLiveData.value)

        LiveData::class.java.declaredMethods.find { it.name == "postValue" }?.let {
            it.isAccessible = true
            it.invoke(liveData, null)
        }
        swapLiveData.observeSingleData(owner, mockedObserver)
        Mockito.verify(mockedObserver).invoke(data)

        Assert.assertNull(swapLiveData.value)
    }

    @Test
    fun whenSwapSource_withSynchronousTransformation_shouldReplicateTransformedDataWithoutStartingThreads() {
        val threadCount = Thread.activeCount()

        val mockedTransformation: (String) -> Int = mock()
        val mockedObserver: (Int) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val swapLiveData = SwapResponseLiveData<Int>()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observeData(owner, mockedObserver)
        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)

        swapLiveData.swapSource(liveData, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = "data"
        val result = 0
        given(mockedTransformation.invoke(data)).willReturn(result)

        liveData.postData(data)

        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)
        Mockito.verify(mockedObserver).invoke(result)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Thread.sleep(50)

        Assert.assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        Assert.assertNull(swapLiveData.data)

        liveData.postLoading()
        liveData.postError(IllegalStateException())

        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)
        Assert.assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }

    @Test
    fun whenSwapSource_withAsynchronousTransformation_shouldReplicateTransformedDataStartingThreads() {
        val threadCount = Thread.activeCount()

        val mockedTransformation: (String) -> Int = mock()
        val mockedObserver: (Int) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val swapLiveData = SwapResponseLiveData<Int>()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observeData(owner, mockedObserver)
        Thread.sleep(50)

        swapLiveData.swapSource(liveData, true, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = "data"
        val result = 0
        given(mockedTransformation.invoke(data)).willReturn(result)

        liveData.postData(data)

        Assert.assertNotEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)
        Mockito.verify(mockedObserver).invoke(result)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Thread.sleep(50)

        Assert.assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        Assert.assertNull(swapLiveData.data)

        liveData.postLoading()
        liveData.postError(IllegalStateException())

        Assert.assertNotEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)
        Assert.assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }

    @Test
    fun whenSwapSource_withAsynchronousTransformation_onError_shouldDeliverErrorResult() {
        val threadCount = Thread.activeCount()

        val mockedTransformation: (String) -> Int = mock()
        val mockedObserver: (Int) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val swapLiveData = SwapResponseLiveData<Int>()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observeData(owner, mockedObserver)
        swapLiveData.observeError(owner, mockedErrorObserver)
        Thread.sleep(50)

        swapLiveData.swapSource(liveData, true, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = "data"
        val result = IllegalStateException("error")
        given(mockedTransformation.invoke(data)).willThrow(result)

        liveData.postData(data)

        Assert.assertNotEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)
        Mockito.verifyNoInteractions(mockedObserver)
        Mockito.verify(mockedErrorObserver).invoke(DataTransformationException("Error performing swapSource, please check your transformations", result))

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Thread.sleep(50)

        Assert.assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        Assert.assertNull(swapLiveData.data)

        liveData.postLoading()
        liveData.postError(IllegalStateException())

        Assert.assertNotEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)
        Assert.assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }

    @Test
    fun whenSwapSource_withSynchronousTransformation_onError_shouldDeliverErrorResult() {
        val threadCount = Thread.activeCount()

        val mockedTransformation: (String) -> Int = mock()
        val mockedObserver: (Int) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val swapLiveData = SwapResponseLiveData<Int>()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observeData(owner, mockedObserver)
        swapLiveData.observeError(owner, mockedErrorObserver)
        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)

        swapLiveData.swapSource(liveData, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = "data"
        val result = IllegalStateException("error")
        given(mockedTransformation.invoke(data)).willThrow(result)

        liveData.postData(data)

        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)
        Mockito.verifyNoInteractions(mockedObserver)
        Mockito.verify(mockedErrorObserver).invoke(DataTransformationException("Error performing swapSource, please check your transformations", result))

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Thread.sleep(50)

        Assert.assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        Assert.assertNull(swapLiveData.data)

        liveData.postLoading()
        liveData.postError(IllegalStateException())

        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)
        Assert.assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }

    @Test
    fun whenSwapSource_withSynchronousTransformation_shouldDeliverTransformedResult() {
        val threadCount = Thread.activeCount()

        val mockedTransformation: (DataResult<String>) -> DataResult<Int> = mock()
        val mockedDataObserver: (Int) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val swapLiveData = SwapResponseLiveData<Int>()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observeData(owner, mockedDataObserver)
        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)

        swapLiveData.swapSource(liveData, false, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = DataResult("data", null, DataResultStatus.SUCCESS)
        val result = DataResult(0, null, DataResultStatus.SUCCESS)
        given(mockedTransformation.invoke(data)).willReturn(result)

        liveData.postData("data")

        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)

        Mockito.verify(mockedTransformation, times(1)).invoke(data)
        Mockito.verify(mockedDataObserver, times(1)).invoke(0)
    }

    @Test
    fun whenSwapSource_withAsynchronousTransformation_shouldDeliverTransformedResult() {
        val threadCount = Thread.activeCount()

        val mockedTransformation: (DataResult<String>) -> DataResult<Int> = mock()
        val mockedDataObserver: (Int) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val swapLiveData = SwapResponseLiveData<Int>()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observeData(owner, mockedDataObserver)
        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)

        swapLiveData.swapSource(liveData, true, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = DataResult("data", null, DataResultStatus.SUCCESS)
        val result = DataResult(0, null, DataResultStatus.SUCCESS)
        given(mockedTransformation.invoke(data)).willReturn(result)

        liveData.postData("data")

        Assert.assertNotEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)

        Mockito.verify(mockedTransformation, times(1)).invoke(data)
        Mockito.verify(mockedDataObserver, times(1)).invoke(0)
    }

    @Test
    fun whenInitialize_withoutValue_shouldReturnAnInstanceWithEmptyValue() {
        val liveData = SwapResponseLiveData<Any>()
        Assert.assertNull(liveData.data)
        Assert.assertNull(liveData.status)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun whenInitialize_withValue_shouldReturnAnInstanceWithADefaultValue() {
        val liveData = SwapResponseLiveData(
            DataResult("value", null, DataResultStatus.SUCCESS)
        )
        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }
}