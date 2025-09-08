@file:Suppress("LongMethod")

package br.com.arch.toolkit.result

import io.mockk.mockk
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveWrapperTest_statusWithTransformer {

    private val error = IllegalStateException("Error!")
    private val data = "data"
    private val mockedStatus: (Int) -> Unit = mockk()
    private val mockedTransformer: (DataResultStatus) -> Int = mockk()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @BeforeTest
    fun init() {
        every { mockedStatus.invoke(123) } returns Unit
        every { mockedTransformer.invoke(any()) } returns 123
    }

    //region SUCCESS
    @Test
    fun `001 - single=false - null - null - SUCCESS`() = test(
        result = DataResult(data = null, error = null, status = SUCCESS),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `001 - single=true - null - null - SUCCESS`() = test(
        result = DataResult(data = null, error = null, status = SUCCESS),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=false - data - null - SUCCESS`() = test(
        result = DataResult(data = data, error = null, status = SUCCESS),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `002 - single=true - data - null - SUCCESS`() = test(
        result = DataResult(data = data, error = null, status = SUCCESS),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=false - null - error - SUCCESS`() = test(
        result = DataResult(data = null, error = error, status = SUCCESS),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `003 - single=true - null - error - SUCCESS`() = test(
        result = DataResult(data = null, error = error, status = SUCCESS),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=false - data - error - SUCCESS`() = test(
        result = DataResult(data = data, error = error, status = SUCCESS),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `004 - single=true - data - error - SUCCESS`() = test(
        result = DataResult(data = data, error = error, status = SUCCESS),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region LOADING
    @Test
    fun `005 - single=false - null - null - LOADING`() = test(
        result = DataResult(data = null, error = null, status = LOADING),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `005 - single=true - null - null - LOADING`() = test(
        result = DataResult(data = null, error = null, status = LOADING),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=false - data - null - LOADING`() = test(
        result = DataResult(data = data, error = null, status = LOADING),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `006 - single=true - data - null - LOADING`() = test(
        result = DataResult(data = data, error = null, status = LOADING),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=false - null - error - LOADING`() = test(
        result = DataResult(data = null, error = error, status = LOADING),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `007 - single=true - null - error - LOADING`() = test(
        result = DataResult(data = null, error = error, status = LOADING),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=false - data - error - LOADING`() = test(
        result = DataResult(data = data, error = error, status = LOADING),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `008 - single=true - data - error - LOADING`() = test(
        result = DataResult(data = data, error = error, status = LOADING),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region ERROR
    @Test
    fun `009 - single=false - null - null - ERROR`() = test(
        result = DataResult(data = null, error = null, status = ERROR),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `009 - single=true - null - null - ERROR`() = test(
        result = DataResult(data = null, error = null, status = ERROR),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=false - data - null - ERROR`() = test(
        result = DataResult(data = data, error = null, status = ERROR),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `010 - single=true - data - null - ERROR`() = test(
        result = DataResult(data = data, error = null, status = ERROR),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=false - null - error - ERROR`() = test(
        result = DataResult(data = null, error = error, status = ERROR),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `011 - single=true - null - error - ERROR`() = test(
        result = DataResult(data = null, error = error, status = ERROR),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=false - data - error - ERROR`() = test(
        result = DataResult(data = data, error = error, status = ERROR),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `012 - single=true - data - error - ERROR`() = test(
        result = DataResult(data = data, error = error, status = ERROR),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    //region NONE
    @Test
    fun `013 - single=false - null - null - NONE`() = test(
        result = DataResult(data = null, error = null, status = NONE),
        single = false
    ) { wrapper, _ ->
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedStatus.invoke(any()) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `013 - single=true - null - null - NONE`() = test(
        result = DataResult(data = null, error = null, status = NONE),
        single = true
    ) { wrapper, _ ->
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedStatus.invoke(any()) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=false - data - null - NONE`() = test(
        result = DataResult(data = data, error = null, status = NONE),
        single = false
    ) { wrapper, _ ->
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedStatus.invoke(any()) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `014 - single=true - data - null - NONE`() = test(
        result = DataResult(data = data, error = null, status = NONE),
        single = true
    ) { wrapper, _ ->
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedStatus.invoke(any()) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=false - null - error - NONE`() = test(
        result = DataResult(data = null, error = error, status = NONE),
        single = false
    ) { wrapper, _ ->
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedStatus.invoke(any()) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `015 - single=true - null - error - NONE`() = test(
        result = DataResult(data = null, error = error, status = NONE),
        single = true
    ) { wrapper, _ ->
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedStatus.invoke(any()) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=false - data - error - NONE`() = test(
        result = DataResult(data = data, error = error, status = NONE),
        single = false
    ) { wrapper, _ ->
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedStatus.invoke(any()) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `016 - single=true - data - error - NONE`() = test(
        result = DataResult(data = data, error = error, status = NONE),
        single = true
    ) { wrapper, _ ->
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedStatus.invoke(any()) }
        assertEquals(1, wrapper.eventList.size)
    }
    //endregion

    //region Empty
    @Test
    fun `017 - single=false - empty - null - SUCCESS`() = test(
        result = DataResult(data = emptyList<String>(), error = null, status = SUCCESS),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `017 - single=true - empty - null - SUCCESS`() = test(
        result = DataResult(data = emptyList<String>(), error = null, status = SUCCESS),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=false - notEmpty - null - SUCCESS`() = test(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        single = false
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(1, wrapper.eventList.size)
    }

    @Test
    fun `018 - single=true - notEmpty - null - SUCCESS`() = test(
        result = DataResult(data = listOf(data), error = null, status = SUCCESS),
        single = true
    ) { wrapper, status ->
        verify(exactly = 1) { mockedTransformer.invoke(status) }
        verify(exactly = 1) { mockedStatus.invoke(123) }
        assertEquals(0, wrapper.eventList.size)
    }
    //endregion

    private inline fun <T> test(
        result: DataResult<T>,
        single: Boolean,
        crossinline block: suspend (ObserveWrapper<T>, DataResultStatus) -> Unit
    ) = runTest {
        val wrapper = ObserveWrapper<T>()
        wrapper.transformDispatcher(Dispatchers.Main.immediate)
        if (single) {
            wrapper.status(single = true, transformer = mockedTransformer, observer = mockedStatus)
        } else {
            wrapper.status(transformer = mockedTransformer, observer = mockedStatus)
        }
        verify(exactly = 0) { mockedTransformer.invoke(any()) }
        verify(exactly = 0) { mockedStatus.invoke(any()) }
        assertEquals(1, wrapper.eventList.size)

        wrapper.attachTo(result)
        advanceUntilIdle()
        block.invoke(wrapper, result.status)
    }
}
