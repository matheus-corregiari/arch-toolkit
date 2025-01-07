package br.com.arch.toolkit.flow

import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.util.dataResultNone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions

@OptIn(ExperimentalCoroutinesApi::class, Experimental::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ResponseFlowTest {

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `0 - init without param, should init with none value`() = runTest {
        val flow = ResponseFlow<Any>()
        Assert.assertEquals(dataResultNone<Any>(), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(NONE, flow.status)
    }

    @Test
    fun `0 - init with param, should init with param value`() = runTest {
        val value = DataResult("String", null, SUCCESS)
        val flow = ResponseFlow(value)

        Assert.assertEquals(value, flow.value)
        Assert.assertNull(flow.error)
        Assert.assertEquals("String", flow.data)
        Assert.assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `01 - Collect with ObserveWrapper`() = runTest {
        val mockObserver: () -> Unit = mock()
        val flow = MutableResponseFlow<Any>()

        flow.collect { showLoading(observer = mockObserver) }
        advanceUntilIdle()
        verifyNoInteractions(mockObserver)

        flow.emitLoading()
        advanceUntilIdle()
        verifyBlocking(mockObserver) { invoke() }
    }

    @Test
    fun `02 - ShareIn should copy a flow to another`() = runTest {
        val mockObserver: () -> Unit = mock()
        val mockShareObserver: () -> Unit = mock()
        val flow = MutableResponseFlow<Any>()
        flow.scope(CoroutineScope(Dispatchers.Main.immediate))
        flow.transformDispatcher(Dispatchers.Main.immediate)

        val shareIn = flow.shareIn(
            scope = CoroutineScope(Dispatchers.Main.immediate),
            started = SharingStarted.WhileSubscribed(),
            replay = 0
        )

        flow.collect { showLoading(observer = mockObserver) }
        shareIn.collect { showLoading(observer = mockShareObserver) }

        advanceUntilIdle()
        verifyNoInteractions(mockObserver)
        verifyNoInteractions(mockShareObserver)

        flow.emitLoading()
        advanceUntilIdle()
        verifyBlocking(mockObserver) { invoke() }
        verifyBlocking(mockShareObserver) { invoke() }
    }
}
