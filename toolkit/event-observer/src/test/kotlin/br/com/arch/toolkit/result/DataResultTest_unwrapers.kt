@file:Suppress("LongMethod")

package br.com.arch.toolkit.result

import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
    private val mockedData: (String) -> Unit = mock()
    private val transformData: (String) -> Int = mock()
    private val mockedTransformedData: (Int) -> Unit = mock()

    // Loading Mocks
    private val mockedLoading: (Boolean) -> Unit = mock()
    private val mockedHideLoading: () -> Unit = mock()
    private val mockedShowLoading: () -> Unit = mock()

    // Error Mocks
    private val mockedError: (Throwable) -> Unit = mock()
    private val mockedErrorWithoutArgument: () -> Unit = mock()
    private val transformError: (Throwable) -> Int = mock()
    private val mockedTransformedError: (Int) -> Unit = mock()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Before
    fun before() {
        whenever(transformData.invoke(any())) doReturn 123
        whenever(transformError.invoke(any())) doReturn 123

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
    fun `A - data A, null, SUCCESS`() = runTest {
        observe(resultA)
        advanceUntilIdle()

        // Data
        verifyBlocking(mockedData) { invoke("data A") }
        verifyBlocking(transformData) { invoke("data A") }
        verifyBlocking(mockedTransformedData) { invoke(123) }

        // Loading
        verifyBlocking(mockedLoading) { invoke(false) }
        verifyBlocking(mockedHideLoading) { invoke() }
        verifyNoInteractions(mockedShowLoading)

        // Error
        verifyNoInteractions(mockedError)
        verifyNoInteractions(mockedErrorWithoutArgument)
        verifyNoInteractions(transformError)
        verifyNoInteractions(mockedTransformedError)
    }

    @Test
    fun `B - null, null, SUCCESS`() = runTest {
        observe(resultB)
        advanceUntilIdle()

        // Data
        verifyNoInteractions(mockedData)
        verifyNoInteractions(transformData)
        verifyNoInteractions(mockedTransformedData)

        // Loading
        verifyBlocking(mockedLoading) { invoke(false) }
        verifyBlocking(mockedHideLoading) { invoke() }
        verifyNoInteractions(mockedShowLoading)

        // Error
        verifyNoInteractions(mockedError)
        verifyNoInteractions(mockedErrorWithoutArgument)
        verifyNoInteractions(transformError)
        verifyNoInteractions(mockedTransformedError)
    }
    //endregion

    //region State: LOADING
    @Test
    fun `C - null, null, LOADING`() = runTest {
        observe(resultC)
        advanceUntilIdle()

        verifyNoInteractions(mockedData)
        verifyNoInteractions(transformData)
        verifyNoInteractions(mockedTransformedData)

        verifyBlocking(mockedLoading) { invoke(true) }
        verifyNoInteractions(mockedHideLoading)
        verifyBlocking(mockedShowLoading) { invoke() }

        // Error
        verifyNoInteractions(mockedError)
        verifyNoInteractions(mockedErrorWithoutArgument)
        verifyNoInteractions(transformError)
        verifyNoInteractions(mockedTransformedError)
    }

    @Test
    fun `D - data D, null, LOADING`() = runTest {
        observe(resultD)
        advanceUntilIdle()

        // Data
        verifyBlocking(mockedData) { invoke("data D") }
        verifyBlocking(transformData) { invoke("data D") }
        verifyBlocking(mockedTransformedData) { invoke(123) }

        // Loading
        verifyBlocking(mockedLoading) { invoke(true) }
        verifyNoInteractions(mockedHideLoading)
        verifyBlocking(mockedShowLoading) { invoke() }

        // Error
        verifyNoInteractions(mockedError)
        verifyNoInteractions(mockedErrorWithoutArgument)
        verifyNoInteractions(transformError)
        verifyNoInteractions(mockedTransformedError)
    }

    @Test
    fun `E - null, error, LOADING`() = runTest {
        observe(resultE)
        advanceUntilIdle()

        // Data
        verifyNoInteractions(mockedData)
        verifyNoInteractions(transformData)
        verifyNoInteractions(mockedTransformedData)

        // Loading
        verifyBlocking(mockedLoading) { invoke(true) }
        verifyNoInteractions(mockedHideLoading)
        verifyBlocking(mockedShowLoading) { invoke() }

        // Error
        verifyNoInteractions(mockedError)
        verifyNoInteractions(mockedErrorWithoutArgument)
        verifyNoInteractions(transformError)
        verifyNoInteractions(mockedTransformedError)
    }
    //endregion

    //region Status: ERROR
    @Test
    fun `F - data F, error, LOADING`() = runTest {
        observe(resultF)
        advanceUntilIdle()

        // Data
        verifyBlocking(mockedData) { invoke("data F") }
        verifyBlocking(transformData) { invoke("data F") }
        verifyBlocking(mockedTransformedData) { invoke(123) }

        // Loading
        verifyBlocking(mockedLoading) { invoke(true) }
        verifyNoInteractions(mockedHideLoading)
        verifyBlocking(mockedShowLoading) { invoke() }

        // Error
        verifyNoInteractions(mockedError)
        verifyNoInteractions(mockedErrorWithoutArgument)
        verifyNoInteractions(transformError)
        verifyNoInteractions(mockedTransformedError)
    }

    @Test
    fun `G - data G, error, ERROR`() = runTest {
        observe(resultG)
        advanceUntilIdle()

        // Data
        verifyBlocking(mockedData) { invoke("data G") }
        verifyBlocking(transformData) { invoke("data G") }
        verifyBlocking(mockedTransformedData) { invoke(123) }

        // Loading
        verifyBlocking(mockedLoading) { invoke(false) }
        verifyBlocking(mockedHideLoading) { invoke() }
        verifyNoInteractions(mockedShowLoading)

        // Error
        verifyBlocking(mockedError) { invoke(error) }
        verifyBlocking(mockedErrorWithoutArgument) { invoke() }
        verifyBlocking(transformError) { invoke(error) }
        verifyBlocking(mockedTransformedError) { invoke(123) }
    }

    @Test
    fun `H - null, error, ERROR`() = runTest {
        observe(resultH)
        advanceUntilIdle()

        // Data
        verifyNoInteractions(mockedData)
        verifyNoInteractions(transformData)
        verifyNoInteractions(mockedTransformedData)

        // Loading
        verifyBlocking(mockedLoading) { invoke(false) }
        verifyBlocking(mockedHideLoading) { invoke() }
        verifyNoInteractions(mockedShowLoading)

        // Error
        verifyBlocking(mockedError) { invoke(error) }
        verifyBlocking(mockedErrorWithoutArgument) { invoke() }
        verifyBlocking(transformError) { invoke(error) }
        verifyBlocking(mockedTransformedError) { invoke(123) }
    }

    @Test
    fun `I - null, null, ERROR`() = runTest {
        observe(resultI)
        advanceUntilIdle()

        // Data
        verifyNoInteractions(mockedData)
        verifyNoInteractions(transformData)
        verifyNoInteractions(mockedTransformedData)

        // Loading
        verifyBlocking(mockedLoading) { invoke(false) }
        verifyBlocking(mockedHideLoading) { invoke() }
        verifyNoInteractions(mockedShowLoading)

        // Error
        verifyNoInteractions(mockedError)
        verifyBlocking(mockedErrorWithoutArgument) { invoke() }
        verifyNoInteractions(transformError)
        verifyNoInteractions(mockedTransformedError)
    }
    //endregion

    //region Status: NONE
    @Test
    fun `J - null, null, NONE`() = runTest {
        observe(resultJ)
        advanceUntilIdle()

        // Data
        verifyNoInteractions(mockedData)
        verifyNoInteractions(transformData)
        verifyNoInteractions(mockedTransformedData)

        // Loading
        verifyNoInteractions(mockedLoading)
        verifyNoInteractions(mockedHideLoading)
        verifyNoInteractions(mockedShowLoading)

        // Error
        verifyNoInteractions(mockedError)
        verifyNoInteractions(mockedErrorWithoutArgument)
        verifyNoInteractions(transformError)
        verifyNoInteractions(mockedTransformedError)
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
