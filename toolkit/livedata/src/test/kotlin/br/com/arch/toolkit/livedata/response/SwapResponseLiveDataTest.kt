package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
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
            registry.markState(Lifecycle.State.RESUMED)
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
    fun whenSwapSource_withTransformation_shouldReplicateTransformedData() {
        val mockedTransformation: (String) -> Int = mock()
        val mockedObserver: (Int) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val swapLiveData = SwapResponseLiveData<Int>()
        Assert.assertFalse(swapLiveData.hasDataSource)

        swapLiveData.observeData(owner, mockedObserver)
        Thread.sleep(10)

        swapLiveData.swapSource(liveData, mockedTransformation)
        Assert.assertTrue(swapLiveData.hasDataSource)

        val data = "data"
        val result = 0
        given(mockedTransformation.invoke(data)).willReturn(result)

        liveData.postData(data)
        liveData.postData(data)

        Thread.sleep(15)
        Mockito.verify(mockedObserver).invoke(result)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Thread.sleep(15)

        Assert.assertEquals(DataResultStatus.SUCCESS, swapLiveData.status)
        Assert.assertNull(swapLiveData.data)

        liveData.postLoading()
        liveData.postError(IllegalStateException())

        Thread.sleep(15)
        Assert.assertEquals(DataResultStatus.ERROR, swapLiveData.status)
    }
}