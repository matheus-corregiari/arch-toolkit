@file:Suppress("LongMethod")

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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ObserveWrapperTest_errorWithThrowable {

    private val error = IllegalStateException("Error!")
    private val data = "data"
    private val mockedError: (Throwable) -> Unit = mock()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Before
    fun init() {
        whenever(mockedError.invoke(error)) doReturn Unit
    }

    //region SUCCESS
    @Test
    fun `001 - single=false - null, null, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = null, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `001 - single=true - null, null, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = null, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `001 - single=false - withData=false - null, null, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = null, status = SUCCESS),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `001 - single=true - withData=false - null, null, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = null, status = SUCCESS),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `001 - single=false - withData=true - null, null, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = null, status = SUCCESS),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `001 - single=true - withData=true - null, null, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = null, status = SUCCESS),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=false - data, null, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = null, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=true - data, null, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = null, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=false - withData=false - data, null, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = null, status = SUCCESS),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=true - withData=false - data, null, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = null, status = SUCCESS),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=false - withData=true - data, null, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = null, status = SUCCESS),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=true - withData=true - data, null, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = null, status = SUCCESS),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=false - null, error, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = error, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=true - null, error, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = error, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=false - withData=false - null, error, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = error, status = SUCCESS),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=true - withData=false - null, error, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = error, status = SUCCESS),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=false - withData=true - null, error, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = error, status = SUCCESS),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=true - withData=true - null, error, SUCCESS`() = test(
        result = DataResult<Any>(data = null, error = error, status = SUCCESS),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=false - data, error, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = error, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=true - data, error, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = error, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=false - withData=false - data, error, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = error, status = SUCCESS),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=true - withData=false - data, error, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = error, status = SUCCESS),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=false - withData=true - data, error, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = error, status = SUCCESS),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=true - withData=true - data, error, SUCCESS`() = test(
        result = DataResult<Any>(data = data, error = error, status = SUCCESS),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }
    //endregion

    //region LOADING
    @Test
    fun `005 - single=false - null, null, LOADING`() = test(
        result = DataResult<Any>(data = null, error = null, status = LOADING),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `005 - single=true - null, null, LOADING`() = test(
        result = DataResult<Any>(data = null, error = null, status = LOADING),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `005 - single=false - withData=false - null, null, LOADING`() = test(
        result = DataResult<Any>(data = null, error = null, status = LOADING),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `005 - single=true - withData=false - null, null, LOADING`() = test(
        result = DataResult<Any>(data = null, error = null, status = LOADING),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `005 - single=false - withData=true - null, null, LOADING`() = test(
        result = DataResult<Any>(data = null, error = null, status = LOADING),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `005 - single=true - withData=true - null, null, LOADING`() = test(
        result = DataResult<Any>(data = null, error = null, status = LOADING),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=false - data, null, LOADING`() = test(
        result = DataResult<Any>(data = data, error = null, status = LOADING),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=true - data, null, LOADING`() = test(
        result = DataResult<Any>(data = data, error = null, status = LOADING),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=false - withData=false - data, null, LOADING`() = test(
        result = DataResult<Any>(data = data, error = null, status = LOADING),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=true - withData=false - data, null, LOADING`() = test(
        result = DataResult<Any>(data = data, error = null, status = LOADING),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=false - withData=true - data, null, LOADING`() = test(
        result = DataResult<Any>(data = data, error = null, status = LOADING),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=true - withData=true - data, null, LOADING`() = test(
        result = DataResult<Any>(data = data, error = null, status = LOADING),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=false - null, error, LOADING`() = test(
        result = DataResult<Any>(data = null, error = error, status = LOADING),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=true - null, error, LOADING`() = test(
        result = DataResult<Any>(data = null, error = error, status = LOADING),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=false - withData=false - null, error, LOADING`() = test(
        result = DataResult<Any>(data = null, error = error, status = LOADING),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=true - withData=false - null, error, LOADING`() = test(
        result = DataResult<Any>(data = null, error = error, status = LOADING),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=false - withData=true - null, error, LOADING`() = test(
        result = DataResult<Any>(data = null, error = error, status = LOADING),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=true - withData=true - null, error, LOADING`() = test(
        result = DataResult<Any>(data = null, error = error, status = LOADING),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=false - data, error, LOADING`() = test(
        result = DataResult<Any>(data = data, error = error, status = LOADING),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=true - data, error, LOADING`() = test(
        result = DataResult<Any>(data = data, error = error, status = LOADING),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=false - withData=false - data, error, LOADING`() = test(
        result = DataResult<Any>(data = data, error = error, status = LOADING),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=true - withData=false - data, error, LOADING`() = test(
        result = DataResult<Any>(data = data, error = error, status = LOADING),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=false - withData=true - data, error, LOADING`() = test(
        result = DataResult<Any>(data = data, error = error, status = LOADING),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=true - withData=true - data, error, LOADING`() = test(
        result = DataResult<Any>(data = data, error = error, status = LOADING),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }
    //endregion

    //region ERROR
    @Test
    fun `009 - single=false - null, null, ERROR`() = test(
        result = DataResult<Any>(data = null, error = null, status = ERROR),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `009 - single=true - null, null, ERROR`() = test(
        result = DataResult<Any>(data = null, error = null, status = ERROR),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `009 - single=false - withData=false - null, null, ERROR`() = test(
        result = DataResult<Any>(data = null, error = null, status = ERROR),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `009 - single=true - withData=false - null, null, ERROR`() = test(
        result = DataResult<Any>(data = null, error = null, status = ERROR),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `009 - single=false - withData=true - null, null, ERROR`() = test(
        result = DataResult<Any>(data = null, error = null, status = ERROR),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `009 - single=true - withData=true - null, null, ERROR`() = test(
        result = DataResult<Any>(data = null, error = null, status = ERROR),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=false - data, null, ERROR`() = test(
        result = DataResult<Any>(data = data, error = null, status = ERROR),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=true - data, null, ERROR`() = test(
        result = DataResult<Any>(data = data, error = null, status = ERROR),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=false - withData=false - data, null, ERROR`() = test(
        result = DataResult<Any>(data = data, error = null, status = ERROR),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=true - withData=false - data, null, ERROR`() = test(
        result = DataResult<Any>(data = data, error = null, status = ERROR),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=false - withData=true - data, null, ERROR`() = test(
        result = DataResult<Any>(data = data, error = null, status = ERROR),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=true - withData=true - data, null, ERROR`() = test(
        result = DataResult<Any>(data = data, error = null, status = ERROR),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=false - null, error, ERROR`() = test(
        result = DataResult<Any>(data = null, error = error, status = ERROR),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedError) { invoke(error) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=true - null, error, ERROR`() = test(
        result = DataResult<Any>(data = null, error = error, status = ERROR),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedError) { invoke(error) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=false - withData=false - null, error, ERROR`() = test(
        result = DataResult<Any>(data = null, error = error, status = ERROR),
        withData = false,
        single = false
    ) { wrapper ->
        verifyBlocking(mockedError) { invoke(error) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=true - withData=false - null, error, ERROR`() = test(
        result = DataResult<Any>(data = null, error = error, status = ERROR),
        withData = false,
        single = true
    ) { wrapper ->
        verifyBlocking(mockedError) { invoke(error) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=false - withData=true - null, error, ERROR`() = test(
        result = DataResult<Any>(data = null, error = error, status = ERROR),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=true - withData=true - null, error, ERROR`() = test(
        result = DataResult<Any>(data = null, error = error, status = ERROR),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=false - data, error, ERROR`() = test(
        result = DataResult<Any>(data = data, error = error, status = ERROR),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedError) { invoke(error) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=true - data, error, ERROR`() = test(
        result = DataResult<Any>(data = data, error = error, status = ERROR),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedError) { invoke(error) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=false - withData=false - data, error, ERROR`() = test(
        result = DataResult<Any>(data = data, error = error, status = ERROR),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=true - withData=false - data, error, ERROR`() = test(
        result = DataResult<Any>(data = data, error = error, status = ERROR),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=false - withData=true - data, error, ERROR`() = test(
        result = DataResult<Any>(data = data, error = error, status = ERROR),
        withData = true,
        single = false
    ) { wrapper ->
        verifyBlocking(mockedError) { invoke(error) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=true - withData=true - data, error, ERROR`() = test(
        result = DataResult<Any>(data = data, error = error, status = ERROR),
        withData = true,
        single = true
    ) { wrapper ->
        verifyBlocking(mockedError) { invoke(error) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region NONE
    @Test
    fun `013 - single=false - null, null, NONE`() = test(
        result = DataResult<Any>(data = null, error = null, status = NONE),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `013 - single=true - null, null, NONE`() = test(
        result = DataResult<Any>(data = null, error = null, status = NONE),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `013 - single=false - withData=false - null, null, NONE`() = test(
        result = DataResult<Any>(data = null, error = null, status = NONE),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `013 - single=true - withData=false - null, null, NONE`() = test(
        result = DataResult<Any>(data = null, error = null, status = NONE),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `013 - single=false - withData=true - null, null, NONE`() = test(
        result = DataResult<Any>(data = null, error = null, status = NONE),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `013 - single=true - withData=true - null, null, NONE`() = test(
        result = DataResult<Any>(data = null, error = null, status = NONE),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=false - data, null, NONE`() = test(
        result = DataResult<Any>(data = data, error = null, status = NONE),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=true - data, null, NONE`() = test(
        result = DataResult<Any>(data = data, error = null, status = NONE),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=false - withData=false - data, null, NONE`() = test(
        result = DataResult<Any>(data = data, error = null, status = NONE),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=true - withData=false - data, null, NONE`() = test(
        result = DataResult<Any>(data = data, error = null, status = NONE),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=false - withData=true - data, null, NONE`() = test(
        result = DataResult<Any>(data = data, error = null, status = NONE),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=true - withData=true - data, null, NONE`() = test(
        result = DataResult<Any>(data = data, error = null, status = NONE),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=false - null, error, NONE`() = test(
        result = DataResult<Any>(data = null, error = error, status = NONE),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=true - null, error, NONE`() = test(
        result = DataResult<Any>(data = null, error = error, status = NONE),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=false - withData=false - null, error, NONE`() = test(
        result = DataResult<Any>(data = null, error = error, status = NONE),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=true - withData=false - null, error, NONE`() = test(
        result = DataResult<Any>(data = null, error = error, status = NONE),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=false - withData=true - null, error, NONE`() = test(
        result = DataResult<Any>(data = null, error = error, status = NONE),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=true - withData=true - null, error, NONE`() = test(
        result = DataResult<Any>(data = null, error = error, status = NONE),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=false - data, error, NONE`() = test(
        result = DataResult<Any>(data = data, error = error, status = NONE),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=true - data, error, NONE`() = test(
        result = DataResult<Any>(data = data, error = error, status = NONE),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=false - withData=false - data, error, NONE`() = test(
        result = DataResult<Any>(data = data, error = error, status = NONE),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=true - withData=false - data, error, NONE`() = test(
        result = DataResult<Any>(data = data, error = error, status = NONE),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=false - withData=true - data, error, NONE`() = test(
        result = DataResult<Any>(data = data, error = error, status = NONE),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=true - withData=true - data, error, NONE`() = test(
        result = DataResult<Any>(data = data, error = error, status = NONE),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }
    //endregion

    //region Empty
    @Test
    fun `017 - single=false - empty, null, SUCCESS`() = test(
        result = DataResult<List<String>>(data = emptyList(), error = null, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `017 - single=true - empty, null, SUCCESS`() = test(
        result = DataResult<List<String>>(data = emptyList(), error = null, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `017 - single=false - withData=false - empty, null, SUCCESS`() = test(
        result = DataResult<List<String>>(data = emptyList(), error = null, status = SUCCESS),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `017 - single=true - withData=false - empty, null, SUCCESS`() = test(
        result = DataResult<List<String>>(data = emptyList(), error = null, status = SUCCESS),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `017 - single=false - withData=true - empty, null, SUCCESS`() = test(
        result = DataResult<List<String>>(data = emptyList(), error = null, status = SUCCESS),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `017 - single=true - withData=true - empty, null, SUCCESS`() = test(
        result = DataResult<List<String>>(data = emptyList(), error = null, status = SUCCESS),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=false - notEmpty, null, SUCCESS`() = test(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=true - notEmpty, null, SUCCESS`() = test(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=false - withData=false - notEmpty, null, SUCCESS`() = test(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        withData = false,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=true - withData=false - notEmpty, null, SUCCESS`() = test(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        withData = false,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=false - withData=true - notEmpty, null, SUCCESS`() = test(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        withData = true,
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=true - withData=true - notEmpty, null, SUCCESS`() = test(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        withData = true,
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)
    }
    //endregion

    private inline fun <T> test(
        result: DataResult<T>,
        single: Boolean,
        crossinline block: suspend (wrapper: ObserveWrapper<T>) -> Unit
    ) = runTest {
        val wrapper = ObserveWrapper<T>()
        if (single) {
            wrapper.error(single = true, observer = mockedError)
        } else {
            wrapper.error(observer = mockedError)
        }
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)

        wrapper.attachTo(result)
        advanceUntilIdle()
        block.invoke(wrapper)
    }

    private inline fun <T> test(
        result: DataResult<T>,
        single: Boolean,
        withData: Boolean,
        crossinline block: suspend (wrapper: ObserveWrapper<T>) -> Unit
    ) = runTest {
        val wrapper = ObserveWrapper<T>()
        wrapper.error(
            single = single,
            dataStatus = if (withData) EventDataStatus.WithData else EventDataStatus.WithoutData,
            observer = mockedError
        )
        verifyNoInteractions(mockedError)
        assertEquals(1, wrapper.eventList.size)

        wrapper.attachTo(result)
        advanceUntilIdle()
        block.invoke(wrapper)
    }
}
