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
class ObserveWrapperTest_data {

    private val error = IllegalStateException("Error!")
    private val data = "data"
    private val mockedData: (String) -> Unit = mock()
    private val mockedListData: (List<String>) -> Unit = mock()

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        Class.forName("kotlinx.coroutines.test.TestScopeKt")
            .getDeclaredMethod("setCatchNonTestRelatedExceptions", Boolean::class.java)
            .invoke(null, true)
    }

    @Before
    fun init() {
        whenever(mockedData.invoke(data)) doReturn Unit
        whenever(mockedListData.invoke(emptyList())) doReturn Unit
        whenever(mockedListData.invoke(listOf(data))) doReturn Unit
    }

    //region SUCCESS
    @Test
    fun `001 - single=false - null, null, SUCCESS`() = test(
        result = DataResult(data = null, error = null, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `001 - single=true - null, null, SUCCESS`() = test(
        result = DataResult(data = null, error = null, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=false - data, null, SUCCESS`() = test(
        result = DataResult(data = data, error = null, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=true - data, null, SUCCESS`() = test(
        result = DataResult(data = data, error = null, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=false - null, error, SUCCESS`() = test(
        result = DataResult(data = null, error = error, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=true - null, error, SUCCESS`() = test(
        result = DataResult(data = null, error = error, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=false - data, error, SUCCESS`() = test(
        result = DataResult(data = data, error = error, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=true - data, error, SUCCESS`() = test(
        result = DataResult(data = data, error = error, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region LOADING
    @Test
    fun `005 - single=false - null, null, LOADING`() = test(
        result = DataResult(data = null, error = null, status = LOADING),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `005 - single=true - null, null, LOADING`() = test(
        result = DataResult(data = null, error = null, status = LOADING),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=false - data, null, LOADING`() = test(
        result = DataResult(data = data, error = null, status = LOADING),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=true - data, null, LOADING`() = test(
        result = DataResult(data = data, error = null, status = LOADING),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=false - null, error, LOADING`() = test(
        result = DataResult(data = null, error = error, status = LOADING),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=true - null, error, LOADING`() = test(
        result = DataResult(data = null, error = error, status = LOADING),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=false - data, error, LOADING`() = test(
        result = DataResult(data = data, error = error, status = LOADING),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=true - data, error, LOADING`() = test(
        result = DataResult(data = data, error = error, status = LOADING),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region ERROR
    @Test
    fun `009 - single=false - null, null, ERROR`() = test(
        result = DataResult(data = null, error = null, status = ERROR),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `009 - single=true - null, null, ERROR`() = test(
        result = DataResult(data = null, error = null, status = ERROR),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=false - data, null, ERROR`() = test(
        result = DataResult(data = data, error = null, status = ERROR),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=true - data, null, ERROR`() = test(
        result = DataResult(data = data, error = null, status = ERROR),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=false - null, error, ERROR`() = test(
        result = DataResult(data = null, error = error, status = ERROR),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=true - null, error, ERROR`() = test(
        result = DataResult(data = null, error = error, status = ERROR),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=false - data, error, ERROR`() = test(
        result = DataResult(data = data, error = error, status = ERROR),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=true - data, error, ERROR`() = test(
        result = DataResult(data = data, error = error, status = ERROR),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedData) { invoke(data) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region NONE
    @Test
    fun `013 - single=false - null, null, NONE`() = test(
        result = DataResult(data = null, error = null, status = NONE),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `013 - single=true - null, null, NONE`() = test(
        result = DataResult(data = null, error = null, status = NONE),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=false - data, null, NONE`() = test(
        result = DataResult(data = data, error = null, status = NONE),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=true - data, null, NONE`() = test(
        result = DataResult(data = data, error = null, status = NONE),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=false - null, error, NONE`() = test(
        result = DataResult(data = null, error = error, status = NONE),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=true - null, error, NONE`() = test(
        result = DataResult(data = null, error = error, status = NONE),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=false - data, error, NONE`() = test(
        result = DataResult(data = data, error = error, status = NONE),
        single = false
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=true - data, error, NONE`() = test(
        result = DataResult(data = data, error = error, status = NONE),
        single = true
    ) { wrapper ->
        verifyNoInteractions(mockedData)
        assertEquals(1, wrapper.eventList.size)
    }
    //endregion

    //region Empty
    @Test
    fun `017 - single=false - empty, null, SUCCESS`() = testList(
        result = DataResult(data = emptyList(), error = null, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedListData) { invoke(emptyList()) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `017 - single=true - empty, null, SUCCESS`() = testList(
        result = DataResult(data = emptyList(), error = null, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedListData) { invoke(emptyList()) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=false - notEmpty, null, SUCCESS`() = testList(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        single = false
    ) { wrapper ->
        verifyBlocking(mockedListData) { invoke(listOf(data)) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=true - notEmpty, null, SUCCESS`() = testList(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        single = true
    ) { wrapper ->
        verifyBlocking(mockedListData) { invoke(listOf(data)) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    private inline fun test(
        result: DataResult<String>,
        single: Boolean,
        crossinline block: suspend (wrapper: ObserveWrapper<String>) -> Unit
    ) = runTest {
        val wrapper = ObserveWrapper<String>()
        if (single) {
            wrapper.data(single = true, observer = mockedData)
        } else {
            wrapper.data(observer = mockedData)
        }
        verifyNoInteractions(mockedData)
        verifyNoInteractions(mockedListData)
        assertEquals(1, wrapper.eventList.size)

        wrapper.attachTo(result)
        advanceUntilIdle()
        block.invoke(wrapper)
    }

    private inline fun testList(
        result: DataResult<List<String>>,
        single: Boolean,
        crossinline block: suspend (wrapper: ObserveWrapper<List<String>>) -> Unit
    ) = runTest {
        val wrapper = ObserveWrapper<List<String>>()
        if (single) {
            wrapper.data(single = true, observer = mockedListData)
        } else {
            wrapper.data(observer = mockedListData)
        }
        verifyNoInteractions(mockedData)
        verifyNoInteractions(mockedListData)
        assertEquals(1, wrapper.eventList.size)

        wrapper.attachTo(result)
        advanceUntilIdle()
        block.invoke(wrapper)
    }
}
