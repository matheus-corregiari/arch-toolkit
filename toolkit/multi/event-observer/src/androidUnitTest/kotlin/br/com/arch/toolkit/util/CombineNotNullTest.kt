package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.alwaysOnOwner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CombineNotNullTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `01 - LiveData - not initialized - combineNotNull`() =
        `LiveData - not initialized - combineNotNull`(null)

    @Test
    fun `02 - LiveData - initialized A - combineNotNull`() =
        `LiveData - initialized A - combineNotNull`(null)

    @Test
    fun `03 - LiveData - initialized B - combineNotNull`() =
        `LiveData - initialized B - combineNotNull`(null)

    @Test
    fun `04 - LiveData - initialized A B - combineNotNull`() =
        `LiveData - initialized A B - combineNotNull`(null)

    @Test
    fun `05 - LiveData - not initialized - combineNotNull - coroutine`() =
        `LiveData - not initialized - combineNotNull`(EmptyCoroutineContext)

    @Test
    fun `06 - LiveData - initialized A - combineNotNull - coroutine`() =
        `LiveData - initialized A - combineNotNull`(EmptyCoroutineContext)

    @Test
    fun `07 - LiveData - initialized B - combineNotNull - coroutine`() =
        `LiveData - initialized B - combineNotNull`(EmptyCoroutineContext)

    @Test
    fun `08 - LiveData - initialized A B - combineNotNull - coroutine`() =
        `LiveData - initialized A B - combineNotNull`(EmptyCoroutineContext)

    //region Transform
    @Test
    fun `09 - LiveData - not initialized - combineNotNull - transform`() = executeCombineTransform(
        liveDataA = MutableLiveData(),
        liveDataB = MutableLiveData(),
        block = { liveDataA, liveDataB, _, mockedObserver ->
            coVerify(exactly = 0) { mockedObserver.invoke(any()) }
            assertAllTransform(liveDataA, liveDataB, mockedObserver)
        }
    )

    @Test
    fun `10 - LiveData - initialized A - combineNotNull - transform`() = executeCombineTransform(
        liveDataA = MutableLiveData(null),
        liveDataB = MutableLiveData(),
        block = { liveDataA, liveDataB, _, mockedObserver ->
            coVerify(exactly = 0) { mockedObserver.invoke(any()) }
            assertAllTransform(liveDataA, liveDataB, mockedObserver)
        }
    )

    @Test
    fun `11 - LiveData - initialized B - combineNotNull - transform`() = executeCombineTransform(
        liveDataA = MutableLiveData(),
        liveDataB = MutableLiveData(null),
        block = { liveDataA, liveDataB, _, mockedObserver ->
            coVerify(exactly = 0) { mockedObserver.invoke(any()) }
            assertAllTransform(liveDataA, liveDataB, mockedObserver)
        }
    )

    @Test
    fun `12 - LiveData - initialized A B - combineNotNull - transform`() = executeCombineTransform(
        liveDataA = MutableLiveData("String"),
        liveDataB = MutableLiveData(null),
        block = { liveDataA, liveDataB, _, mockedObserver ->
            coVerify(exactly = 0) { mockedObserver.invoke(any()) }
            assertAllTransform(liveDataA, liveDataB, mockedObserver)
        }
    )

    @Test
    fun `13 - LiveData - initialized A B - combineNotNull - transform - exception`() =
        executeCombineTransform(
            liveDataA = MutableLiveData("String"),
            liveDataB = MutableLiveData(123),
            transformException = true,
            block = { _, _, _, mockedObserver ->
                coVerify(exactly = 0) { mockedObserver.invoke(any()) }
            }
        )

    @Test
    fun `14 - LiveData - initialized A B - combineNotNull - transform - exception - fallback`() =
        executeCombineTransform(
            liveDataA = MutableLiveData("String"),
            liveDataB = MutableLiveData(123),
            transformException = true,
            useFallback = true,
            block = { _, _, _, mockedObserver ->
                coVerify(exactly = 1) { mockedObserver.invoke("fallback") }
            }
        )

    //endregion

    private fun `LiveData - not initialized - combineNotNull`(context: CoroutineContext?) =
        executeCombine(
            context = context,
            liveDataA = MutableLiveData(),
            liveDataB = MutableLiveData(),
            block = { liveDataA, liveDataB, mockedObserver ->
                coVerify(exactly = 0) { mockedObserver.invoke(any()) }
                assertAll(liveDataA, liveDataB, mockedObserver)
            }
        )

    private fun `LiveData - initialized A - combineNotNull`(context: CoroutineContext?) =
        executeCombine(
            context = context,
            liveDataA = MutableLiveData(null),
            liveDataB = MutableLiveData(),
            block = { liveDataA, liveDataB, mockedObserver ->
                coVerify(exactly = 0) { mockedObserver.invoke(any()) }
                assertAll(liveDataA, liveDataB, mockedObserver)
            }
        )

    private fun `LiveData - initialized B - combineNotNull`(context: CoroutineContext?) =
        executeCombine(
            context = context,
            liveDataA = MutableLiveData(),
            liveDataB = MutableLiveData(null),
            block = { liveDataA, liveDataB, mockedObserver ->
                coVerify(exactly = 0) { mockedObserver.invoke(any()) }
                assertAll(liveDataA, liveDataB, mockedObserver)
            }
        )

    private fun `LiveData - initialized A B - combineNotNull`(context: CoroutineContext?) =
        executeCombine(
            context = context,
            liveDataA = MutableLiveData(null),
            liveDataB = MutableLiveData(null),
            block = { liveDataA, liveDataB, mockedObserver ->
                coVerify(exactly = 0) { mockedObserver.invoke(any()) }
                assertAll(liveDataA, liveDataB, mockedObserver)
            }
        )

    //region Auxiliary
    @Suppress("LongParameterList")
    private fun executeCombine(
        context: CoroutineContext? = null,
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        block: suspend TestScope.(
            a: MutableLiveData<String>,
            b: MutableLiveData<Int>,
            observer: suspend (Pair<String, Int>) -> Unit
        ) -> Unit
    ) = runTest {
        val mockedObserver: (Pair<String?, Int?>) -> Unit = mockk("Observer")
        coEvery { mockedObserver.invoke(any()) } coAnswers {
            println("Result -> ${it.invocation.args[0]}")
        }

        val liveDataC = if (context == null) {
            liveDataA.combineNotNull(liveDataB)
        } else {
            liveDataA.combineNotNull(context, liveDataB)
        }
        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        block(liveDataA, liveDataB, mockedObserver)
    }

    @Suppress("LongParameterList")
    private fun executeCombineTransform(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        transformException: Boolean = false,
        useFallback: Boolean = false,
        block: suspend TestScope.(
            a: MutableLiveData<String>,
            b: MutableLiveData<Int>,
            transformer: suspend (String, Int) -> String,
            observer: suspend (String) -> Unit
        ) -> Unit
    ) = runTest {
        val mockedObserver: (String) -> Unit = mockk("Observer")
        coEvery { mockedObserver.invoke(any()) } coAnswers {
            println("Result -> ${it.invocation.args[0]}")
        }
        val mockedTransform: suspend (String, Int) -> String = mockk("Transform")
        coEvery { mockedTransform.invoke(any(), any()) } coAnswers {
            if (transformException) error("") else "${it.invocation.args[0]}|${it.invocation.args[1]}"
        }

        val liveDataC = liveDataA.combineNotNull(
            other = liveDataB,
            transform = Transform.NotNull.Fallback(
                dispatcher = Dispatchers.Main,
                func = mockedTransform,
                onErrorReturn = if (useFallback) ({ "fallback" }) else null
            )
        )
        advanceUntilIdle()
        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        block(liveDataA, liveDataB, mockedTransform, mockedObserver)
    }

    private fun TestScope.assertAll(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        observer: suspend (Pair<String, Int>) -> Unit
    ) {
        liveDataA.value = null
        advanceUntilIdle()
        coVerify(exactly = 0) { observer.invoke(any()) }

        liveDataB.value = null
        advanceUntilIdle()
        coVerify(exactly = 0) { observer.invoke(any()) }

        liveDataA.value = "String"
        advanceUntilIdle()
        coVerify(exactly = 0) { observer.invoke(any()) }

        liveDataB.value = 123
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke("String" to 123) }

        liveDataA.value = null
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke(any()) }

        liveDataB.value = null
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke(any()) }

        liveDataB.value = 123
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke(any()) }

        liveDataA.value = "String"
        advanceUntilIdle()
        coVerify(exactly = 2) { observer.invoke("String" to 123) }
    }

    private fun TestScope.assertAllTransform(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        observer: suspend (String) -> Unit
    ) {
        liveDataA.value = null
        advanceUntilIdle()
        coVerify(exactly = 0) { observer.invoke(any()) }

        liveDataB.value = null
        advanceUntilIdle()
        coVerify(exactly = 0) { observer.invoke(any()) }

        liveDataA.value = "String"
        advanceUntilIdle()
        coVerify(exactly = 0) { observer.invoke(any()) }

        liveDataB.value = 123
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke("String|123") }

        liveDataA.value = null
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke(any()) }

        liveDataB.value = null
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke(any()) }

        liveDataB.value = 123
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke(any()) }

        liveDataA.value = "String"
        advanceUntilIdle()
        coVerify(exactly = 2) { observer.invoke("String|123") }
    }
}
