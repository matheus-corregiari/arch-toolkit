@file:Suppress("LongMethod")

package br.com.arch.toolkit.result

import br.com.arch.toolkit.exception.DataResultException
import br.com.arch.toolkit.exception.DataResultTransformationException
import br.com.arch.toolkit.util.dataResultError
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
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Yes I do hate the person who did the coroutines stopping treating exceptions inside the scope of the test
 * without giving me any other option to do that
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Ignore("Validar funcionamento e depois pensar em como testar essa bagaça")
class ObserveWrapperTest_ThrownError {

    private val error = IllegalStateException("Thrown Error!")
    private val expected = DataResultException(
        message = "Any error event found, please add one error { ... } to retry",
        error = error
    )
    private val expectedTransformation = DataResultTransformationException(
        message = "Error performing transformation",
        error = error
    )
    private val expectedError = DataResultException(
        message = "Error retried but without any success",
        error = error
    )

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    //region Data Error Thrown Scenarios
    @Test
    fun `ERROR Data - Without ERROR`() = runTest {
        val resultWithData = dataResultSuccess("data")
        val mockedBlock: (String) -> Unit = mockk()

        // Prepare Mock
        every { mockedBlock.invoke("data") } throws error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                data(observer = mockedBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verify(exactly = 1) { mockedBlock.invoke("data") }

        // Assert Error Type!
        assertEquals(expected, errorFound)
    }

    @Test
    fun `ERROR Data - With ERROR`() = runTest {
        val resultWithData = dataResultSuccess("data")
        val mockedBlock: (String) -> Unit = mockk()
        val mockedErrorBlock: (Throwable) -> Unit = mockk()

        // Prepare Mock
        every { mockedBlock.invoke("data") } throws error
        every { mockedErrorBlock.invoke(any()) } throws error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                data(observer = mockedBlock)
                error(observer = mockedErrorBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verify(exactly = 1) { mockedBlock.invoke("data") }
        verify(exactly = 1) { mockedErrorBlock.invoke(any()) }

        // Assert Error Type!
        assertEquals(expectedError, errorFound)
    }

    @Test
    fun `ERROR Data Transform - Without ERROR`() = runTest {
        val resultWithData = dataResultSuccess("data")
        val mockedBlock: (Int) -> Unit = mockk()
        val mockedTransformer: (String) -> Int = mockk()

        // Prepare Mock
        every { mockedBlock.invoke(any()) } returns Unit
        every { mockedTransformer.invoke("data") } throws error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                transformDispatcher(Dispatchers.Main.immediate)
                data(transformer = mockedTransformer, observer = mockedBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verify(exactly = 0) { mockedBlock.invoke(any()) }
        verify(exactly = 1) { mockedTransformer.invoke("data") }

        // Assert Error Type!
        assertEquals(expectedTransformation, errorFound)
    }

    @Test
    fun `ERROR Data Transform - With ERROR`() = runTest {
        val resultWithData = dataResultSuccess("data")
        val mockedBlock: (Int) -> Unit = mockk()
        val mockedTransformer: (String) -> Int = mockk()
        val mockedErrorBlock: (Throwable) -> Unit = mockk()

        // Prepare Mock
        every { mockedBlock.invoke(any()) } returns Unit
        every { mockedTransformer.invoke("data") } throws error
        every { mockedErrorBlock.invoke(any()) } throws error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                transformDispatcher(Dispatchers.Main.immediate)
                data(transformer = mockedTransformer, observer = mockedBlock)
                error(observer = mockedErrorBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verify(exactly = 0) { mockedBlock.invoke(any()) }
        verify(exactly = 1) { mockedTransformer.invoke("data") }
        verify(exactly = 0) { mockedErrorBlock.invoke(any()) }

        // Assert Error Type!
        assertEquals(expectedTransformation, errorFound)
    }
    //endregion

    //region Error Thrown Scenarios
    @Test
    fun `ERROR Error - Should retry one time`() = runTest {
        val resultWithData = dataResultError(error, "data")
        val mockedBlock: (Throwable) -> Unit = mockk()

        // Prepare Mock
        every { mockedBlock.invoke(any()) } throws error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                error(observer = mockedBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verify(exactly = 2) { mockedBlock.invoke(any()) }

        // Assert Error Type!
        assertEquals(expectedError, errorFound)
    }

    @Test
    fun `ERROR Error Transform - Should retry one time`() = runTest {
        val resultWithData = dataResultError(error, "data")
        val mockedBlock: (Int) -> Unit = mockk()
        val mockedTransformer: (Throwable) -> Int = mockk()

        // Prepare Mock
        every { mockedBlock.invoke(any()) } returns Unit
        every { mockedTransformer.invoke(any()) } throws error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                transformDispatcher(Dispatchers.Main.immediate)
                error(transformer = mockedTransformer, observer = mockedBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verify(exactly = 0) { mockedBlock.invoke(any()) }
        verify(exactly = 1) { mockedTransformer.invoke(any()) }

        // Assert Error Type!
        assertEquals(expectedTransformation, errorFound)
    }
    //endregion
}
