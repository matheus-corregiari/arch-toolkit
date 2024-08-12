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
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CombineNotNullTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `01 - LiveData - not initialized - combineNotNull`() = runTest {
        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA.combineNotNull(liveDataB)
        advanceUntilIdle()

        val mockedObserver: (Pair<String, Int>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyNoInteractions(mockedObserver)

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `02 - LiveData - initialized A - combineNotNull`() = runTest {
        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA.combineNotNull(liveDataB)

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        verifyNoInteractions(mockedObserver)

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `03 - LiveData - initialized B - combineNotNull`() = runTest {
        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA.combineNotNull(liveDataB)

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        verifyNoInteractions(mockedObserver)

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `04 - LiveData - initialized A B - combineNotNull`() = runTest {
        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA.combineNotNull(liveDataB)

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        verifyNoInteractions(mockedObserver)

        assertAll(liveDataA, liveDataB, mockedObserver)
    }

    @Test
    fun `05 - LiveData - initialized A B - combineNotNull`() = runTest {
        val liveDataA = MutableLiveData("String")
        val liveDataB = MutableLiveData(123)
        val liveDataC = liveDataA.combineNotNull(liveDataB)
        advanceUntilIdle()

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke("String" to 123) }

        assertAll(liveDataA, liveDataB, mockedObserver, true)
    }

    @Test
    fun `06 - LiveData - not initialized - combineNotNull - coroutine`() = runTest {
        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA.combineNotNull(EmptyCoroutineContext, liveDataB)
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
    fun `07 - LiveData - initialized A - combineNotNull - coroutine`() = runTest {
        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>()
        val liveDataC = liveDataA.combineNotNull(EmptyCoroutineContext, liveDataB)
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
    fun `08 - LiveData - initialized B - combineNotNull - coroutine`() = runTest {
        val liveDataA = MutableLiveData<String>()
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA.combineNotNull(EmptyCoroutineContext, liveDataB)
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
    fun `09 - LiveData - initialized A B - combineNotNull - coroutine`() = runTest {
        val liveDataA = MutableLiveData<String>(null)
        val liveDataB = MutableLiveData<Int>(null)
        val liveDataC = liveDataA.combineNotNull(EmptyCoroutineContext, liveDataB)
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
    fun `10 - LiveData - initialized A B - combineNotNull - coroutine`() = runTest {
        val liveDataA = MutableLiveData("String")
        val liveDataB = MutableLiveData(123)
        val liveDataC = liveDataA.combineNotNull(EmptyCoroutineContext, liveDataB)
        advanceUntilIdle()

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke("String" to 123) }

        assertAll(liveDataA, liveDataB, mockedObserver, true)
    }

    @Test
    fun `11 - LiveData - not initialized - combineNotNull - transform`() = runTest {
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
        val liveDataC = liveDataA.combineNotNull(
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
    fun `12 - LiveData - initialized A - combineNotNull - transform`() = runTest {
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
        val liveDataC = liveDataA.combineNotNull(
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
    fun `13 - LiveData - initialized B - combineNotNull - transform`() = runTest {
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
        val liveDataC = liveDataA.combineNotNull(
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
    fun `14 - LiveData - initialized A B - combineNotNull - transform`() = runTest {
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
        val liveDataC = liveDataA.combineNotNull(
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
    fun `15 - LiveData - initialized A B - combineNotNull - transform`() = runTest {
        val mockedObserver: (String?) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )
        val transform: (String?, Int?) -> String = mock(
            name = "Transform",
            defaultAnswer = { "${it.arguments[0]}|${it.arguments[1]}" }
        )

        val liveDataA = MutableLiveData("String")
        val liveDataB = MutableLiveData(123)
        val liveDataC = liveDataA.combineNotNull(
            context = EmptyCoroutineContext,
            other = liveDataB,
            transformDispatcher = Dispatchers.Main.immediate,
            transform = transform
        )
        advanceUntilIdle()

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke("String|123") }

        assertAllTransform(liveDataA, liveDataB, mockedObserver, true)
    }

    @Test
    fun `16 - LiveData - initialized A B - combineNotNull - transform - exception`() = runTest {
        val mockedObserver: (String) -> Unit = mock(
            name = "Observer",
            defaultAnswer = { println("Result -> ${it.arguments[0]}") }
        )
        val transform: (String, Int) -> String = mock(
            name = "Transform",
            defaultAnswer = { error("Deu ruim!") }
        )

        val liveDataA = MutableLiveData("String")
        val liveDataB = MutableLiveData(123)
        val liveDataC = liveDataA.combineNotNull(
            context = EmptyCoroutineContext,
            other = liveDataB,
            transformDispatcher = Dispatchers.Main.immediate,
            transform = transform
        )
        advanceUntilIdle()

        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        verifyNoInteractions(mockedObserver)
    }

    @Suppress("MultiLineIfElse")
    private fun TestScope.assertAll(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        nullObserver: (Pair<String, Int>) -> Unit,
        initializedWithNonNull: Boolean = false
    ) {
        liveDataA.value = null
        advanceUntilIdle()
        if (initializedWithNonNull) verifyNoMoreInteractions(nullObserver)
        else verifyNoInteractions(nullObserver)

        liveDataB.value = null
        advanceUntilIdle()
        if (initializedWithNonNull) verifyNoMoreInteractions(nullObserver)
        else verifyNoInteractions(nullObserver)

        liveDataA.value = "String"
        advanceUntilIdle()
        if (initializedWithNonNull) verifyNoMoreInteractions(nullObserver)
        else verifyNoInteractions(nullObserver)

        liveDataB.value = 123
        advanceUntilIdle()
        verifyBlocking(
            nullObserver,
            times(1.plus(if (initializedWithNonNull) 1 else 0))
        ) { invoke("String" to 123) }

        liveDataA.value = null
        advanceUntilIdle()
        verifyNoMoreInteractions(nullObserver)

        liveDataB.value = null
        advanceUntilIdle()
        verifyNoMoreInteractions(nullObserver)

        liveDataB.value = 123
        advanceUntilIdle()
        verifyNoMoreInteractions(nullObserver)

        liveDataA.value = "String"
        advanceUntilIdle()
        verifyBlocking(
            nullObserver,
            times(2.plus(if (initializedWithNonNull) 1 else 0))
        ) { invoke("String" to 123) }
    }

    @Suppress("MultiLineIfElse")
    private fun TestScope.assertAllTransform(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        nullObserver: (String?) -> Unit,
        initializedWithNonNull: Boolean = false
    ) {
        val hasInitialValue = liveDataA.value != null && liveDataB.value != null

        liveDataA.value = null
        advanceUntilIdle()
        if (initializedWithNonNull) verifyNoMoreInteractions(nullObserver)
        else verifyNoInteractions(nullObserver)

        liveDataB.value = null
        advanceUntilIdle()
        if (initializedWithNonNull) verifyNoMoreInteractions(nullObserver)
        else verifyNoInteractions(nullObserver)

        liveDataA.value = "String"
        advanceUntilIdle()
        if (initializedWithNonNull) verifyNoMoreInteractions(nullObserver)
        else verifyNoInteractions(nullObserver)

        liveDataB.value = 123
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(1.plus(if (hasInitialValue) 1 else 0))) { invoke("String|123") }

        liveDataA.value = null
        advanceUntilIdle()
        verifyNoMoreInteractions(nullObserver)
        liveDataB.value = null
        advanceUntilIdle()
        verifyNoMoreInteractions(nullObserver)
        liveDataB.value = 123
        advanceUntilIdle()
        verifyNoMoreInteractions(nullObserver)
        liveDataA.value = "String"
        advanceUntilIdle()
        verifyBlocking(nullObserver, times(2.plus(if (hasInitialValue) 1 else 0))) { invoke("String|123") }
    }
}
