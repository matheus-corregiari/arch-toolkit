@file:Suppress("LongMethod", "ClassNaming", "ClassName")

package br.com.arch.toolkit.result

import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
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
class ObserveWrapperTest_result {

    private val error = IllegalStateException("Error!")
    private val data = "data"
    private val mockedResult: (DataResult<String>) -> Unit = mock()
    private val mockedListResult: (DataResult<List<String>>) -> Unit = mock()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Before
    fun init() {
        whenever(mockedResult.invoke(any())) doReturn Unit
        whenever(mockedListResult.invoke(any())) doReturn Unit
    }

    //region SUCCESS
    @Test
    fun `001 - single=false - null, null, SUCCESS`() = test(
        result = DataResult(data = null, error = null, status = SUCCESS),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `001 - single=true - null, null, SUCCESS`() = test(
        result = DataResult(data = null, error = null, status = SUCCESS),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=false - data, null, SUCCESS`() = test(
        result = DataResult(data = data, error = null, status = SUCCESS),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=true - data, null, SUCCESS`() = test(
        result = DataResult(data = data, error = null, status = SUCCESS),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=false - null, error, SUCCESS`() = test(
        result = DataResult(data = null, error = error, status = SUCCESS),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=true - null, error, SUCCESS`() = test(
        result = DataResult(data = null, error = error, status = SUCCESS),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=false - data, error, SUCCESS`() = test(
        result = DataResult(data = data, error = error, status = SUCCESS),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=true - data, error, SUCCESS`() = test(
        result = DataResult(data = data, error = error, status = SUCCESS),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region LOADING
    @Test
    fun `005 - single=false - null, null, LOADING`() = test(
        result = DataResult(data = null, error = null, status = LOADING),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `005 - single=true - null, null, LOADING`() = test(
        result = DataResult(data = null, error = null, status = LOADING),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=false - data, null, LOADING`() = test(
        result = DataResult(data = data, error = null, status = LOADING),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=true - data, null, LOADING`() = test(
        result = DataResult(data = data, error = null, status = LOADING),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=false - null, error, LOADING`() = test(
        result = DataResult(data = null, error = error, status = LOADING),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=true - null, error, LOADING`() = test(
        result = DataResult(data = null, error = error, status = LOADING),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=false - data, error, LOADING`() = test(
        result = DataResult(data = data, error = error, status = LOADING),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=true - data, error, LOADING`() = test(
        result = DataResult(data = data, error = error, status = LOADING),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region ERROR
    @Test
    fun `009 - single=false - null, null, ERROR`() = test(
        result = DataResult(data = null, error = null, status = ERROR),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `009 - single=true - null, null, ERROR`() = test(
        result = DataResult(data = null, error = null, status = ERROR),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=false - data, null, ERROR`() = test(
        result = DataResult(data = data, error = null, status = ERROR),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=true - data, null, ERROR`() = test(
        result = DataResult(data = data, error = null, status = ERROR),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=false - null, error, ERROR`() = test(
        result = DataResult(data = null, error = error, status = ERROR),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=true - null, error, ERROR`() = test(
        result = DataResult(data = null, error = error, status = ERROR),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=false - data, error, ERROR`() = test(
        result = DataResult(data = data, error = error, status = ERROR),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=true - data, error, ERROR`() = test(
        result = DataResult(data = data, error = error, status = ERROR),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region NONE
    @Test
    fun `013 - single=false - null, null, NONE`() = test(
        result = DataResult(data = null, error = null, status = NONE),
        single = false
    ) { wrapper, _ ->
        verifyNoInteractions(mockedResult)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `013 - single=true - null, null, NONE`() = test(
        result = DataResult(data = null, error = null, status = NONE),
        single = true
    ) { wrapper, _ ->
        verifyNoInteractions(mockedResult)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=false - data, null, NONE`() = test(
        result = DataResult(data = data, error = null, status = NONE),
        single = false
    ) { wrapper, _ ->
        verifyNoInteractions(mockedResult)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=true - data, null, NONE`() = test(
        result = DataResult(data = data, error = null, status = NONE),
        single = true
    ) { wrapper, _ ->
        verifyNoInteractions(mockedResult)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=false - null, error, NONE`() = test(
        result = DataResult(data = null, error = error, status = NONE),
        single = false
    ) { wrapper, _ ->
        verifyNoInteractions(mockedResult)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=true - null, error, NONE`() = test(
        result = DataResult(data = null, error = error, status = NONE),
        single = true
    ) { wrapper, _ ->
        verifyNoInteractions(mockedResult)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=false - data, error, NONE`() = test(
        result = DataResult(data = data, error = error, status = NONE),
        single = false
    ) { wrapper, _ ->
        verifyNoInteractions(mockedResult)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=true - data, error, NONE`() = test(
        result = DataResult(data = data, error = error, status = NONE),
        single = true
    ) { wrapper, _ ->
        verifyNoInteractions(mockedResult)
        assertEquals(1, wrapper.eventList.size)
    }
    //endregion

    //region Empty
    @Test
    fun `017 - single=false - empty, null, SUCCESS`() = testList(
        result = DataResult(data = emptyList(), error = null, status = SUCCESS),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedListResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `017 - single=true - empty, null, SUCCESS`() = testList(
        result = DataResult(data = emptyList(), error = null, status = SUCCESS),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedListResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=false - notEmpty, null, SUCCESS`() = testList(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        single = false
    ) { wrapper, result ->
        verifyBlocking(mockedListResult) { invoke(result) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=true - notEmpty, null, SUCCESS`() = testList(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        single = true
    ) { wrapper, result ->
        verifyBlocking(mockedListResult) { invoke(result) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    private inline fun test(
        result: DataResult<String>,
        single: Boolean,
        crossinline block: suspend (ObserveWrapper<String>, DataResult<String>) -> Unit
    ) = runTest {
        val wrapper = ObserveWrapper<String>()

        if (single) {
            wrapper.result(single = true, observer = mockedResult)
        } else {
            wrapper.result(observer = mockedResult)
        }
        verifyNoInteractions(mockedResult)
        assertEquals(1, wrapper.eventList.size)

        wrapper.attachTo(result)
        advanceUntilIdle()
        block.invoke(wrapper, result)
    }

    private inline fun testList(
        result: DataResult<List<String>>,
        single: Boolean,
        crossinline block: suspend (wrapper: ObserveWrapper<List<String>>, DataResult<List<String>>) -> Unit
    ) = runTest {
        val wrapper = ObserveWrapper<List<String>>()
        if (single) {
            wrapper.result(single = true, observer = mockedListResult)
        } else {
            wrapper.result(observer = mockedListResult)
        }
        verifyNoInteractions(mockedListResult)
        assertEquals(1, wrapper.eventList.size)

        wrapper.attachTo(result)
        advanceUntilIdle()
        block.invoke(wrapper, result)
    }
}
