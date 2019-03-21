package br.com.arch.toolkit.livedata.extention

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class LiveDataExtensionTest {

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
    fun observeShouldHandleOnlyNonNullObjects() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observe(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.postValue("nonNullData")
        Mockito.verify(mockedObserver).invoke("nonNullData")
    }

    @Test
    fun observeSingleShouldHandleOnlyNonNullObjectOnce() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeSingle(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyZeroInteractions(mockedObserver)
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
}