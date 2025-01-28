package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.alwaysOnOwner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
import kotlin.coroutines.CoroutineContext
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
    fun `01 - LiveData - not initialized - combine`() =
        `LiveData - not initialized - combine`(null)

    @Test
    fun `02 - LiveData - initialized A - combine`() =
        `LiveData - initialized A - combine`(null)

    @Test
    fun `03 - LiveData - initialized B - combine`() =
        `LiveData - initialized B - combine`(null)

    @Test
    fun `04 - LiveData - initialized A B - combine`() =
        `LiveData - initialized A B - combine`(null)

    @Test
    fun `05 - LiveData - not initialized - combine - coroutine`() =
        `LiveData - not initialized - combine`(EmptyCoroutineContext)

    @Test
    fun `06 - LiveData - initialized A - combine - coroutine`() =
        `LiveData - initialized A - combine`(EmptyCoroutineContext)

    @Test
    fun `07 - LiveData - initialized B - combine - coroutine`() =
        `LiveData - initialized B - combine`(EmptyCoroutineContext)

    @Test
    fun `08 - LiveData - initialized A B - combine - coroutine`() =
        `LiveData - initialized A B - combine`(EmptyCoroutineContext)

    //region Transform
    @Test
    fun `09 - LiveData - not initialized - combine - transform`() = executeCombineTransform(
        liveDataA = MutableLiveData(),
        liveDataB = MutableLiveData(),
        block = { liveDataA, liveDataB, _, mockedObserver ->
            coVerify(exactly = 0) { mockedObserver.invoke(any()) }
            assertAllTransform(liveDataA, liveDataB, mockedObserver)
        }
    )

    @Test
    fun `10 - LiveData - initialized A - combine - transform`() = executeCombineTransform(
        liveDataA = MutableLiveData(null),
        liveDataB = MutableLiveData(),
        block = { liveDataA, liveDataB, _, mockedObserver ->
            coVerify(exactly = 1) { mockedObserver.invoke("null|null") }
            assertAllTransform(liveDataA, liveDataB, mockedObserver)
        }
    )

    @Test
    fun `11 - LiveData - initialized B - combine - transform`() = executeCombineTransform(
        liveDataA = MutableLiveData(),
        liveDataB = MutableLiveData(null),
        block = { liveDataA, liveDataB, _, mockedObserver ->
            coVerify(exactly = 1) { mockedObserver.invoke("null|null") }
            assertAllTransform(liveDataA, liveDataB, mockedObserver)
        }
    )

    @Test
    fun `12 - LiveData - initialized A B - combine - transform`() = executeCombineTransform(
        liveDataA = MutableLiveData(null),
        liveDataB = MutableLiveData(null),
        block = { liveDataA, liveDataB, _, mockedObserver ->
            coVerify(exactly = 1) { mockedObserver.invoke("null|null") }
            assertAllTransform(liveDataA, liveDataB, mockedObserver)
        }
    )

    @Test
    fun `13 - LiveData - initialized A B - combine - transform - exception - omit`() =
        executeCombineTransform(
            liveDataA = MutableLiveData("String"),
            liveDataB = MutableLiveData(123),
            transformException = true,
            block = { _, _, _, mockedObserver ->
                coVerify(exactly = 0) { mockedObserver.invoke(any()) }
            }
        )

    @Test
    fun `14 - LiveData - initialized A B - combine - transform - exception - null`() =
        executeCombineTransform(
            liveDataA = MutableLiveData("String"),
            liveDataB = MutableLiveData(123),
            failMode = Transform.Mode.NULL_WHEN_FAIL,
            transformException = true,
            block = { _, _, _, mockedObserver ->
                coVerify(exactly = 1) { mockedObserver.invoke(null) }
            }
        )

    @Test
    fun `15 - LiveData - initialized A B - combine - transform - exception - fallback`() =
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

    private fun `LiveData - not initialized - combine`(context: CoroutineContext?) = executeCombine(
        context = context,
        liveDataA = MutableLiveData(),
        liveDataB = MutableLiveData(),
        block = { liveDataA, liveDataB, mockedObserver ->
            coVerify(exactly = 0) { mockedObserver.invoke(any()) }
            assertAll(liveDataA, liveDataB, mockedObserver)
        }
    )

    private fun `LiveData - initialized A - combine`(context: CoroutineContext?) = executeCombine(
        context = context,
        liveDataA = MutableLiveData(null),
        liveDataB = MutableLiveData(),
        block = { liveDataA, liveDataB, mockedObserver ->
            coVerify(exactly = 1) { mockedObserver.invoke(null to null) }
            assertAll(liveDataA, liveDataB, mockedObserver)
        }
    )

    private fun `LiveData - initialized B - combine`(context: CoroutineContext?) = executeCombine(
        context = context,
        liveDataA = MutableLiveData(),
        liveDataB = MutableLiveData(null),
        block = { liveDataA, liveDataB, mockedObserver ->
            coVerify(exactly = 1) { mockedObserver.invoke(null to null) }
            assertAll(liveDataA, liveDataB, mockedObserver)
        }
    )

    private fun `LiveData - initialized A B - combine`(context: CoroutineContext?) = executeCombine(
        context = context,
        liveDataA = MutableLiveData(null),
        liveDataB = MutableLiveData(null),
        block = { liveDataA, liveDataB, mockedObserver ->
            coVerify(exactly = 1) { mockedObserver.invoke(null to null) }
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
            observer: suspend (Pair<String?, Int?>) -> Unit
        ) -> Unit
    ) = runTest {
        val mockedObserver: (Pair<String?, Int?>) -> Unit = mockk("Observer")
        coEvery { mockedObserver.invoke(any()) } coAnswers {
            println("Result -> ${it.invocation.args[0]}")
        }

        val liveDataC = if (context == null) {
            liveDataA.combine(liveDataB)
        } else {
            liveDataA.combine(context, liveDataB)
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
        failMode: Transform.Mode = Transform.Mode.OMIT_WHEN_FAIL,
        block: suspend TestScope.(
            a: MutableLiveData<String>,
            b: MutableLiveData<Int>,
            transformer: suspend (String?, Int?) -> String?,
            observer: suspend (String?) -> Unit
        ) -> Unit
    ) = runTest {
        val mockedObserver: (String?) -> Unit = mockk("Observer")
        coEvery { mockedObserver.invoke(any()) } coAnswers {
            println("Result -> ${it.invocation.args[0]}")
        }
        val mockedTransform: suspend (String?, Int?) -> String? = mockk("Transform")
        coEvery { mockedTransform.invoke(any(), any()) } coAnswers {
            if (transformException) error("") else "${it.invocation.args[0]}|${it.invocation.args[1]}"
        }

        val liveDataC = liveDataA.combine(
            other = liveDataB,
            transform = Transform.Nullable.Custom(
                dispatcher = Dispatchers.Main,
                failMode = failMode,
                func = mockedTransform,
                onErrorReturn = if (useFallback) ({ "fallback" }) else null
            )
        )
        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        block(liveDataA, liveDataB, mockedTransform, mockedObserver)
    }

    private fun TestScope.assertAll(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        observer: suspend (Pair<String?, Int?>) -> Unit
    ) {
        val hasInitialValue = liveDataA.isInitialized || liveDataB.isInitialized

        liveDataA.value = null
        advanceUntilIdle()
        coVerify(exactly = 1.plus(if (hasInitialValue) 1 else 0)) { observer.invoke(null to null) }

        liveDataB.value = null
        advanceUntilIdle()
        coVerify(exactly = 2.plus(if (hasInitialValue) 1 else 0)) { observer.invoke(null to null) }

        liveDataA.value = "String"
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke("String" to null) }

        liveDataB.value = 123
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke("String" to 123) }

        liveDataA.value = null
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke(null to 123) }

        liveDataB.value = null
        advanceUntilIdle()
        coVerify(exactly = 3.plus(if (hasInitialValue) 1 else 0)) { observer.invoke(null to null) }

        liveDataB.value = 123
        advanceUntilIdle()
        coVerify(exactly = 2) { observer.invoke(null to 123) }

        liveDataA.value = "String"
        advanceUntilIdle()
        coVerify(exactly = 2) { observer.invoke("String" to 123) }
    }

    private fun TestScope.assertAllTransform(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        observer: suspend (String?) -> Unit
    ) {
        val hasInitialValue = liveDataA.isInitialized || liveDataB.isInitialized

        liveDataA.value = null
        advanceUntilIdle()
        coVerify(exactly = 1.plus(if (hasInitialValue) 1 else 0)) { observer.invoke("null|null") }

        liveDataB.value = null
        advanceUntilIdle()
        coVerify(exactly = 2.plus(if (hasInitialValue) 1 else 0)) { observer.invoke("null|null") }

        liveDataA.value = "String"
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke("String|null") }

        liveDataB.value = 123
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke("String|123") }

        liveDataA.value = null
        advanceUntilIdle()
        coVerify(exactly = 1) { observer.invoke("null|123") }

        liveDataB.value = null
        advanceUntilIdle()
        coVerify(exactly = 3.plus(if (hasInitialValue) 1 else 0)) { observer.invoke("null|null") }

        liveDataB.value = 123
        advanceUntilIdle()
        coVerify(exactly = 2) { observer.invoke("null|123") }

        liveDataA.value = "String"
        advanceUntilIdle()
        coVerify(exactly = 2) { observer.invoke("String|123") }
    }
}
