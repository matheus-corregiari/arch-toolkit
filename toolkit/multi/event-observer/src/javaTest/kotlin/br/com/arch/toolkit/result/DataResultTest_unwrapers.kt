@file:Suppress("LongMethod")

package br.com.arch.toolkit.result

import io.mockk.mockk
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DataResultTest_unwrapers {

    private val error = IllegalStateException("error")

    // Success
    private val resultA = dataResultSuccess("data A")
    private val resultB = dataResultSuccess<String>(null)

    // Loading
    private val resultC = dataResultLoading<String>()
    private val resultD = dataResultLoading("data D")
    private val resultE = dataResultLoading<String>(null, error)
    private val resultF = dataResultLoading("data F", error)

    // Error
    private val resultG = dataResultError(error, "data G")
    private val resultH = dataResultError<String>(error)
    private val resultI = dataResultError<String>(null)

    // None
    private val resultJ = dataResultNone<String>()

    // Data Mocks
    private val mockedData: (String) -> Unit = mockk(relaxed = true)
    private val transformData: (String) -> Int = mockk()
    private val mockedTransformedData: (Int) -> Unit = mockk(relaxed = true)

    // Loading Mocks
    private val mockedLoading: (Boolean) -> Unit = mockk(relaxed = true)
    private val mockedHideLoading: () -> Unit = mockk(relaxed = true)
    private val mockedShowLoading: () -> Unit = mockk(relaxed = true)

    // Error Mocks
    private val mockedError: (Throwable) -> Unit = mockk(relaxed = true)
    private val mockedErrorWithoutArgument: () -> Unit = mockk(relaxed = true)
    private val transformError: (Throwable) -> Int = mockk()
    private val mockedTransformedError: (Int) -> Unit = mockk(relaxed = true)

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @BeforeTest
    fun before() {
        every { transformData.invoke(any()) } returns 123
        every { transformError.invoke(any()) } returns 123

        resultA.scope(CoroutineScope(Dispatchers.Main))
        resultA.transformDispatcher(Dispatchers.Main)
        resultB.transformDispatcher(Dispatchers.Main)
        resultC.transformDispatcher(Dispatchers.Main)
        resultD.transformDispatcher(Dispatchers.Main)
        resultE.transformDispatcher(Dispatchers.Main)
        resultF.transformDispatcher(Dispatchers.Main)
        resultG.transformDispatcher(Dispatchers.Main)
        resultH.transformDispatcher(Dispatchers.Main)
        resultI.transformDispatcher(Dispatchers.Main)
        resultJ.transformDispatcher(Dispatchers.Main)
    }

    // region State: SUCCESS
    @Test
    fun `A - data A - null - SUCCESS`() = runTest {
        observe(resultA)
        advanceUntilIdle()

        // Data
        verify(exactly = 1) { mockedData.invoke("data A") }
        verify(exactly = 1) { transformData.invoke("data A") }
        verify(exactly = 1) { mockedTransformedData.invoke(123) }

        // Loading
        verify(exactly = 1) { mockedLoading.invoke(false) }
        verify(exactly = 1) { mockedHideLoading.invoke() }
        verify(exactly = 0) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 0) { mockedError.invoke(any()) }
        verify(exactly = 0) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 0) { transformError.invoke(any()) }
        verify(exactly = 0) { mockedTransformedError.invoke(any()) }
    }

    @Test
    fun `B - null - null - SUCCESS`() = runTest {
        observe(resultB)
        advanceUntilIdle()

        // Data
        verify(exactly = 0) { mockedData.invoke(any()) }
        verify(exactly = 0) { transformData.invoke(any()) }
        verify(exactly = 0) { mockedTransformedData.invoke(any()) }

        // Loading
        verify(exactly = 1) { mockedLoading.invoke(false) }
        verify(exactly = 1) { mockedHideLoading.invoke() }
        verify(exactly = 0) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 0) { mockedError.invoke(any()) }
        verify(exactly = 0) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 0) { transformError.invoke(any()) }
        verify(exactly = 0) { mockedTransformedError.invoke(any()) }
    }
    //endregion

    //region State: LOADING
    @Test
    fun `C - null - null - LOADING`() = runTest {
        observe(resultC)
        advanceUntilIdle()

        verify(exactly = 0) { mockedData.invoke(any()) }
        verify(exactly = 0) { transformData.invoke(any()) }
        verify(exactly = 0) { mockedTransformedData.invoke(any()) }

        verify(exactly = 1) { mockedLoading.invoke(true) }
        verify(exactly = 0) { mockedHideLoading.invoke() }
        verify(exactly = 1) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 0) { mockedError.invoke(any()) }
        verify(exactly = 0) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 0) { transformError.invoke(any()) }
        verify(exactly = 0) { mockedTransformedError.invoke(any()) }
    }

    @Test
    fun `D - data D - null - LOADING`() = runTest {
        observe(resultD)
        advanceUntilIdle()

        // Data
        verify(exactly = 1) { mockedData.invoke("data D") }
        verify(exactly = 1) { transformData.invoke("data D") }
        verify(exactly = 1) { mockedTransformedData.invoke(123) }

        // Loading
        verify(exactly = 1) { mockedLoading.invoke(true) }
        verify(exactly = 0) { mockedHideLoading.invoke() }
        verify(exactly = 1) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 0) { mockedError.invoke(any()) }
        verify(exactly = 0) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 0) { transformError.invoke(any()) }
        verify(exactly = 0) { mockedTransformedError.invoke(any()) }
    }

    @Test
    fun `E - null - error - LOADING`() = runTest {
        observe(resultE)
        advanceUntilIdle()

        // Data
        verify(exactly = 0) { mockedData.invoke(any()) }
        verify(exactly = 0) { transformData.invoke(any()) }
        verify(exactly = 0) { mockedTransformedData.invoke(any()) }

        // Loading
        verify(exactly = 1) { mockedLoading.invoke(true) }
        verify(exactly = 0) { mockedHideLoading.invoke() }
        verify(exactly = 1) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 0) { mockedError.invoke(any()) }
        verify(exactly = 0) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 0) { transformError.invoke(any()) }
        verify(exactly = 0) { mockedTransformedError.invoke(any()) }
    }
    //endregion

    //region Status: ERROR
    @Test
    fun `F - data F - error - LOADING`() = runTest {
        observe(resultF)
        advanceUntilIdle()

        // Data
        verify(exactly = 1) { mockedData.invoke("data F") }
        verify(exactly = 1) { transformData.invoke("data F") }
        verify(exactly = 1) { mockedTransformedData.invoke(123) }

        // Loading
        verify(exactly = 1) { mockedLoading.invoke(true) }
        verify(exactly = 0) { mockedHideLoading.invoke() }
        verify(exactly = 1) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 0) { mockedError.invoke(any()) }
        verify(exactly = 0) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 0) { transformError.invoke(any()) }
        verify(exactly = 0) { mockedTransformedError.invoke(any()) }
    }

    @Test
    fun `G - data G - error - ERROR`() = runTest {
        observe(resultG)
        advanceUntilIdle()

        // Data
        verify(exactly = 1) { mockedData.invoke("data G") }
        verify(exactly = 1) { transformData.invoke("data G") }
        verify(exactly = 1) { mockedTransformedData.invoke(123) }

        // Loading
        verify(exactly = 1) { mockedLoading.invoke(false) }
        verify(exactly = 1) { mockedHideLoading.invoke() }
        verify(exactly = 0) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 1) { mockedError.invoke(error) }
        verify(exactly = 1) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 1) { transformError.invoke(error) }
        verify(exactly = 1) { mockedTransformedError.invoke(123) }
    }

    @Test
    fun `H - null - error - ERROR`() = runTest {
        observe(resultH)
        advanceUntilIdle()

        // Data
        verify(exactly = 0) { mockedData.invoke(any()) }
        verify(exactly = 0) { transformData.invoke(any()) }
        verify(exactly = 0) { mockedTransformedData.invoke(any()) }

        // Loading
        verify(exactly = 1) { mockedLoading.invoke(false) }
        verify(exactly = 1) { mockedHideLoading.invoke() }
        verify(exactly = 0) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 1) { mockedError.invoke(error) }
        verify(exactly = 1) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 1) { transformError.invoke(error) }
        verify(exactly = 1) { mockedTransformedError.invoke(123) }
    }

    @Test
    fun `I - null - null - ERROR`() = runTest {
        observe(resultI)
        advanceUntilIdle()

        // Data
        verify(exactly = 0) { mockedData.invoke(any()) }
        verify(exactly = 0) { transformData.invoke(any()) }
        verify(exactly = 0) { mockedTransformedData.invoke(any()) }

        // Loading
        verify(exactly = 1) { mockedLoading.invoke(false) }
        verify(exactly = 1) { mockedHideLoading.invoke() }
        verify(exactly = 0) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 0) { mockedError.invoke(any()) }
        verify(exactly = 1) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 0) { transformError.invoke(any()) }
        verify(exactly = 0) { mockedTransformedError.invoke(any()) }
    }
    //endregion

    //region Status: NONE
    @Test
    fun `J - null - null - NONE`() = runTest {
        observe(resultJ)
        advanceUntilIdle()

        // Data
        verify(exactly = 0) { mockedData.invoke(any()) }
        verify(exactly = 0) { transformData.invoke(any()) }
        verify(exactly = 0) { mockedTransformedData.invoke(any()) }

        // Loading
        verify(exactly = 0) { mockedLoading.invoke(any()) }
        verify(exactly = 0) { mockedHideLoading.invoke() }
        verify(exactly = 0) { mockedShowLoading.invoke() }

        // Error
        verify(exactly = 0) { mockedError.invoke(any()) }
        verify(exactly = 0) { mockedErrorWithoutArgument.invoke() }
        verify(exactly = 0) { transformError.invoke(any()) }
        verify(exactly = 0) { mockedTransformedError.invoke(any()) }
    }
    //endregion

    private fun observe(result: DataResult<String>) {
        // Data
        result.data(mockedData)
        result.data(transformData, mockedTransformedData)

        // Loading
        result.loading(mockedLoading)
        result.hideLoading(mockedHideLoading)
        result.showLoading(mockedShowLoading)

        // Error
        result.error(mockedError)
        result.error(mockedErrorWithoutArgument)
        result.error(transformError, mockedTransformedError)
    }
}
