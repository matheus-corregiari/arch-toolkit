package br.com.arch.toolkit.flow

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResponseMutableStateFlowTest {

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `0 - init without param - should init with none value`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        assertEquals(dataResultNone<Any>(), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(NONE, flow.status)
    }

    @Test
    fun `0 - init with param - should init with param value`() = runTest {
        val value = DataResult("String", null, SUCCESS)
        val flow = ResponseMutableStateFlow(value)

        assertEquals(value, flow.value)
        assertNull(flow.error)
        assertEquals("String", flow.data)
        assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `EMIT - null - null - LOADING`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitLoading()

        assertEquals(dataResultLoading<Any>(), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(LOADING, flow.status)
    }

    @Test
    fun `EMIT - data - null - LOADING`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitLoading("data")

        assertEquals(dataResultLoading<Any>("data"), flow.value)
        assertNull(flow.error)
        assertEquals("data", flow.data)
        assertEquals(LOADING, flow.status)
    }

    @Test
    fun `EMIT - null - error - ERROR`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        val error = IllegalStateException()
        flow.emitError(error)

        assertEquals(dataResultError<Any>(error), flow.value)
        assertEquals(error, flow.error)
        assertNull(flow.data)
        assertEquals(ERROR, flow.status)
    }

    @Test
    fun `EMIT - data - error - ERROR`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        val error = IllegalStateException()
        flow.emitError(error, "data")

        assertEquals(dataResultError<Any>(error, "data"), flow.value)
        assertEquals(error, flow.error)
        assertEquals("data", flow.data)
        assertEquals(ERROR, flow.status)
    }

    @Test
    fun `EMIT - data - null - SUCCESS`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitData("data")

        assertEquals(dataResultSuccess<Any>("data"), flow.value)
        assertNull(flow.error)
        assertEquals("data", flow.data)
        assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `EMIT - null - null - SUCCESS`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitSuccess()

        assertEquals(dataResultSuccess<Any>(null), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `EMIT - null - null - NONE`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitNone()

        assertEquals(dataResultNone<Any>(), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(NONE, flow.status)
    }

    @Test
    fun `EMIT - DataResult`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emit(dataResultNone())

        assertEquals(dataResultNone<Any>(), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(NONE, flow.status)
    }

    @Test
    fun `TRY EMIT - null - null - LOADING`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitLoading()

        assertEquals(dataResultLoading<Any>(), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(LOADING, flow.status)
    }

    @Test
    fun `TRY EMIT - data - null - LOADING`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitLoading("data")

        assertEquals(dataResultLoading<Any>("data"), flow.value)
        assertNull(flow.error)
        assertEquals("data", flow.data)
        assertEquals(LOADING, flow.status)
    }

    @Test
    fun `TRY EMIT - null - error - ERROR`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        val error = IllegalStateException()
        flow.tryEmitError(error)

        assertEquals(dataResultError<Any>(error), flow.value)
        assertEquals(error, flow.error)
        assertNull(flow.data)
        assertEquals(ERROR, flow.status)
    }

    @Test
    fun `TRY EMIT - data - error - ERROR`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        val error = IllegalStateException()
        flow.tryEmitError(error, "data")

        assertEquals(dataResultError<Any>(error, "data"), flow.value)
        assertEquals(error, flow.error)
        assertEquals("data", flow.data)
        assertEquals(ERROR, flow.status)
    }

    @Test
    fun `TRY EMIT - data - null - SUCCESS`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitData("data")

        assertEquals(dataResultSuccess<Any>("data"), flow.value)
        assertNull(flow.error)
        assertEquals("data", flow.data)
        assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `TRY EMIT - null - null - SUCCESS`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitSuccess()

        assertEquals(dataResultSuccess<Any>(null), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `TRY EMIT - null - null - NONE`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitNone()

        assertEquals(dataResultNone<Any>(), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(NONE, flow.status)
    }

    @Test
    fun `TRY EMIT - DataResult`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmit(dataResultNone())

        assertEquals(dataResultNone<Any>(), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(NONE, flow.status)
    }

    @Test
    fun `SET - DataResult`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.value = dataResultNone()

        assertEquals(dataResultNone<Any>(), flow.value)
        assertNull(flow.error)
        assertNull(flow.data)
        assertEquals(NONE, flow.status)
    }
}
