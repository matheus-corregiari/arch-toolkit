package br.com.arch.toolkit.flow

import br.com.arch.toolkit.MainDispatcherRule
import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class, Experimental::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ResponseMutableStateFlowTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @Test
    fun `0 - init without param, should init with none value`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        Assert.assertEquals(dataResultNone<Any>(), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(NONE, flow.status)
    }

    @Test
    fun `0 - init with param, should init with param value`() = runTest {
        val value = DataResult("String", null, SUCCESS)
        val flow = ResponseMutableStateFlow(value)

        Assert.assertEquals(value, flow.value)
        Assert.assertNull(flow.error)
        Assert.assertEquals("String", flow.data)
        Assert.assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `EMIT - null, null, LOADING`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitLoading()

        Assert.assertEquals(dataResultLoading<Any>(), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(LOADING, flow.status)
    }

    @Test
    fun `EMIT - data, null, LOADING`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitLoading("data")

        Assert.assertEquals(dataResultLoading<Any>("data"), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertEquals("data", flow.data)
        Assert.assertEquals(LOADING, flow.status)
    }

    @Test
    fun `EMIT - null, error, ERROR`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        val error = IllegalStateException()
        flow.emitError(error)

        Assert.assertEquals(dataResultError<Any>(error), flow.value)
        Assert.assertEquals(error, flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(ERROR, flow.status)
    }

    @Test
    fun `EMIT - data, error, ERROR`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        val error = IllegalStateException()
        flow.emitError(error, "data")

        Assert.assertEquals(dataResultError<Any>(error, "data"), flow.value)
        Assert.assertEquals(error, flow.error)
        Assert.assertEquals("data", flow.data)
        Assert.assertEquals(ERROR, flow.status)
    }

    @Test
    fun `EMIT - data, null, SUCCESS`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitData("data")

        Assert.assertEquals(dataResultSuccess<Any>("data"), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertEquals("data", flow.data)
        Assert.assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `EMIT - null, null, SUCCESS`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitSuccess()

        Assert.assertEquals(dataResultSuccess<Any>(null), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `EMIT - null, null, NONE`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emitNone()

        Assert.assertEquals(dataResultNone<Any>(), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(NONE, flow.status)
    }

    @Test
    fun `EMIT - DataResult`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.emit(dataResultNone())

        Assert.assertEquals(dataResultNone<Any>(), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(NONE, flow.status)
    }

    @Test
    fun `TRY EMIT - null, null, LOADING`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitLoading()

        Assert.assertEquals(dataResultLoading<Any>(), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(LOADING, flow.status)
    }

    @Test
    fun `TRY EMIT - data, null, LOADING`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitLoading("data")

        Assert.assertEquals(dataResultLoading<Any>("data"), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertEquals("data", flow.data)
        Assert.assertEquals(LOADING, flow.status)
    }

    @Test
    fun `TRY EMIT - null, error, ERROR`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        val error = IllegalStateException()
        flow.tryEmitError(error)

        Assert.assertEquals(dataResultError<Any>(error), flow.value)
        Assert.assertEquals(error, flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(ERROR, flow.status)
    }

    @Test
    fun `TRY EMIT - data, error, ERROR`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        val error = IllegalStateException()
        flow.tryEmitError(error, "data")

        Assert.assertEquals(dataResultError<Any>(error, "data"), flow.value)
        Assert.assertEquals(error, flow.error)
        Assert.assertEquals("data", flow.data)
        Assert.assertEquals(ERROR, flow.status)
    }

    @Test
    fun `TRY EMIT - data, null, SUCCESS`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitData("data")

        Assert.assertEquals(dataResultSuccess<Any>("data"), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertEquals("data", flow.data)
        Assert.assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `TRY EMIT - null, null, SUCCESS`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitSuccess()

        Assert.assertEquals(dataResultSuccess<Any>(null), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(SUCCESS, flow.status)
    }

    @Test
    fun `TRY EMIT - null, null, NONE`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmitNone()

        Assert.assertEquals(dataResultNone<Any>(), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(NONE, flow.status)
    }

    @Test
    fun `TRY EMIT - DataResult`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.tryEmit(dataResultNone())

        Assert.assertEquals(dataResultNone<Any>(), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(NONE, flow.status)
    }

    @Test
    fun `SET - DataResult`() = runTest {
        val flow = ResponseMutableStateFlow<Any>()
        flow.value = dataResultNone()

        Assert.assertEquals(dataResultNone<Any>(), flow.value)
        Assert.assertNull(flow.error)
        Assert.assertNull(flow.data)
        Assert.assertEquals(NONE, flow.status)
    }
}
