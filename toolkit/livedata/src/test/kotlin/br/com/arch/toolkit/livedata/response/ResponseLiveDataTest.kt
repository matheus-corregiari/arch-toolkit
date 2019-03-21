package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import net.vidageek.mirror.dsl.Mirror
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class ResponseLiveDataTest {

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
    fun validateNullValueScenarios() {
        val mockedObserver: (Any) -> Unit = mock()
        val errorObserver: (Throwable) -> Unit = mock()
        val emptyObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleShowLoading(owner, emptyObserver)
        liveData.observeSingleHideLoading(owner, emptyObserver)
        liveData.observeSingleError(owner, emptyObserver)
        liveData.observeSingleError(owner, errorObserver)
        liveData.observeSingleData(owner, mockedObserver)
        liveData.observeSingleSuccess(owner, emptyObserver)

        val nullObject: DataResult<Any>? = null

        LiveData::class.java.declaredMethods.find { it.name == "postValue" }?.let {
            it.isAccessible = true
            it.invoke(liveData, nullObject)
        }

        Mockito.verifyZeroInteractions(mockedObserver)
        Mockito.verifyZeroInteractions(errorObserver)
        Mockito.verifyZeroInteractions(emptyObserver)

        Assert.assertNull(liveData.value)
        Assert.assertNull(liveData.data)
        Assert.assertNull(liveData.status)
        Assert.assertNull(liveData.error)
    }

    // region Loading
    @Test
    fun whenObserveLoading_shouldReceiveTrueWhenStatusIsLoading_andFalseOtherwise() {
        val mockedObserver: (Boolean) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeLoading(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verify(mockedObserver).invoke(true)

        liveData.postError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke(false)

        liveData.postLoading()
        Mockito.verify(mockedObserver, times(2)).invoke(true)
    }

    @Test
    fun whenObserveShowLoading_shouldBeCalledWhenStatusIsLoading() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeShowLoading(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verify(mockedObserver).invoke()

        liveData.postError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.postLoading()
        Mockito.verify(mockedObserver, times(2)).invoke()
    }

    @Test
    fun whenObserveHideLoading_shouldBeCalledWhenStatusIsLoading() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeHideLoading(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.postError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke()

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveSingleShowLoading_shouldBeCalledWhenStatusIsLoading_onlyOnce() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleShowLoading(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verify(mockedObserver).invoke()

        liveData.postError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleHideLoading_shouldBeCalledWhenStatusIsLoading() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleHideLoading(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.postError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke()

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.postError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }
    // endregion

    // region Error
    @Test
    fun whenObserveError_shouldBeCalledWhenStatusIsError() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeError(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.postError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke()

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.postError(IllegalStateException())
        Mockito.verify(mockedObserver, times(2)).invoke()
    }

    @Test
    fun whenObserveError_withExceptionData_shouldBeCalledWhenStatusIsError() {
        val mockedObserver: (Throwable) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeError(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val exception = IllegalStateException()
        liveData.postError(exception)
        Mockito.verify(mockedObserver).invoke(exception)

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.ERROR))
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveSingleError_shouldBeCalledWhenStatusIsError() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleError(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.postError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke()

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.postError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleError_withExceptionData_shouldBeCalledWhenStatusIsError() {
        val mockedObserver: (Throwable) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleError(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val exception = IllegalStateException()
        liveData.postError(exception)
        Mockito.verify(mockedObserver).invoke(exception)

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.postError(exception)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.ERROR))
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }
    // endregion

    // region Success
    @Test
    fun whenObserveData_shouldBeCalledWhenStatusIsSuccess() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeData(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        liveData.postData(data)
        Mockito.verify(mockedObserver).invoke(data)

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveSuccess_shouldBeCalledWhenStatusIsSuccess() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSuccess(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        liveData.postData(data)
        Mockito.verify(mockedObserver).invoke()

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveSingleData_shouldBeCalledWhenStatusIsSuccess() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleData(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        liveData.postData(data)
        Mockito.verify(mockedObserver).invoke(data)

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.postData(data)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleSuccess_shouldBeCalledWhenStatusIsSuccess() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleSuccess(owner, mockedObserver)

        liveData.postLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        liveData.postData(data)
        Mockito.verify(mockedObserver).invoke()

        liveData.postLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.postData(data)
        Mockito.verifyZeroInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }
    // endregion

    @Test
    fun whenMap_shouldTransformData() {
        val mockedTransformer: (String) -> String = mock()
        val mockedObserver: (String) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val mappedLiveData = liveData.map(mockedTransformer)

        val data = "data"
        Mockito.`when`(mockedTransformer.invoke(data)).thenReturn(data)

        liveData.postData(data)
        mappedLiveData.observeData(owner, mockedObserver)

        Thread.sleep(5)

        Mockito.verify(mockedObserver).invoke(data)
        Mockito.verify(mockedTransformer).invoke(data)
    }

    @Test
    fun whenOnNext_shouldDeliverDataBeforeCallObserver() {
        val mockedOnNext: (String) -> Unit = mock()
        val mockedObserver: (String) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>()
        val onNextLiveData = liveData.onNext(mockedOnNext)

        val data = "data"

        liveData.postData(data)
        onNextLiveData.observeData(owner, mockedObserver)

        Thread.sleep(10)

        Mockito.verify(mockedObserver).invoke(data)
        Mockito.verify(mockedOnNext).invoke(data)
    }
}