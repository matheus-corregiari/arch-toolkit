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

        liveData.setLoading()
        Mockito.verify(mockedObserver).invoke(true)

        liveData.setError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke(false)

        liveData.setLoading()
        Mockito.verify(mockedObserver, times(2)).invoke(true)
    }

    @Test
    fun whenObserveShowLoading_shouldBeCalledWhenStatusIsLoading() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeShowLoading(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verify(mockedObserver).invoke()

        liveData.setError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setLoading()
        Mockito.verify(mockedObserver, times(2)).invoke()
    }

    @Test
    fun whenObserveHideLoading_shouldBeCalledWhenStatusIsLoading() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeHideLoading(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke()

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveSingleLoading_shouldBeCalledWhenStatusIsLoading_untilReceiveAStatusDifferentThenLOADING() {
        val mockedObserver: (Boolean) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleLoading(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verify(mockedObserver).invoke(true)

        Assert.assertTrue(liveData.hasObservers())

        liveData.setError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke(false)

        Assert.assertFalse(liveData.hasObservers())

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleShowLoading_shouldBeCalledWhenStatusIsLoading_onlyOnce() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleShowLoading(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verify(mockedObserver).invoke()

        liveData.setError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleHideLoading_shouldBeCalledWhenStatusIsLoading() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleHideLoading(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke()

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
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

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke()

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        Mockito.verify(mockedObserver, times(2)).invoke()
    }

    @Test
    fun whenObserveError_withExceptionData_shouldBeCalledWhenStatusIsError() {
        val mockedObserver: (Throwable) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeError(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val exception = IllegalStateException()
        liveData.setError(exception)
        Mockito.verify(mockedObserver).invoke(exception)

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.ERROR))
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveError_withErrorTransformer_withExceptionData_shouldBeCalledWhenStatusIsError() {
        val mockedTransformer: (Throwable) -> String = mock()
        val mockedObserver: (String) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeError(owner, mockedTransformer, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedTransformer)
        Mockito.verifyZeroInteractions(mockedObserver)

        val exception = IllegalStateException()
        Mockito.`when`(mockedTransformer.invoke(exception)).thenReturn("")
        liveData.setError(exception)
        Mockito.verify(mockedTransformer).invoke(exception)
        Mockito.verify(mockedObserver).invoke("")

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedTransformer)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.ERROR))
        Mockito.verifyNoMoreInteractions(mockedTransformer)
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveSingleError_shouldBeCalledWhenStatusIsError() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleError(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        Mockito.verify(mockedObserver).invoke()

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleError_withExceptionData_shouldBeCalledWhenStatusIsError() {
        val mockedObserver: (Throwable) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleError(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val exception = IllegalStateException()
        liveData.setError(exception)
        Mockito.verify(mockedObserver).invoke(exception)

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(exception)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.ERROR))
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleError_withErrorTransformer_withExceptionData_shouldBeCalledWhenStatusIsError() {
        val mockedTransformer: (Throwable) -> String = mock()
        val mockedObserver: (String) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleError(owner, mockedTransformer, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val exception = IllegalStateException()
        Mockito.`when`(mockedTransformer.invoke(exception)).thenReturn("")
        liveData.setError(exception)

        Mockito.verify(mockedTransformer).invoke(exception)
        Mockito.verify(mockedObserver).invoke("")

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedTransformer)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(exception)
        Mockito.verifyNoMoreInteractions(mockedTransformer)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.ERROR))
        Mockito.verifyNoMoreInteractions(mockedTransformer)
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

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        liveData.setData(data)
        Mockito.verify(mockedObserver).invoke(data)

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveData_withTransformer_shouldBeCalledWhenStatusIsSuccess() {
        val mockedObserver: (Int) -> Unit = mock()
        val mockedTransformer: (String) -> Int = mock()
        val liveData = MutableResponseLiveData<String>()
        liveData.observeData(owner, mockedTransformer, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        Mockito.`when`(mockedTransformer.invoke(data)).thenReturn(0)
        liveData.setData(data)
        Mockito.verify(mockedObserver).invoke(0)
        Mockito.verify(mockedTransformer).invoke(data)

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)
    }

    @Test
    fun whenObserveSuccess_shouldBeCalledWhenStatusIsSuccess() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSuccess(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        liveData.setData(data)
        Mockito.verify(mockedObserver).invoke()

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveSingleData_withTransformer_shouldBeCalledWhenStatusIsSuccess() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleData(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        liveData.setData(data)
        Mockito.verify(mockedObserver).invoke(data)

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setData(data)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleData_shouldBeCalledWhenStatusIsSuccess() {
        val mockedObserver: (Int) -> Unit = mock()
        val mockedTransformer: (String) -> Int = mock()
        val liveData = MutableResponseLiveData<String>()
        liveData.observeSingleData(owner, mockedTransformer, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        Mockito.`when`(mockedTransformer.invoke(data)).thenReturn(0)
        liveData.setData(data)
        Mockito.verify(mockedObserver).invoke(0)
        Mockito.verify(mockedTransformer).invoke(data)

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        liveData.setData(data)
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        Mirror().on(liveData).invoke().method("postValue").withArgs(DataResult<Any>(null, null, DataResultStatus.SUCCESS))
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleSuccess_shouldBeCalledWhenStatusIsSuccess() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observeSingleSuccess(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyZeroInteractions(mockedObserver)

        val data = "data"
        liveData.setData(data)
        Mockito.verify(mockedObserver).invoke()

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setData(data)
        Mockito.verifyZeroInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }
    // endregion

    // region Result
    @Test
    fun whenObserveResult_shouldBeCalledWhenResultIsPosted() {
        val mockedObserver: (DataResult<Any>) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { result(observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedObserver).invoke(result)

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verify(mockedObserver).invoke(result2)
    }

    @Test
    fun whenObserveResult_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedResult() {
        val mockedObserver: (Int) -> Unit = mock()
        val mockedTransformer: (DataResult<Any>) -> Int = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { result(observer = mockedObserver, transformer = mockedTransformer) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mockito.`when`(mockedTransformer.invoke(result)).thenReturn(0)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedTransformer).invoke(result)
        Mockito.verify(mockedObserver).invoke(0)

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mockito.`when`(mockedTransformer.invoke(result2)).thenReturn(1)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verify(mockedTransformer).invoke(result2)
        Mockito.verify(mockedObserver).invoke(1)
    }

    @Test
    fun whenObserveResult_shouldBeCalledWhenResultIsPosted_withoutArguments() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { result(observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedObserver).invoke()

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verify(mockedObserver, times(2)).invoke()
    }

    @Test
    fun whenObserveSingleResult_shouldBeCalledWhenResultIsPosted() {
        val mockedObserver: (DataResult<Any>) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { result(single = true, observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedObserver).invoke(result)

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verifyZeroInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleResult_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedResult() {
        val mockedObserver: (Int) -> Unit = mock()
        val mockedTransformer: (DataResult<Any>) -> Int = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { result(single = true, observer = mockedObserver, transformer = mockedTransformer) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mockito.`when`(mockedTransformer.invoke(result)).thenReturn(0)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedTransformer).invoke(result)
        Mockito.verify(mockedObserver).invoke(0)

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mockito.`when`(mockedTransformer.invoke(result2)).thenReturn(1)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verifyZeroInteractions(mockedTransformer)
        Mockito.verifyZeroInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleResult_shouldBeCalledWhenResultIsPosted_withoutArguments() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { result(single = true, observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedObserver).invoke()

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verifyZeroInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }
    // endregion

    // region Status
    @Test
    fun whenObserveStatus_shouldBeCalledWhenResultIsPosted() {
        val mockedObserver: (DataResultStatus) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { status(observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedObserver).invoke(DataResultStatus.SUCCESS)

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verify(mockedObserver).invoke(DataResultStatus.ERROR)
    }

    @Test
    fun whenObserveStatus_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedStatus() {
        val mockedObserver: (Int) -> Unit = mock()
        val mockedTransformer: (DataResultStatus) -> Int = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { status(observer = mockedObserver, transformer = mockedTransformer) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mockito.`when`(mockedTransformer.invoke(DataResultStatus.SUCCESS)).thenReturn(0)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedTransformer).invoke(DataResultStatus.SUCCESS)
        Mockito.verify(mockedObserver).invoke(0)

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mockito.`when`(mockedTransformer.invoke(DataResultStatus.ERROR)).thenReturn(1)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verify(mockedTransformer).invoke(DataResultStatus.ERROR)
        Mockito.verify(mockedObserver).invoke(1)
    }

    @Test
    fun whenObserveSingleStatus_shouldBeCalledWhenResultIsPosted() {
        val mockedObserver: (DataResultStatus) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { status(single = true, observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedObserver).invoke(DataResultStatus.SUCCESS)

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verifyZeroInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleStatus_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedStatus() {
        val mockedObserver: (Int) -> Unit = mock()
        val mockedTransformer: (DataResultStatus) -> Int = mock()
        val liveData = MutableResponseLiveData<Any>()
        liveData.observe(owner) { status(single = true, observer = mockedObserver, transformer = mockedTransformer) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        Mockito.`when`(mockedTransformer.invoke(DataResultStatus.SUCCESS)).thenReturn(0)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result)
        Mockito.verify(mockedTransformer).invoke(DataResultStatus.SUCCESS)
        Mockito.verify(mockedObserver).invoke(0)

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        Mockito.`when`(mockedTransformer.invoke(DataResultStatus.ERROR)).thenReturn(1)
        Mirror().on(liveData).invoke().method("setValue").withArgs(result2)
        Mockito.verifyZeroInteractions(mockedTransformer)
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

        liveData.setData(data)
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

        liveData.setData(data)
        onNextLiveData.observeData(owner, mockedObserver)

        Thread.sleep(10)

        Mockito.verify(mockedObserver).invoke(data)
        Mockito.verify(mockedOnNext).invoke(data)
    }
}