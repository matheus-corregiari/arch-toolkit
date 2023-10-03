package br.com.arch.toolkit.livedata.extention

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.common.DataResultStatus
import com.nhaarman.mockitokotlin2.mock
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
class LiveDataExtensionTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var owner = object : LifecycleOwner {
        private val registry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle
            get() {
                registry.currentState = Lifecycle.State.RESUMED
                return registry
            }
    }

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun observeShouldHandleOnlyNonNullObjects() = runTest {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeNotNull(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyNoInteractions(mockedObserver)

        liveData.postValue("nonNullData")
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke("nonNullData") }
    }

    @Test
    fun observeShouldHandleOnlyNullObjects() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeNull(owner, mockedObserver)

        liveData.postValue(null)

        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        liveData.postValue("nonNullData")
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun observeSingleShouldHandleOnlyNonNullObjectOnce() = runTest {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeSingle(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyNoInteractions(mockedObserver)
        Assert.assertTrue(liveData.hasObservers())

        liveData.postValue("nonNullData")
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke("nonNullData") }
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun observeUntilShouldHandleObjectsUntilReturnTrue() = runTest {
        val mockedObserver: (Any?) -> Boolean = mock()
        Mockito.`when`(mockedObserver.invoke(null)).thenReturn(false)
        Mockito.`when`(mockedObserver.invoke("FALSE")).thenReturn(false)
        Mockito.`when`(mockedObserver.invoke("TRUE")).thenReturn(true)

        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeUntil(owner, mockedObserver)

        liveData.postValue(null)
        Assert.assertTrue(liveData.hasObservers())
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(null) }

        liveData.postValue("FALSE")
        Assert.assertTrue(liveData.hasObservers())
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke("FALSE") }

        liveData.postValue("TRUE")
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke("TRUE") }
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun responseLiveDataOfValueShouldReturnAnInstanceWithTheValueAlreadySet() = runTest {
        val liveData = responseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun responseLiveDataOfErrorShouldReturnAnInstanceWithTheThrowableAlreadySet() = runTest {
        val liveData = responseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun mutableResponseLiveDataOfValueShouldReturnAnInstanceWithTheValueAlreadySet() = runTest {
        val liveData = mutableResponseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun mutableResponseLiveDataOfErrorShouldReturnAnInstanceWithTheThrowableAlreadySet() = runTest {
        val liveData = mutableResponseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }

    @Test
    fun swapResponseLiveDataOfValueShouldReturnAnInstanceWithTheValueAlreadySet() = runTest {
        val liveData = swapResponseLiveDataOf("value")

        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun swapResponseLiveDataOfErrorShouldReturnAnInstanceWithTheThrowableAlreadySet() = runTest {
        val liveData = swapResponseLiveDataOf<Any>(Throwable())

        Assert.assertNull(liveData.data)
        Assert.assertTrue(liveData.status == DataResultStatus.ERROR)
        Assert.assertNotNull(liveData.error)
    }
}