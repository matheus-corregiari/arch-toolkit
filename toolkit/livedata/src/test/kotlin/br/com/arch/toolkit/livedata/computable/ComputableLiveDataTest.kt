package br.com.arch.toolkit.livedata.computable

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import br.com.arch.toolkit.livedata.extention.observe
import br.com.arch.toolkit.livedata.extention.observeSingle
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class ComputableLiveDataTest {

    @Rule
    @get:Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockedObserver: (Any) -> Unit
    private lateinit var computeObserver: () -> Unit
    private lateinit var abortObserver: () -> Unit

    private var owner = object : LifecycleOwner {
        private val registry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle {
            registry.markState(Lifecycle.State.RESUMED)
            return registry
        }
    }

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
        Mockito.verifyZeroInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)

        liveData.observe(owner, mockedObserver)

        Thread.sleep(20)
        Mockito.verify(computeObserver).invoke()
        Mockito.verifyZeroInteractions(abortObserver)
    }

    @Test
    fun invalidate_afterCompute_shouldComputeAgain() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyZeroInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)

        // Call when become active
        liveData.observe(owner, mockedObserver)
        Thread.sleep(20)

        // Call again if have observers
        liveData.invalidate()
        Thread.sleep(20)

        Mockito.verify(computeObserver, times(2)).invoke()
        Mockito.verifyZeroInteractions(abortObserver)
    }

    @Test
    fun invalidate_afterCompute_withoutObservers_shouldSetComputedFlagToFalseAndNotCompute() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyZeroInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)

        // Call when become active
        liveData.observe(owner, mockedObserver)
        Thread.sleep(20)
        Mockito.verify(computeObserver).invoke()

        liveData.removeObservers(owner)
        Assert.assertTrue(liveData.hasComputed())

        // Call again if have observers
        liveData.invalidate()
        Assert.assertFalse(liveData.hasComputed())
        Mockito.verifyNoMoreInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)
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
        Mockito.verifyZeroInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)
        Assert.assertFalse(liveData.isRunningOrHasComputed)

        // Call when become active
        liveData.observe(owner, mockedObserver)
        Thread.sleep(20)
        Mockito.verify(computeObserver).invoke()

        // Call again if have observers
        liveData.invalidate()
        Assert.assertTrue(liveData.isRunning)
        Assert.assertTrue(liveData.isRunningOrHasComputed)
        Mockito.verifyNoMoreInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)
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
        Mockito.verifyZeroInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)

        // Call when become active
        liveData.observe(owner, mockedObserver)
        Thread.sleep(20)
        Mockito.verify(computeObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertFalse(liveData.hasComputed())
        Mockito.verifyZeroInteractions(abortObserver)
    }

    @Test
    fun becomeActive_afterCompute_shouldDoNothing() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyZeroInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)

        // Call when become active
        liveData.observeSingle(owner, mockedObserver)
        Thread.sleep(20)
        Mockito.verify(computeObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertTrue(liveData.hasComputed())

        liveData.observeSingle(owner, mockedObserver)
        Thread.sleep(20)
        Mockito.verifyNoMoreInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)
    }

    @Test
    fun interrupt_withoutCompute_shouldCallAbort() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyZeroInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)

        liveData.interrupt()
        Mockito.verify(abortObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertFalse(liveData.hasComputed())

        Mockito.verifyZeroInteractions(computeObserver)
    }

    @Test
    fun interrupt_afterCompute_shouldCallAbort() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        Mockito.verifyZeroInteractions(computeObserver)
        Mockito.verifyZeroInteractions(abortObserver)

        liveData.observeSingle(owner, mockedObserver)
        Thread.sleep(15)
        Mockito.verify(computeObserver).invoke()
        Assert.assertTrue(liveData.isRunningOrHasComputed)

        liveData.interrupt()
        Mockito.verify(abortObserver).invoke()
        Assert.assertFalse(liveData.isRunning)
        Assert.assertFalse(liveData.hasComputed())

        Mockito.verifyZeroInteractions(computeObserver)
    }
}