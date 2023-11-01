package br.com.arch.toolkit.livedata.computable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.util.observeNotNull
import br.com.arch.toolkit.util.observeSingle
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.kotlin.mock

class ComputableLiveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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
        Mockito.verifyNoInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)

        liveData.observeNotNull(alwaysOnOwner, mockedObserver)

        Thread.sleep(50)
        Mockito.verify(computeObserver).invoke()
        Mockito.verifyNoInteractions(abortObserver)
    }

    @Test
    fun invalidate_afterCompute_shouldComputeAgain() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyNoInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)

        // Call again if have observers
        liveData.invalidate()
        Thread.sleep(50)

        Mockito.verify(computeObserver, times(2)).invoke()
        Mockito.verifyNoInteractions(abortObserver)
    }

    @Test
    fun invalidate_afterCompute_withoutObservers_shouldSetComputedFlagToFalseAndNotCompute() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyNoInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        Mockito.verify(computeObserver).invoke()

        liveData.removeObservers(alwaysOnOwner)
        Assert.assertTrue(liveData.hasComputed())

        // Call again if have observers
        liveData.invalidate()
        Assert.assertFalse(liveData.hasComputed())
        Mockito.verifyNoMoreInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)
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
        Mockito.verifyNoInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)
        Assert.assertFalse(liveData.isRunningOrHasComputed)

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        Mockito.verify(computeObserver).invoke()

        // Call again if have observers
        liveData.invalidate()
        Assert.assertTrue(liveData.isRunning)
        Assert.assertTrue(liveData.isRunningOrHasComputed)
        Mockito.verifyNoMoreInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)
    }

    @Test
    fun computeWithException_shouldSetCputedToFalse() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() {
                computeObserver.invoke()
                throw IllegalStateException()
            }

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyNoInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        Mockito.verify(computeObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertFalse(liveData.hasComputed())
        Mockito.verifyNoInteractions(abortObserver)
    }

    @Test
    fun becomeActive_afterCompute_shouldDoNothing() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyNoInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)

        // Call when become active
        liveData.observeSingle(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        Mockito.verify(computeObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertTrue(liveData.hasComputed())

        liveData.observeSingle(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        Mockito.verifyNoMoreInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)
    }

    @Test
    fun interrupt_withoutCompute_shouldCallAbort() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyNoInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)

        liveData.interrupt()
        Mockito.verify(abortObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertFalse(liveData.hasComputed())

        Mockito.verifyNoInteractions(computeObserver)
    }

    @Test
    fun interrupt_afterCompute_shouldCallAbort() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyNoInteractions(computeObserver)
        Mockito.verifyNoInteractions(abortObserver)

        liveData.observeSingle(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        Mockito.verify(computeObserver).invoke()
        Assert.assertTrue(liveData.isRunningOrHasComputed)

        liveData.interrupt()
        Mockito.verify(abortObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertFalse(liveData.hasComputed())

        Mockito.verifyNoMoreInteractions(computeObserver)
    }
}