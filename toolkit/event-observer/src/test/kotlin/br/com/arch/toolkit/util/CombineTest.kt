package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.alwaysOnOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CombineTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `01 - LiveData - not initialized - combine`() = runTest {
        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA + liveDataB
        advanceUntilIdle()

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyNoInteractions(mockedObserver)

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `02 - LiveData - initialized A - combine`() = runTest {
        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA + liveDataB

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        verifyBlocking(mockedObserver, times(1)) { invoke(null to null) }

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `03 - LiveData - initialized B - combine`() = runTest {
        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA + liveDataB

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        verifyBlocking(mockedObserver, times(1)) { invoke(null to null) }

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `04 - LiveData - initialized A B - combine`() = runTest {
        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA + liveDataB

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        verifyBlocking(mockedObserver, times(1)) { invoke(null to null) }

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `05 - LiveData - not initialized - combine - coroutine`() = runTest {
        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA.plus(EmptyCoroutineContext, liveDataB)
        advanceUntilIdle()

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyNoInteractions(mockedObserver)

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `06 - LiveData - initialized A - combine - coroutine`() = runTest {
        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA.combine(EmptyCoroutineContext, liveDataB)
        advanceUntilIdle()

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke(null to null) }

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `07 - LiveData - initialized B - combine - coroutine`() = runTest {
        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA.combine(EmptyCoroutineContext, liveDataB)
        advanceUntilIdle()

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke(null to null) }

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `08 - LiveData - initialized A B - combine - coroutine`() = runTest {
        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA.combine(EmptyCoroutineContext, liveDataB)
        advanceUntilIdle()

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke(null to null) }

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `09 - LiveData - not initialized - combine - transform`() = runTest {
        val mockedObserver: (String?) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )
        val transform: (String?, Int?) -> String = mock(
            name = "Transform",
            defaultAnswer = { "${it.arguments[0]}|${it.arguments[1]}" }
        )

        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA.combine(
            context = EmptyCoroutineContext,
            other = liveDataB,
            transformDispatcher = Dispatchers.Main.immediate,
            transform = transform
        )
        advanceUntilIdle()

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyNoInteractions(mockedObserver)

        assertAllTransform(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `10 - LiveData - initialized A - combine - transform`() = runTest {
        val mockedObserver: (String?) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )
        val transform: (String?, Int?) -> String = mock(
            name = "Transform",
            defaultAnswer = { "${it.arguments[0]}|${it.arguments[1]}" }
        )

        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA.combine(
            context = EmptyCoroutineContext,
            other = liveDataB,
            transformDispatcher = Dispatchers.Main.immediate,
            transform = transform
        )
        advanceUntilIdle()

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke("null|null") }

        assertAllTransform(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `11 - LiveData - initialized B - combine - transform`() = runTest {
        val mockedObserver: (String?) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )
        val transform: (String?, Int?) -> String = mock(
            name = "Transform",
            defaultAnswer = { "${it.arguments[0]}|${it.arguments[1]}" }
        )

        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA.combine(
            context = EmptyCoroutineContext,
            other = liveDataB,
            transformDispatcher = Dispatchers.Main.immediate,
            transform = transform
        )
        advanceUntilIdle()

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke("null|null") }

        assertAllTransform(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `12 - LiveData - initialized A B - combine - transform`() = runTest {
        val mockedObserver: (String?) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )
        val transform: (String?, Int?) -> String = mock(
            name = "Transform",
            defaultAnswer = { "${it.arguments[0]}|${it.arguments[1]}" }
        )

        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA.combine(
            context = EmptyCoroutineContext,
            other = liveDataB,
            transformDispatcher = Dispatchers.Main.immediate,
            transform = transform
        )
        advanceUntilIdle()

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke("null|null") }

        assertAllTransform(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `13 - LiveData - initialized A B - combine - transform - exception`() = runTest {
        val mockedObserver: (String?) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )
        val transform: (String?, Int?) -> String = mock(
            name = "Transform",
            defaultAnswer = { error("Deu ruim!") }
        )

        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA.combine(
            context = EmptyCoroutineContext,
            other = liveDataB,
            transformDispatcher = Dispatchers.Main.immediate,
            transform = transform
        )
        advanceUntilIdle()

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke(null) }
    }

    private fun TestScope.assertAll(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        nullObserver: (Pair<String?, Int?>) -> Unit
    ) {
        val hasInitialValue = liveDataA.isInitialized || liveDataB.isInitialized

        liveDataA.value = null
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(1.plus(if (hasInitialValue) 1 else 0))) {
            invoke(null to null)
        }

        liveDataB.value = null
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(2.plus(if (hasInitialValue) 1 else 0))) {
            invoke(null to null)
        }

        liveDataA.value = "String"
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(1)) { invoke("String" to null) }

        liveDataB.value = 123
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(1)) { invoke("String" to 123) }

        liveDataA.value = null
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(1)) { invoke(null to 123) }

        liveDataB.value = null
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(3.plus(if (hasInitialValue) 1 else 0))) {
            invoke(null to null)
        }

        liveDataB.value = 123
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(2)) { invoke(null to 123) }

        liveDataA.value = "String"
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(2)) { invoke("String" to 123) }
    }

    private fun TestScope.assertAllTransform(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        nullObserver: (String?) -> Unit
    ) {
        val hasInitialValue = liveDataA.isInitialized || liveDataB.isInitialized

        liveDataA.value = null
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(1.plus(if (hasInitialValue) 1 else 0))) {
            invoke("null|null")
        }

        liveDataB.value = null
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(2.plus(if (hasInitialValue) 1 else 0))) {
            invoke("null|null")
        }

        liveDataA.value = "String"
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(1)) { invoke("String|null") }

        liveDataB.value = 123
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(1)) { invoke("String|123") }

        liveDataA.value = null
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(1)) { invoke("null|123") }

        liveDataB.value = null
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(3.plus(if (hasInitialValue) 1 else 0))) {
            invoke("null|null")
        }

        liveDataB.value = 123
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(2)) { invoke("null|123") }

        liveDataA.value = "String"
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(2)) { invoke("String|123") }
    }
}
