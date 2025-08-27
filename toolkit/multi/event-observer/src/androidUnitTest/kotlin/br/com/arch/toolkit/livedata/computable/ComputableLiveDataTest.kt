package br.com.arch.toolkit.livedata.computable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.util.observeNotNull
import br.com.arch.toolkit.util.observeSingle
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComputableLiveDataTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    private lateinit var mockedObserver: (Any) -> Unit
    private lateinit var computeObserver: () -> Unit
    private lateinit var abortObserver: () -> Unit

    @BeforeTest
    fun setup() {
        mockedObserver = mockk(relaxed = true)
        computeObserver = mockk(relaxed = true)
        abortObserver = mockk(relaxed = true)
    }

    @Test
    fun shouldComputeValueWhenBecomeActive() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verify(exactly = 0) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }

        liveData.observeNotNull(alwaysOnOwner, mockedObserver)

        Thread.sleep(50)
        verify(exactly = 1) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }
    }

    @Test
    fun invalidate_afterCompute_shouldComputeAgain() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verify(exactly = 0) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)

        // Call again if have observers
        liveData.invalidate()
        Thread.sleep(50)

        verify(exactly = 2) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }
    }

    @Test
    fun invalidate_afterCompute_withoutObservers_shouldSetComputedFlagToFalseAndNotCompute() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verify(exactly = 0) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(exactly = 1) { computeObserver.invoke() }

        liveData.removeObservers(alwaysOnOwner)
        assertTrue(liveData.hasComputed())

        // Call again if have observers
        liveData.invalidate()
        assertFalse(liveData.hasComputed())
        verify(exactly = 1) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }
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
        verify(exactly = 0) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }
        assertFalse(liveData.isRunningOrHasComputed)

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(exactly = 1) { computeObserver.invoke() }

        // Call again if have observers
        liveData.invalidate()
        assertTrue(liveData.isRunning)
        assertTrue(liveData.isRunningOrHasComputed)
        verify(exactly = 1) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }
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
        verify(exactly = 0) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }

        // Call when become active
        liveData.observeNotNull(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(exactly = 1) { computeObserver.invoke() }
        assertFalse(liveData.isRunning)
        assertFalse(liveData.hasComputed())
        verify(exactly = 0) { abortObserver.invoke() }
    }

    @Test
    fun becomeActive_afterCompute_shouldDoNothing() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verify(exactly = 0) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }

        // Call when become active
        liveData.observeSingle(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(exactly = 1) { computeObserver.invoke() }
        assertFalse(liveData.isRunning)
        assertTrue(liveData.hasComputed())

        liveData.observeSingle(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(exactly = 1) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }
    }

    @Test
    fun interrupt_withoutCompute_shouldCallAbort() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verify(exactly = 0) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }

        liveData.interrupt()
        verify(exactly = 1) { abortObserver.invoke() }
        assertFalse(liveData.isRunning)
        assertFalse(liveData.hasComputed())

        verify(exactly = 0) { computeObserver.invoke() }
    }

    @Test
    fun interrupt_afterCompute_shouldCallAbort() {
        val liveData = object : ComputableLiveData<Any>() {
            override fun compute() = computeObserver.invoke()

            override fun abort() = abortObserver.invoke()
        }
        verify(exactly = 0) { computeObserver.invoke() }
        verify(exactly = 0) { abortObserver.invoke() }

        liveData.observeSingle(alwaysOnOwner, mockedObserver)
        Thread.sleep(50)
        verify(exactly = 1) { computeObserver.invoke() }
        assertTrue(liveData.isRunningOrHasComputed)

        liveData.interrupt()
        verify(exactly = 1) { abortObserver.invoke() }
        assertFalse(liveData.isRunning)
        assertFalse(liveData.hasComputed())

        verify(exactly = 1) { computeObserver.invoke() }
    }
}
