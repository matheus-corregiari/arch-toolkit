@file:Suppress("LongMethod", "ClassNaming", "ClassName")

package br.com.arch.toolkit.result

import br.com.arch.toolkit.exception.DataResultException
import br.com.arch.toolkit.exception.DataResultTransformationException
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
        Dispatchers.setMain(StandardTestDispatcher())
    }

    //region Data Error Thrown Scenarios
    @Test
    fun `ERROR Data - Without ERROR`() = runTest {
        val resultWithData = dataResultSuccess("data")
        val mockedBlock: (String) -> Unit = mock()

        // Prepare Mock
        whenever(mockedBlock.invoke("data")) doThrow error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                data(observer = mockedBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verifyBlocking(mockedBlock, times(1)) { invoke("data") }

        // Assert Error Type!
        Assert.assertEquals(expected, errorFound)
    }

    @Test
    fun `ERROR Data - With ERROR`() = runTest {
        val resultWithData = dataResultSuccess("data")
        val mockedBlock: (String) -> Unit = mock()
        val mockedErrorBlock: (Throwable) -> Unit = mock()

        // Prepare Mock
        whenever(mockedBlock.invoke("data")) doThrow error
        whenever(mockedErrorBlock.invoke(any())) doThrow error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                data(observer = mockedBlock)
                error(observer = mockedErrorBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verifyBlocking(mockedBlock, times(1)) { invoke("data") }
        verifyBlocking(mockedErrorBlock, times(1)) { invoke(any()) }

        // Assert Error Type!
        Assert.assertEquals(expectedError, errorFound)
    }

    @Test
    fun `ERROR Data Transform - Without ERROR`() = runTest {
        val resultWithData = dataResultSuccess("data")
        val mockedBlock: (Int) -> Unit = mock()
        val mockedTransformer: (String) -> Int = mock()

        // Prepare Mock
        whenever(mockedBlock.invoke(any())) doReturn Unit
        whenever(mockedTransformer.invoke("data")) doThrow error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                transformDispatcher(Dispatchers.Main.immediate)
                data(transformer = mockedTransformer, observer = mockedBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verifyNoInteractions(mockedBlock)
        verifyBlocking(mockedTransformer, times(1)) { invoke("data") }

        // Assert Error Type!
        Assert.assertEquals(expectedTransformation, errorFound)
    }

    @Test
    fun `ERROR Data Transform - With ERROR`() = runTest {
        val resultWithData = dataResultSuccess("data")
        val mockedBlock: (Int) -> Unit = mock()
        val mockedTransformer: (String) -> Int = mock()
        val mockedErrorBlock: (Throwable) -> Unit = mock()

        // Prepare Mock
        whenever(mockedBlock.invoke(any())) doReturn Unit
        whenever(mockedTransformer.invoke("data")) doThrow error
        whenever(mockedErrorBlock.invoke(any())) doThrow error

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
        verifyNoInteractions(mockedBlock)
        verifyBlocking(mockedTransformer, times(1)) { invoke("data") }
        verifyNoInteractions(mockedErrorBlock)

        // Assert Error Type!
        Assert.assertEquals(expectedTransformation, errorFound)
    }
    //endregion

    //region Error Thrown Scenarios
    @Test
    fun `ERROR Error - Should retry one time`() = runTest {
        val resultWithData = dataResultError(error, "data")
        val mockedBlock: (Throwable) -> Unit = mock()

        // Prepare Mock
        whenever(mockedBlock.invoke(any())) doThrow error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                error(observer = mockedBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verifyBlocking(mockedBlock, times(2)) { invoke(any()) }

        // Assert Error Type!
        Assert.assertEquals(expectedError, errorFound)
    }

    @Test
    fun `ERROR Error Transform - Should retry one time`() = runTest {
        val resultWithData = dataResultError(error, "data")
        val mockedBlock: (Int) -> Unit = mock()
        val mockedTransformer: (Throwable) -> Int = mock()

        // Prepare Mock
        whenever(mockedBlock.invoke(any())) doReturn Unit
        whenever(mockedTransformer.invoke(any())) doThrow error

        // Do Evil call
        val errorFound = runCatching {
            resultWithData.unwrap {
                transformDispatcher(Dispatchers.Main.immediate)
                error(transformer = mockedTransformer, observer = mockedBlock)
            }
            advanceUntilIdle()
        }.exceptionOrNull() ?: error("This test must have a error")

        // Tried to call block!
        verifyNoInteractions(mockedBlock)
        verifyBlocking(mockedTransformer, times(1)) { invoke(any()) }

        // Assert Error Type!
        Assert.assertEquals(expectedTransformation, errorFound)
    }
    //endregion
}
