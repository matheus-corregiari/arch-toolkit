package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
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
class ChainTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    //region No Coroutine
    @Test
    fun `01 - LiveData - not initialized - chainWith`() =
        `LiveData - not initialized - chainWith`(null)

    @Test
    fun `02 - LiveData - initialized A - chainWith`() =
        `LiveData - initialized A - chainWith`(null)

    @Test
    fun `03 - LiveData - initialized B - chainWith`() =
        `LiveData - initialized B - chainWith`(null)

    @Test
    fun `04 - LiveData - initialized A B - chainWith`() =
        `LiveData - initialized A B - chainWith`(null)

    @Test
    fun `05 - LiveData - initialized A B - chainWith - data`() =
        `LiveData - initialized A B - chainWith - data`(null)

    @Test
    fun `06 - LiveData - initialized A B - chainWith - exception condition`() =
        `LiveData - initialized A B - chainWith - exception condition`(null)

    @Test
    fun `07 - LiveData - initialized A B - chainWith - exception livedata`() =
        `LiveData - initialized A B - chainWith - exception livedata`(null)
    //endregion

    //region Coroutine
    @Test
    fun `08 - LiveData - not initialized - chainWith - coroutine`() =
        `LiveData - not initialized - chainWith`(EmptyCoroutineContext)

    @Test
    fun `09 - LiveData - initialized A - chainWith - coroutine`() =
        `LiveData - initialized A - chainWith`(EmptyCoroutineContext)

    @Test
    fun `10 - LiveData - initialized B - chainWith - coroutine`() =
        `LiveData - initialized B - chainWith`(EmptyCoroutineContext)

    @Test
    fun `11 - LiveData - initialized A B - chainWith - coroutine`() =
        `LiveData - initialized A B - chainWith`(EmptyCoroutineContext)

    @Test
    fun `12 - LiveData - initialized A B - chainWith - coroutine`() =
        `LiveData - initialized A B - chainWith - data`(EmptyCoroutineContext)

    @Test
    fun `13 - LiveData - initialized A B - chainWith - coroutine - exception condition`() =
        `LiveData - initialized A B - chainWith - exception condition`(EmptyCoroutineContext)

    @Test
    fun `14 - LiveData - initialized A B - chainWith - coroutine - exception livedata`() =
        `LiveData - initialized A B - chainWith - exception livedata`(EmptyCoroutineContext)
    //endregion

    //region Transform
    @Test
    fun `15 - LiveData - not initialized - chainWith - transform`() = executeChainWithTransform(
        liveDataA = MutableLiveData<String>(),
        liveDataB = MutableLiveData<Int>(),
        block = { liveDataA, liveDataB, liveData, condition, transform, observer ->
            coVerify(exactly = 0) { condition(any()) }
            coVerify(exactly = 0) { liveData(any()) }
            coVerify(exactly = 0) { transform(any(), any()) }
            coVerify(exactly = 0) { observer(any()) }

            liveDataA.value = null
            advanceUntilIdle()
            coVerify(exactly = 1) { condition(null) }
            coVerify(exactly = 0) { liveData(any()) }
            coVerify(exactly = 0) { transform(any(), any()) }
            coVerify(exactly = 0) { observer(any()) }

            liveDataB.value = null
            advanceUntilIdle()
            coVerify(exactly = 1) { condition(null) }
            coVerify(exactly = 0) { liveData(any()) }
            coVerify(exactly = 0) { transform(any(), any()) }
            coVerify(exactly = 0) { observer(any()) }

            liveDataA.value = "String"
            advanceUntilIdle()
            coVerify(exactly = 1) { condition("String") }
            coVerify(exactly = 1) { liveData("String") }
            coVerify(exactly = 1) { transform("String", null) }
            coVerify(exactly = 1) { observer("String|null") }

            liveDataB.value = 123
            advanceUntilIdle()
            coVerify(exactly = 2) { condition(any()) }
            coVerify(exactly = 1) { liveData(any()) }
            coVerify(exactly = 1) { transform("String", 123) }
            coVerify(exactly = 1) { observer("String|123") }

            liveDataA.value = null
            advanceUntilIdle()
            coVerify(exactly = 2) { condition(null) }
            coVerify(exactly = 1) { liveData(any()) }
            coVerify(exactly = 2) { transform(any(), any()) }
            coVerify(exactly = 2) { observer(any()) }

            liveDataB.value = null
            advanceUntilIdle()
            coVerify(exactly = 2) { condition(null) }
            coVerify(exactly = 1) { liveData(any()) }
            coVerify(exactly = 2) { transform(any(), any()) }
            coVerify(exactly = 2) { observer(any()) }

            liveDataB.value = 123
            advanceUntilIdle()
            coVerify(exactly = 2) { condition(null) }
            coVerify(exactly = 1) { liveData(any()) }
            coVerify(exactly = 2) { transform(any(), any()) }
            coVerify(exactly = 2) { observer(any()) }

            liveDataA.value = "String"
            advanceUntilIdle()
            coVerify(exactly = 2) { condition("String") }
            coVerify(exactly = 2) { liveData("String") }
            coVerify(exactly = 2) { transform("String", 123) }
            coVerify(exactly = 2) { observer("String|123") }
        }
    )

    @Test
    fun `16 - LiveData - initialized A - chainWith - transform`() = executeChainWithTransform(
        liveDataA = MutableLiveData<String>(null),
        liveDataB = MutableLiveData<Int>(),
        block = { _, _, liveData, condition, transform, observer ->
            coVerify(exactly = 1) { condition(null) }
            coVerify(exactly = 0) { liveData(any()) }
            coVerify(exactly = 0) { transform(any(), any()) }
            coVerify(exactly = 0) { observer(any()) }
        }
    )

    @Test
    fun `17 - LiveData - initialized B - chainWith - transform`() = executeChainWithTransform(
        liveDataA = MutableLiveData<String>(),
        liveDataB = MutableLiveData<Int>(null),
        block = { _, _, liveData, condition, transform, observer ->
            coVerify(exactly = 0) { condition(any()) }
            coVerify(exactly = 0) { liveData(any()) }
            coVerify(exactly = 0) { transform(any(), any()) }
            coVerify(exactly = 0) { observer(any()) }
        }
    )

    @Test
    fun `18 - LiveData - initialized A B - chainWith - transform`() = executeChainWithTransform(
        liveDataA = MutableLiveData<String>(null),
        liveDataB = MutableLiveData<Int>(null),
        block = { _, _, liveData, condition, transform, observer ->
            coVerify(exactly = 1) { condition(null) }
            coVerify(exactly = 0) { liveData(any()) }
            coVerify(exactly = 0) { transform(any(), any()) }
            coVerify(exactly = 0) { observer(any()) }
        }
    )

    @Test
    fun `19 - LiveData - initialized A B - chainWith - transform`() = executeChainWithTransform(
        liveDataA = MutableLiveData<String>("String"),
        liveDataB = MutableLiveData<Int>(123),
        block = { _, _, liveData, condition, transform, observer ->
            coVerify(exactly = 1) { condition("String") }
            coVerify(exactly = 1) { liveData("String") }
            coVerify(exactly = 1) { transform("String", 123) }
            coVerify(exactly = 1) { observer("String|123") }
        }
    )

    @Test
    fun `20 - LiveData - initialized A B - chainWith - transform - exception condition`() =
        executeChainWithTransform(
            liveDataA = MutableLiveData<String>("String"),
            liveDataB = MutableLiveData<Int>(123),
            conditionException = true,
            block = { _, _, liveData, condition, transform, observer ->
                coVerify(exactly = 1) { condition("String") }
                coVerify(exactly = 0) { liveData(any()) }
                coVerify(exactly = 0) { transform(any(), any()) }
                coVerify(exactly = 0) { observer(any()) }
            }
        )

    @Test
    fun `21 - LiveData - initialized A B - chainWith - transform - exception livedata`() =
        executeChainWithTransform(
            liveDataA = MutableLiveData<String>("String"),
            liveDataB = MutableLiveData<Int>(123),
            liveDataException = true,
            block = { _, _, liveData, condition, transform, observer ->
                coVerify(exactly = 1) { condition("String") }
                coVerify(exactly = 1) { liveData("String") }
                coVerify(exactly = 0) { transform(any(), any()) }
                coVerify(exactly = 0) { observer(any()) }
            }
        )

    @Test
    fun `22 - LiveData - initialized A B - chainWith - transform - exception transform`() =
        executeChainWithTransform(
            liveDataA = MutableLiveData<String>("String"),
            liveDataB = MutableLiveData<Int>(123),
            transformException = true,
            block = { _, _, liveData, condition, transform, observer ->
                coVerify(exactly = 1) { condition("String") }
                coVerify(exactly = 1) { liveData("String") }
                coVerify(exactly = 1) { transform("String", 123) }
                coVerify(exactly = 1) { observer(null) }
            }
        )
    //endregion

    //region Scenarios
    private fun `LiveData - not initialized - chainWith`(context: CoroutineContext?) =
        executeChainWith(
            context = context,
            liveDataA = MutableLiveData<String>(),
            liveDataB = MutableLiveData<Int>(),
            block = { liveDataA, liveDataB, liveData, condition, observer ->
                coVerify(exactly = 0) { condition(any()) }
                coVerify(exactly = 0) { liveData(any()) }
                coVerify(exactly = 0) { observer(any()) }

                liveDataA.value = null
                advanceUntilIdle()
                coVerify(exactly = 1) { condition(null) }
                coVerify(exactly = 0) { liveData(any()) }
                coVerify(exactly = 0) { observer(any()) }

                liveDataB.value = null
                advanceUntilIdle()
                coVerify(exactly = 1) { condition(null) }
                coVerify(exactly = 0) { liveData(any()) }
                coVerify(exactly = 0) { observer(any()) }

                liveDataA.value = "String"
                advanceUntilIdle()
                coVerify(exactly = 1) { condition("String") }
                coVerify(exactly = 1) { liveData("String") }
                coVerify(exactly = 1) { observer("String" to null) }

                liveDataB.value = 123
                advanceUntilIdle()
                coVerify(exactly = 2) { condition(any()) }
                coVerify(exactly = 1) { liveData(any()) }
                coVerify(exactly = 1) { observer("String" to 123) }

                liveDataA.value = null
                advanceUntilIdle()
                coVerify(exactly = 2) { condition(null) }
                coVerify(exactly = 1) { liveData(any()) }
                coVerify(exactly = 2) { observer(any()) }

                liveDataB.value = null
                advanceUntilIdle()
                coVerify(exactly = 2) { condition(null) }
                coVerify(exactly = 1) { liveData(any()) }
                coVerify(exactly = 2) { observer(any()) }

                liveDataB.value = 123
                advanceUntilIdle()
                coVerify(exactly = 2) { condition(null) }
                coVerify(exactly = 1) { liveData(any()) }
                coVerify(exactly = 2) { observer(any()) }

                liveDataA.value = "String"
                advanceUntilIdle()
                coVerify(exactly = 2) { condition("String") }
                coVerify(exactly = 2) { liveData("String") }
                coVerify(exactly = 2) { observer("String" to 123) }
            }
        )

    private fun `LiveData - initialized A - chainWith`(context: CoroutineContext?) =
        executeChainWith(
            context = context,
            liveDataA = MutableLiveData<String>(null),
            liveDataB = MutableLiveData<Int>(),
            block = { _, _, liveData, condition, observer ->
                coVerify(exactly = 1) { condition(null) }
                coVerify(exactly = 0) { liveData(any()) }
                coVerify(exactly = 0) { observer(any()) }
            }
        )

    private fun `LiveData - initialized B - chainWith`(context: CoroutineContext?) =
        executeChainWith(
            context = context,
            liveDataA = MutableLiveData<String>(),
            liveDataB = MutableLiveData<Int>(null),
            block = { _, _, liveData, condition, observer ->
                coVerify(exactly = 0) { condition(any()) }
                coVerify(exactly = 0) { liveData(any()) }
                coVerify(exactly = 0) { observer(any()) }
            }
        )

    private fun `LiveData - initialized A B - chainWith`(context: CoroutineContext?) =
        executeChainWith(
            context = context,
            liveDataA = MutableLiveData<String>(null),
            liveDataB = MutableLiveData<Int>(null),
            block = { _, _, liveData, condition, observer ->
                coVerify(exactly = 1) { condition(null) }
                coVerify(exactly = 0) { liveData(any()) }
                coVerify(exactly = 0) { observer(any()) }
            }
        )

    private fun `LiveData - initialized A B - chainWith - data`(context: CoroutineContext?) =
        executeChainWith(
            context = context,
            liveDataA = MutableLiveData<String>("String"),
            liveDataB = MutableLiveData<Int>(123),
            block = { _, _, liveData, condition, observer ->
                coVerify(exactly = 1) { condition("String") }
                coVerify(exactly = 1) { liveData("String") }
                coVerify(exactly = 1) { observer("String" to 123) }
            }
        )

    private fun `LiveData - initialized A B - chainWith - exception condition`(context: CoroutineContext?) =
        executeChainWith(
            context = context,
            liveDataA = MutableLiveData<String>("String"),
            liveDataB = MutableLiveData<Int>(123),
            conditionException = true,
            block = { _, _, liveData, condition, observer ->
                coVerify(exactly = 1) { condition("String") }
                coVerify(exactly = 0) { liveData(any()) }
                coVerify(exactly = 0) { observer(any()) }
            }
        )

    private fun `LiveData - initialized A B - chainWith - exception livedata`(context: CoroutineContext?) =
        executeChainWith(
            context = context,
            liveDataA = MutableLiveData<String>("String"),
            liveDataB = MutableLiveData<Int>(123),
            liveDataException = true,
            block = { _, _, liveData, condition, observer ->
                coVerify(exactly = 1) { condition("String") }
                coVerify(exactly = 1) { liveData("String") }
                coVerify(exactly = 0) { observer(any()) }
            }
        )
    //endregion

    //region Auxiliary
    @Suppress("LongParameterList")
    private fun executeChainWith(
        context: CoroutineContext? = null,
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        conditionException: Boolean = false,
        liveDataException: Boolean = false,
        block: suspend TestScope.(
            a: MutableLiveData<String>,
            b: MutableLiveData<Int>,
            liveData: suspend (String?) -> LiveData<Int>,
            condition: suspend (String?) -> Boolean,
            observer: suspend (Pair<String?, Int?>) -> Unit
        ) -> Unit
    ) = runTest {
        val mockedCondition: (String?) -> Boolean = mockk("Condition")
        coEvery { mockedCondition.invoke(any()) } coAnswers {
            if (conditionException) error("") else it.invocation.args[0] == "String"
        }

        val mockedLiveData: (String?) -> LiveData<Int> = mockk("LiveData")
        coEvery { mockedLiveData.invoke(any()) } coAnswers {
            if (liveDataException) error("") else liveDataB
        }

        val mockedObserver: (Pair<String?, Int?>) -> Unit = mockk("Observer")
        coEvery { mockedObserver.invoke(any()) } coAnswers {
            println("Result -> ${it.invocation.args[0]}")
        }

        val liveDataC = if (context == null) {
            liveDataA.chainWith(mockedLiveData, mockedCondition)
        } else {
            liveDataA.chainWith(context, mockedLiveData, mockedCondition)
        }
        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        block(liveDataA, liveDataB, mockedLiveData, mockedCondition, mockedObserver)
    }

    @Suppress("LongParameterList")
    private fun executeChainWithTransform(
        liveDataA: MutableLiveData<String>,
        liveDataB: MutableLiveData<Int>,
        conditionException: Boolean = false,
        liveDataException: Boolean = false,
        transformException: Boolean = false,
        block: suspend TestScope.(
            a: MutableLiveData<String>,
            b: MutableLiveData<Int>,
            liveData: suspend (String?) -> LiveData<Int>,
            condition: suspend (String?) -> Boolean,
            transform: suspend (String?, Int?) -> String?,
            observer: suspend (String?) -> Unit
        ) -> Unit
    ) = runTest {
        val mockedCondition: suspend (String?) -> Boolean = mockk("Condition")
        coEvery { mockedCondition.invoke(any()) } coAnswers {
            if (conditionException) error("") else it.invocation.args[0] == "String"
        }

        val mockedLiveData: suspend (String?) -> LiveData<Int> = mockk("LiveData")
        coEvery { mockedLiveData.invoke(any()) } coAnswers {
            if (liveDataException) error("") else liveDataB
        }

        val mockedTransform: suspend (String?, Int?) -> String? = mockk("Transform")
        coEvery { mockedTransform.invoke(any(), any()) } coAnswers {
            if (transformException) error("") else "${it.invocation.args[0]}|${it.invocation.args[1]}"
        }

        val mockedObserver: (String?) -> Unit = mockk("Observer")
        coEvery { mockedObserver.invoke(any()) } coAnswers {
            println("Result -> ${it.invocation.args[0]}")
        }

        val liveDataC = liveDataA.chainWith<String, Int, String>(
            context = EmptyCoroutineContext,
            other = mockedLiveData,
            condition = mockedCondition,
            transform = Dispatchers.Main to mockedTransform
        )
        liveDataC.observe(alwaysOnOwner, mockedObserver)
        advanceUntilIdle()
        block(
            liveDataA,
            liveDataB,
            mockedLiveData,
            mockedCondition,
            mockedTransform,
            mockedObserver
        )
    }
    //endregion
}
