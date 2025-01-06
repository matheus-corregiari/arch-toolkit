package br.com.arch.toolkit.livedata.computable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.util.observeNotNull
import br.com.arch.toolkit.util.observeSingle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.Mockito.times
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ComputableLiveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    private lateinit var mockedObserver: (Any) -> Unit
    private lateinit var computeObserver: () -> Unit
    private lateinit var abortObserver: () -> Unit

    @Before
    fun setup() {
        mockedObserver = mock()
        computeObserver = mock()
        abortObserver = mock()
    }

    @Test
    fun shouldComputeValueWhenBecomeActive() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verifyNoInteractions(computeObserver)
        verifyNoInteractions(abortObserver)

        liveData.observeNotNull(alwaysOnOwner, mockedObserver)

        Thread.sleep(50)
        verify(computeObserver).invoke()
        verifyNoInteractions(abortObserver)
    }

    @Test
    fun invalidate_afterCompute_shouldComputeAgain() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verifyNoInteractions(computeObserver)
        verifyNoInteractions(abortObserver)

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)

        // Call again if have observers
        liveData.invalidate()
        Thread.sleep(50)

        verify(computeObserver, times(2)).invoke()
        verifyNoInteractions(abortObserver)
    }

    @Test
    fun invalidate_afterCompute_withoutObservers_shouldSetComputedFlagToFalseAndNotCompute() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verifyNoInteractions(computeObserver)
        verifyNoInteractions(abortObserver)

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(computeObserver).invoke()

        liveData.removeObservers(alwaysOnOwner)
        Assert.assertTrue(liveData.hasComputed())

        // Call again if have observers
        liveData.invalidate()
        Assert.assertFalse(liveData.hasComputed())
        verifyNoMoreInteractions(computeObserver)
        verifyNoInteractions(abortObserver)
    }

    @Test
    fun invalidate_whileIsRunning_shouldDoNothing() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() {
                computeObserver.invoke()
                Thread.sleep(500)
            }

            override fun abort() = abortObserver.invoke()
        }
        verifyNoInteractions(computeObserver)
        verifyNoInteractions(abortObserver)
        Assert.assertFalse(liveData.isRunningOrHasComputed)

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(computeObserver).invoke()

        // Call again if have observers
        liveData.invalidate()
        Assert.assertTrue(liveData.isRunning)
        Assert.assertTrue(liveData.isRunningOrHasComputed)
        verifyNoMoreInteractions(computeObserver)
        verifyNoInteractions(abortObserver)
    }

    @Test
    fun computeWithException_shouldSetCputedToFalse() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() {
                computeObserver.invoke()
                error("error")
            }

            override fun abort() = abortObserver.invoke()
        }
        verifyNoInteractions(computeObserver)
        verifyNoInteractions(abortObserver)

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(computeObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertFalse(liveData.hasComputed())
        verifyNoInteractions(abortObserver)
    }

    @Test
    fun becomeActive_afterCompute_shouldDoNothing() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verifyNoInteractions(computeObserver)
        verifyNoInteractions(abortObserver)

        // Call when become active
        liveData.observeSingle(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(computeObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertTrue(liveData.hasComputed())

        liveData.observeSingle(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verifyNoMoreInteractions(computeObserver)
        verifyNoInteractions(abortObserver)
    }

    @Test
    fun interrupt_withoutCompute_shouldCallAbort() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verifyNoInteractions(computeObserver)
        verifyNoInteractions(abortObserver)

        liveData.interrupt()
        verify(abortObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertFalse(liveData.hasComputed())

        verifyNoInteractions(computeObserver)
    }

    @Test
    fun interrupt_afterCompute_shouldCallAbort() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verifyNoInteractions(computeObserver)
        verifyNoInteractions(abortObserver)

        liveData.observeSingle(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(computeObserver).invoke()
        Assert.assertTrue(liveData.isRunningOrHasComputed)

        liveData.interrupt()
        verify(abortObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertFalse(liveData.hasComputed())

        verifyNoMoreInteractions(computeObserver)
    }
}
