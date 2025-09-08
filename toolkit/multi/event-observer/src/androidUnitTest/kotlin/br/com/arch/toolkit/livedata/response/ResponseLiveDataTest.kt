package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultSuccess
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ResponseLiveDataTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `00 - init without param, should init with null value`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.scope(this)
        liveData.transformDispatcher(Dispatchers.Main.immediate)

        assertNull(liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertNull(liveData.status)
    }

    @Test
    fun `01 - init with param, should init with param value`() = runTest {
        val value = DataResult("String", null, SUCCESS)
        val liveData = MutableResponseLiveData(value)

        assertEquals(value, liveData.value)
        assertNull(liveData.error)
        assertEquals("String", liveData.data)
        assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `02 - Should keep observer until all events has been handled`() = runTest {
        fun singleTrue() {
            val liveData = MutableResponseLiveData<Any>()
            liveData.transformDispatcher(Dispatchers.Main.immediate)
            liveData.observe(alwaysOnOwner) {
                status(single = true) { /* @see ObserverWrapper Tests */ }
            }
            assertTrue(liveData.hasObservers())
            liveData.setValue(dataResultLoading())
            advanceUntilIdle()
            assertFalse(liveData.hasObservers())
        }

        fun singleFalse() {
            val liveData = MutableResponseLiveData<Any>()
            liveData.scope(this)
            liveData.transformDispatcher(Dispatchers.Main.immediate)
            liveData.observe(alwaysOnOwner) {
                status(single = false) { /* @see ObserverWrapper Tests */ }
            }
            assertTrue(liveData.hasObservers())
            liveData.setValue(dataResultLoading())
            advanceUntilIdle()
            assertTrue(liveData.hasObservers())
        }

        singleTrue()
        singleFalse()
    }

    @Test
    fun `03 - transform`() = runTest {
        val mockTransform: (DataResult<Int>) -> DataResult<String> = mockk()

        every { mockTransform.invoke(any()) } returns dataResultSuccess("String")
        every { mockTransform.invoke(any()) } returns dataResultSuccess("String")

        val liveData = MutableResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val transformedLiveData = liveData.transform(mockTransform)

        assertNull(liveData.value)
        assertNull(transformedLiveData.value)

        // Will change the value only after the observe
        liveData.value = dataResultSuccess(123)
        advanceUntilIdle()
        assertEquals(dataResultSuccess(123), liveData.value)
        assertNull(transformedLiveData.value)
        verify(exactly = 0) { mockTransform.invoke(any()) }

        // Now the transformation will be triggered
        transformedLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        assertEquals(dataResultSuccess("String"), transformedLiveData.value)
        verify(exactly = 1) { mockTransform.invoke(dataResultSuccess(123)) }
    }

    @Test
    fun `04 - onError`() = runTest {
        val error = IllegalStateException("error")
        val mockOnError: (Throwable) -> Unit = mockk(relaxed = true)
        val liveData = MutableResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.onError(mockOnError)

        assertNull(liveData.value)
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockOnError.invoke(any()) }

        // Will change the value only after the observe
        liveData.setValue(dataResultError(error))
        advanceUntilIdle()
        assertEquals(
            dataResultError<Int>(error),
            liveData.value
        )
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockOnError.invoke(any()) }

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        assertEquals(
            dataResultError<Int>(error),
            onErrorLiveData.value
        )
        verify(exactly = 1) { mockOnError.invoke(error) }
    }

    @Test
    fun `05 - onErrorReturn`() = runTest {
        val error = IllegalStateException("error")
        val mockOnErrorReturn: (Throwable) -> Int = mockk()
        every { mockOnErrorReturn.invoke(error) } returns 123
        val liveData = MutableResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.onErrorReturn(mockOnErrorReturn)

        assertNull(liveData.value)
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockOnErrorReturn.invoke(any()) }

        // Will change the value only after the observe
        liveData.setValue(dataResultError(error))
        advanceUntilIdle()
        assertEquals(
            dataResultError<Int>(error),
            liveData.value
        )
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockOnErrorReturn.invoke(any()) }

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        assertEquals(
            DataResult(123, error, SUCCESS),
            onErrorLiveData.value
        )
        verify(exactly = 1) { mockOnErrorReturn.invoke(error) }
    }

    @Test
    fun `06 - mapError`() = runTest {
        val error = IllegalStateException("error")
        val error2 = IllegalStateException("error2")
        val mockMapError: (Throwable) -> Throwable = mockk()
        every { mockMapError.invoke(error) } returns error2
        val liveData = MutableResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.mapError(mockMapError)

        assertNull(liveData.value)
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockMapError.invoke(any()) }

        // Will change the value only after the observe
        liveData.setValue(dataResultError(error))
        advanceUntilIdle()
        assertEquals(
            dataResultError<Int>(error),
            liveData.value
        )
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockMapError.invoke(any()) }

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        assertEquals(
            dataResultError<Int>(error2),
            onErrorLiveData.value
        )
        verify(exactly = 1) { mockMapError.invoke(error) }
    }

    @Test
    fun `07 - map`() = runTest {
        val mockMap: (Int) -> String = mockk()
        every { mockMap.invoke(123) } returns "String"
        val liveData = MutableResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.map(mockMap)

        assertNull(liveData.value)
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockMap.invoke(any()) }

        // Will change the value only after the observe
        liveData.setValue(dataResultSuccess(123))
        advanceUntilIdle()
        assertEquals(
            dataResultSuccess(123),
            liveData.value
        )
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockMap.invoke(any()) }

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        assertEquals(
            dataResultSuccess("String"),
            onErrorLiveData.value
        )
        verify(exactly = 1) { mockMap.invoke(123) }
    }

    @Test
    fun `08 - onNext`() = runTest {
        val mockOnNext: (Int) -> Unit = mockk(relaxed = true)
        val liveData = MutableResponseLiveData<Int>()
        liveData.transformDispatcher(Dispatchers.Main.immediate)
        val onErrorLiveData = liveData.onNext(mockOnNext)

        assertNull(liveData.value)
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockOnNext.invoke(any()) }

        // Will change the value only after the observe
        liveData.setValue(dataResultSuccess(123))
        advanceUntilIdle()
        assertEquals(
            dataResultSuccess(123),
            liveData.value
        )
        assertNull(onErrorLiveData.value)
        verify(exactly = 0) { mockOnNext.invoke(any()) }

        // Now the transformation will be triggered
        onErrorLiveData.observe(alwaysOnOwner) { status { /* Nothing */ } }
        advanceUntilIdle()
        assertEquals(
            dataResultSuccess(123),
            onErrorLiveData.value
        )
        verify(exactly = 1) { mockOnNext.invoke(123) }
    }

}
