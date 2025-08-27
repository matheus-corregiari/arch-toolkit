package br.com.arch.toolkit.flow

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.util.dataResultNone
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ResponseStateFlowTest {

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `0 - init without param - should init with none value`() = runTest {
        val flow = ResponseStateFlow<Any>()
        flow.scope(CoroutineScope(Dispatchers.Main.immediate))
        flow.transformDispatcher(Dispatchers.Main.immediate)

        assertEquals(dataResultNone<Any>(), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(NONE, flow.status)
    }

    @Test
    fun `0 - init with param - should init with param value`() = runTest {
        val value = DataResult("String", null, SUCCESS)
        val flow = ResponseStateFlow(value)
        flow.scope(CoroutineScope(Dispatchers.Main.immediate))
        flow.transformDispatcher(Dispatchers.Main.immediate)

        assertEquals(value, flow.value)
        assertNull(flow.error)
        assertEquals("String", flow.data)
        assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `01 - Collect with ObserveWrapper`() = runTest {
        val mockObserver: () -> Unit = mockk(relaxed = true)
        val flow = ResponseMutableStateFlow<Any>()
        flow.scope(CoroutineScope(Dispatchers.Main.immediate))
        flow.transformDispatcher(Dispatchers.Main.immediate)

        flow.cold(hotWhile = { it.isNone }).collect { showLoading(observer = mockObserver) }
        advanceUntilIdle()
        verify(exactly = 0) { mockObserver.invoke() }

        flow.emitLoading()
        advanceUntilIdle()
        verify(exactly = 1) { mockObserver.invoke() }
    }

    @Test
    fun `02 - ShareIn should copy a flow to another`() = runTest {
        val mockObserver: () -> Unit = mockk(relaxed = true)
        val mockShareObserver: () -> Unit = mockk(relaxed = true)
        val flow = ResponseMutableStateFlow<Any>()
        flow.scope(CoroutineScope(Dispatchers.Main.immediate))
        flow.transformDispatcher(Dispatchers.Main.immediate)

        val shareIn = flow.shared(replay = 0)

        flow.cold(hotWhile = { it.isNone }).collect { showLoading(observer = mockObserver) }
        shareIn.cold(hotWhile = { it.isNone }).collect { showLoading(observer = mockShareObserver) }

        advanceUntilIdle()
        verify(exactly = 0) { mockObserver.invoke() }
        verify(exactly = 0) { mockShareObserver.invoke() }

        flow.emitLoading()
        advanceUntilIdle()
        verify(exactly = 1) { mockObserver.invoke() }
        verify(exactly = 1) { mockShareObserver.invoke() }
    }
}
