package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.alwaysOnOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ChainTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `01 - LiveData - not initialized - chainWith`() = executeChainWith(
        liveDataA = MutableLiveData<String>(),
        liveDataB = MutableLiveData<Int>()
    ) { a, b, c -> a.chainWith({ b }, c) }

    @Test
    fun `02 - LiveData - initialized A - chainWith`() = executeChainWith(
        liveDataA = MutableLiveData<String>(null),
        liveDataB = MutableLiveData<Int>()
    ) { a, b, c -> a.chainWith({ b }, c) }

    @Test
    fun `03 - LiveData - initialized B - chainWith`() = executeChainWith(
        liveDataA = MutableLiveData<String>(),
        liveDataB = MutableLiveData<Int>(null)
    ) { a, b, c -> a.chainWith({ b }, c) }

    @Test
    fun `04 - LiveData - initialized A B - chainWith`() = executeChainWith(
        liveDataA = MutableLiveData<String>(null),
        liveDataB = MutableLiveData<Int>(null)
    ) { a, b, c -> a.chainWith({ b }, c) }

    @Test
    fun `05 - LiveData - initialized A B - chainWith`() = executeChainWith(
        liveDataA = MutableLiveData<String>("String"),
        liveDataB = MutableLiveData<Int>(123)
    ) { a, b, c -> a.chainWith({ b }, c) }

    // 06 TODO missing exception inside condition
    // 07 TODO missing exception inside livedata block

    @Test
    fun `08 - LiveData - not initialized - chainWith - coroutine`() = executeChainWith(
        liveDataA = MutableLiveData<String>(),
        liveDataB = MutableLiveData<Int>()
    ) { a, b, c -> a.chainWith(EmptyCoroutineContext, { b }, c) }

    @Test
    fun `09 - LiveData - initialized A - chainWith - coroutine`() = executeChainWith(
        liveDataA = MutableLiveData<String>(null),
        liveDataB = MutableLiveData<Int>()
    ) { a, b, c -> a.chainWith(EmptyCoroutineContext, { b }, c) }

    @Test
    fun `10 - LiveData - initialized B - chainWith - coroutine`() = executeChainWith(
        liveDataA = MutableLiveData<String>(),
        liveDataB = MutableLiveData<Int>(null)
    ) { a, b, c -> a.chainWith(EmptyCoroutineContext, { b }, c) }

    @Test
    fun `11 - LiveData - initialized A B - chainWith - coroutine`() = executeChainWith(
        liveDataA = MutableLiveData<String>(null),
        liveDataB = MutableLiveData<Int>(null)
    ) { a, b, c -> a.chainWith(EmptyCoroutineContext, { b }, c) }

    @Test
    fun `12 - LiveData - initialized A B - chainWith - coroutine`() = executeChainWith(
        liveDataA = MutableLiveData<String>("String"),
        liveDataB = MutableLiveData<Int>(123)
    ) { a, b, c -> a.chainWith(EmptyCoroutineContext, { b }, c) }

    // 13 TODO missing exception inside condition
    // 14 TODO missing exception inside livedata block
    // 15 TODO transform
    // 16 TODO transform A
    // 17 TODO transform B
    // 18 TODO transform A B
    // 19 TODO transform A B value
    // 20 TODO transform A B exception condition
    // 21 TODO transform A B exception liveDataBock
    // 22 TODO transform A B exception transform

    private fun executeChainWith(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        liveDataCBlock: (
            LiveData<String>,
            LiveData<Int>,
            condition: (String?) -> Boolean
        ) -> LiveData<Pair<String?, Int?>>
    ) = runTest {
        val mockedObserver: (Pair<String?, Int?>) -> Unit =
            mock(name = "Observer", defaultAnswer = { println("Result -> ${it.arguments[0]}") })
        val mockedCondition: (String?) -> Boolean =
            mock(name = "Condition", defaultAnswer = { it.arguments[0] == "String" })
        val liveDataC = liveDataCBlock(liveDataA, liveDataB, mockedCondition)
        advanceUntilIdle()

        val aInitialized = liveDataA.isInitialized
        val bInitialized = liveDataB.isInitialized
        val hasInitialValue = aInitialized && bInitialized
        val initialValue =
            if (hasInitialValue) (liveDataA.value to liveDataB.value).toNotNull() else null

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        when {
            initialValue != null -> {
                verifyBlocking(mockedCondition, times(1)) { invoke("String") }
                verifyBlocking(mockedObserver, times(1)) { invoke("String" to 123) }
            }

            aInitialized -> {
                verifyBlocking(mockedCondition, times(1)) { invoke(null) }
                verifyNoInteractions(mockedObserver)
            }

            else -> {
                verifyNoInteractions(mockedCondition)
                verifyNoInteractions(mockedObserver)
            }
        }

        liveDataA.value = null
        advanceUntilIdle()
        when {
            initialValue != null -> {
                verifyBlocking(mockedCondition, times(1)) { invoke(null) }
                verifyNoMoreInteractions(mockedObserver)
            }

            aInitialized -> {
                verifyBlocking(mockedCondition, times(2)) { invoke(null) }
                verifyNoInteractions(mockedObserver)
            }

            else -> {
                verifyBlocking(mockedCondition, times(1)) { invoke(null) }
                verifyNoInteractions(mockedObserver)
            }
        }

        liveDataB.value = null
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedCondition)
        when {
            initialValue != null -> verifyNoMoreInteractions(mockedObserver)
            else -> verifyNoInteractions(mockedObserver)
        }

        liveDataA.value = "String"
        advanceUntilIdle()
        when {
            initialValue != null -> {
                verifyBlocking(mockedCondition, times(2)) { invoke("String") }
                verifyBlocking(mockedObserver, times(1)) { invoke("String" to null) }
            }

            else -> {
                verifyBlocking(mockedCondition, times(1)) { invoke("String") }
                verifyBlocking(mockedObserver, times(1)) { invoke("String" to null) }
            }
        }

        liveDataB.value = 123
        advanceUntilIdle()
        when {
            initialValue != null -> {
                verifyNoMoreInteractions(mockedCondition)
                verifyBlocking(mockedObserver, times(2)) { invoke("String" to 123) }
            }

            else -> {
                verifyNoMoreInteractions(mockedCondition)
                verifyBlocking(mockedObserver, times(1)) { invoke("String" to 123) }
            }
        }

        liveDataA.value = null
        advanceUntilIdle()
        when {
            initialValue != null -> verifyBlocking(mockedCondition, times(2)) { invoke(null) }
            aInitialized -> verifyBlocking(mockedCondition, times(3)) { invoke(null) }
            else -> verifyBlocking(mockedCondition, times(2)) { invoke(null) }
        }
        verifyNoMoreInteractions(mockedObserver)

        liveDataB.value = null
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedCondition)
        verifyNoMoreInteractions(mockedObserver)

        liveDataB.value = 123
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedCondition)
        verifyNoMoreInteractions(mockedObserver)

        liveDataA.value = "String"
        advanceUntilIdle()
        when {
            initialValue != null -> verifyBlocking(mockedCondition, times(3)) { invoke("String") }
            else -> verifyBlocking(mockedCondition, times(2)) { invoke("String") }
        }
        when {
            initialValue != null ->
                verifyBlocking(mockedObserver, times(3)) { invoke("String" to 123) }

            else -> verifyBlocking(mockedObserver, times(2)) { invoke("String" to 123) }
        }
    }
}
