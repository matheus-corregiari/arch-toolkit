package br.com.arch.toolkit.livedata.extention

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.common.DataResultStatus
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class LiveDataExtensionTest {

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

    @Test
    fun observeShouldHandleOnlyNonNullObjects() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeNotNull(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyNoInteractions(mockedObserver)

        liveData.postValue("nonNullData")
        Mockito.verify(mockedObserver).invoke("nonNullData")
    }

    @Test
    fun observeShouldHandleOnlyNullObjects() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeNull(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verify(mockedObserver).invoke()

        liveData.postValue("nonNullData")
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun observeSingleShouldHandleOnlyNonNullObjectOnce() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeSingle(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyNoInteractions(mockedObserver)
        Assert.assertTrue(liveData.hasObservers())

        liveData.postValue("nonNullData")
        Mockito.verify(mockedObserver).invoke("nonNullData")
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun observeUntilShouldHandleObjectsUntilReturnTrue() {
        val mockedObserver: (Any?) -> Boolean = mock()
        Mockito.`when`(mockedObserver.invoke(null)).thenReturn(false)
        Mockito.`when`(mockedObserver.invoke("FALSE")).thenReturn(false)
        Mockito.`when`(mockedObserver.invoke("TRUE")).thenReturn(true)

        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeUntil(owner, mockedObserver)

        liveData.postValue(null)
        Assert.assertTrue(liveData.hasObservers())
        Mockito.verify(mockedObserver).invoke(null)

        liveData.postValue("FALSE")
        Assert.assertTrue(liveData.hasObservers())
        Mockito.verify(mockedObserver).invoke("FALSE")

        liveData.postValue("TRUE")
        Mockito.verify(mockedObserver).invoke("TRUE")
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun responseLiveDataOfValueShouldReturnAnInstanceWithTheValueAlreadySet() {
        val liveData = responseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun responseLiveDataOfErrorShouldReturnAnInstanceWithTheThrowableAlreadySet() {
        val liveData = responseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun mutableResponseLiveDataOfValueShouldReturnAnInstanceWithTheValueAlreadySet() {
        val liveData = mutableResponseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun mutableResponseLiveDataOfErrorShouldReturnAnInstanceWithTheThrowableAlreadySet() {
        val liveData = mutableResponseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun swapResponseLiveDataOfValueShouldReturnAnInstanceWithTheValueAlreadySet() {
        val liveData = swapResponseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun swapResponseLiveDataOfErrorShouldReturnAnInstanceWithTheThrowableAlreadySet() {
        val liveData = swapResponseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }
}