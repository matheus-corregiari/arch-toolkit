package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.testSetValue
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ResponseLiveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `00 - init without param, should init with null value`() = runTest {
        val liveData = ResponseLiveData<Any>()
        liveData.scope(this)
        liveData.transformDispatcher(Dispatchers.Main.immediate)

        Assert.assertNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertNull(liveData.status)
    }

    @Test
    fun `01 - init with param, should init with param value`() = runTest {
        val value = DataResult("String", null, SUCCESS)
        val liveData = ResponseLiveData(value)

        Assert.assertEquals(value, liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals("String", liveData.data)
        Assert.assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `02 - Should keep observer until all events has been handled`() = runTest {
        fun singleTrue() {
            val liveData = ResponseLiveData<Any>()
            liveData.transformDispatcher(Dispatchers.Main.immediate)
            liveData.observe(alwaysOnOwner) {
                status(single = true) { /* @see ObserverWrapper Tests */ }
            }
            Assert.assertTrue(liveData.hasObservers())
            liveData.testSetValue(dataResultLoading())
            advanceUntilIdle()
            Assert.assertFalse(liveData.hasObservers())
        }

        fun singleFalse() {
            val liveData = ResponseLiveData<Any>()
            liveData.scope(this)
            liveData.transformDispatcher(Dispatchers.Main.immediate)
            liveData.observe(alwaysOnOwner) {
                status(single = false) { /* @see ObserverWrapper Tests */ }
            }
            Assert.assertTrue(liveData.hasObservers())
            liveData.testSetValue(dataResultLoading())
            advanceUntilIdle()
            Assert.assertTrue(liveData.hasObservers())
        }

        singleTrue()
        singleFalse()
    }

    @Test
    fun `03 - transform`() = runTest {
        val mockTransform: (DataResult<Int>) -> DataResult<String> = mock()
        whenever(mockTransform.invoke(any())) doReturn dataResultSuccess("String")

        val liveData = ResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val transformedLiveData = liveData.transform(mockTransform)

        Assert.assertNull(liveData.value)
        Assert.assertNull(transformedLiveData.value)

        // Will change the value only after the observe
        liveData.testSetValue(dataResultSuccess(123))
        advanceUntilIdle()
        Assert.assertEquals(dataResultSuccess(123), liveData.value)
        Assert.assertNull(transformedLiveData.value)
        verifyNoInteractions(mockTransform)

        // Now the transformation will be triggered
        transformedLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        Assert.assertEquals(dataResultSuccess("String"), transformedLiveData.value)
        verifyBlocking(mockTransform) { invoke(dataResultSuccess(123)) }
    }

    @Test
    fun `04 - onError`() = runTest {
        val error = IllegalStateException("error")
        val mockOnError: (Throwable) -> Unit = mock()
        val liveData = ResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.onError(mockOnError)

        Assert.assertNull(liveData.value)
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockOnError)

        // Will change the value only after the observe
        liveData.testSetValue(dataResultError(error))
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultError<Int>(error),
            liveData.value
        )
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockOnError)

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultError<Int>(error),
            onErrorLiveData.value
        )
        verifyBlocking(mockOnError) { invoke(error) }
    }

    @Test
    fun `05 - onErrorReturn`() = runTest {
        val error = IllegalStateException("error")
        val mockOnErrorReturn: (Throwable) -> Int = mock()
        whenever(mockOnErrorReturn.invoke(error)) doReturn 123
        val liveData = ResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.onErrorReturn(mockOnErrorReturn)

        Assert.assertNull(liveData.value)
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockOnErrorReturn)

        // Will change the value only after the observe
        liveData.testSetValue(dataResultError(error))
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultError<Int>(error),
            liveData.value
        )
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockOnErrorReturn)

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        Assert.assertEquals(
            DataResult(123, error, SUCCESS),
            onErrorLiveData.value
        )
        verifyBlocking(mockOnErrorReturn) { invoke(error) }
    }

    @Test
    fun `06 - mapError`() = runTest {
        val error = IllegalStateException("error")
        val error2 = IllegalStateException("error2")
        val mockMapError: (Throwable) -> Throwable = mock()
        whenever(mockMapError.invoke(error)) doReturn error2
        val liveData = ResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.mapError(mockMapError)

        Assert.assertNull(liveData.value)
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockMapError)

        // Will change the value only after the observe
        liveData.testSetValue(dataResultError(error))
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultError<Int>(error),
            liveData.value
        )
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockMapError)

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultError<Int>(error2),
            onErrorLiveData.value
        )
        verifyBlocking(mockMapError) { invoke(error) }
    }

    @Test
    fun `07 - map`() = runTest {
        val mockMap: (Int) -> String = mock()
        whenever(mockMap.invoke(123)) doReturn "String"
        val liveData = ResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.map(mockMap)

        Assert.assertNull(liveData.value)
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockMap)

        // Will change the value only after the observe
        liveData.testSetValue(dataResultSuccess(123))
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultSuccess(123),
            liveData.value
        )
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockMap)

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultSuccess("String"),
            onErrorLiveData.value
        )
        verifyBlocking(mockMap) { invoke(123) }
    }

    @Test
    fun `08 - onNext`() = runTest {
        val mockOnNext: (Int) -> Unit = mock()
        val liveData = ResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.onNext(mockOnNext)

        Assert.assertNull(liveData.value)
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockOnNext)

        // Will change the value only after the observe
        liveData.testSetValue(dataResultSuccess(123))
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultSuccess(123),
            liveData.value
        )
        Assert.assertNull(onErrorLiveData.value)
        verifyNoInteractions(mockOnNext)

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        Assert.assertEquals(
            dataResultSuccess(123),
            onErrorLiveData.value
        )
        verifyBlocking(mockOnNext) { invoke(123) }
    }

    @Test
    fun `09 - mergeWith - plus`() = runTest {
        val liveDataA = ResponseLiveData(dataResultSuccess(123))
        val liveDataB = ResponseLiveData(dataResultSuccess("String"))
        val liveDataMerge = liveDataA + liveDataB

        Assert.assertEquals(dataResultSuccess(123), liveDataA.value)
        Assert.assertEquals(dataResultSuccess("String"), liveDataB.value)
        Assert.assertEquals(dataResultSuccess(123 to "String"), liveDataMerge.value)
    }

    @Test
    fun `10 - mergeWith with tag`() = runTest {
        val liveDataA = ResponseLiveData(dataResultSuccess(123))
        val liveDataB = ResponseLiveData(dataResultSuccess("String"))
        val liveDataMerge = liveDataA.mergeWith("tagA", "tagB" to liveDataB)

        Assert.assertEquals(dataResultSuccess(123), liveDataA.value)
        Assert.assertEquals(dataResultSuccess("String"), liveDataB.value)
        Assert.assertEquals(
            dataResultSuccess(
                mapOf(
                    "tagA" to 123,
                    "tagB" to "String",
                )
            ),
            liveDataMerge.value
        )
    }

    @Test
    fun `11 - followedBy - both success`() = runTest {
        val liveDataA = ResponseLiveData(dataResultSuccess(123))
        liveDataA.transformDispatcher(Dispatchers.Main.immediate)

        val liveDataB = ResponseLiveData(dataResultSuccess("String"))
        val liveDataMerge = liveDataA.followedBy { liveDataB }

        Assert.assertEquals(dataResultSuccess(123), liveDataA.value)
        Assert.assertEquals(dataResultSuccess("String"), liveDataB.value)
        Assert.assertNull(liveDataMerge.value)
        liveDataMerge.observe(alwaysOnOwner) { status { /* Do Nothing*/ } }

        advanceUntilIdle()

        Assert.assertEquals(dataResultSuccess(123 to "String"), liveDataMerge.value)
    }

    @Test
    fun `12 - followedBy - one success, other loading`() = runTest {
        val liveDataA = ResponseLiveData(dataResultSuccess(123))
        liveDataA.transformDispatcher(Dispatchers.Main.immediate)

        val liveDataB = ResponseLiveData<String>(dataResultLoading())
        val liveDataMerge = liveDataA.followedBy { liveDataB }

        Assert.assertEquals(dataResultSuccess(123), liveDataA.value)
        Assert.assertEquals(dataResultLoading<String>(), liveDataB.value)
        Assert.assertNull(liveDataMerge.value)
        liveDataMerge.observe(alwaysOnOwner) { status { /* Do Nothing*/ } }

        advanceUntilIdle()

        Assert.assertEquals(dataResultLoading<Pair<Int, String>>(), liveDataMerge.value)
    }
}
