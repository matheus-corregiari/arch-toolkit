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
import org.junit.Assert
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
class DataResultTest {

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

    // List
    private val resultListEmpty = dataResultSuccess(listOf<String>())
    private val resultMapEmpty = dataResultSuccess(mapOf<String, String>())
    private val resultSequenceEmpty = dataResultSuccess(sequenceOf<String>())
    private val resultListNotEmpty = dataResultSuccess(listOf("a"))
    private val resultMapNotEmpty = dataResultSuccess(mapOf("a" to "a"))
    private val resultSequenceNotEmpty = dataResultSuccess(sequenceOf("a"))

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

    @Test
    fun `0 - Custom Getters and setters`() {
        // hasData
        Assert.assertTrue(resultA.hasData)
        Assert.assertFalse(resultB.hasData)
        Assert.assertFalse(resultC.hasData)
        Assert.assertTrue(resultD.hasData)
        Assert.assertFalse(resultE.hasData)
        Assert.assertTrue(resultF.hasData)
        Assert.assertTrue(resultG.hasData)
        Assert.assertFalse(resultH.hasData)
        Assert.assertFalse(resultI.hasData)
        Assert.assertFalse(resultJ.hasData)
        Assert.assertTrue(resultListEmpty.hasData)
        Assert.assertTrue(resultMapEmpty.hasData)
        Assert.assertTrue(resultSequenceEmpty.hasData)
        Assert.assertTrue(resultListNotEmpty.hasData)
        Assert.assertTrue(resultMapNotEmpty.hasData)
        Assert.assertTrue(resultSequenceNotEmpty.hasData)

        // hasError
        Assert.assertFalse(resultA.hasError)
        Assert.assertFalse(resultB.hasError)
        Assert.assertFalse(resultC.hasError)
        Assert.assertFalse(resultD.hasError)
        Assert.assertTrue(resultE.hasError)
        Assert.assertTrue(resultF.hasError)
        Assert.assertTrue(resultG.hasError)
        Assert.assertTrue(resultH.hasError)
        Assert.assertFalse(resultI.hasError)
        Assert.assertFalse(resultJ.hasError)
        Assert.assertFalse(resultListEmpty.hasError)
        Assert.assertFalse(resultMapEmpty.hasError)
        Assert.assertFalse(resultSequenceEmpty.hasError)
        Assert.assertFalse(resultListNotEmpty.hasError)
        Assert.assertFalse(resultMapNotEmpty.hasError)
        Assert.assertFalse(resultSequenceNotEmpty.hasError)

        // isSuccess
        Assert.assertTrue(resultA.isSuccess)
        Assert.assertTrue(resultB.isSuccess)
        Assert.assertFalse(resultC.isSuccess)
        Assert.assertFalse(resultD.isSuccess)
        Assert.assertFalse(resultE.isSuccess)
        Assert.assertFalse(resultF.isSuccess)
        Assert.assertFalse(resultG.isSuccess)
        Assert.assertFalse(resultH.isSuccess)
        Assert.assertFalse(resultI.isSuccess)
        Assert.assertFalse(resultJ.isSuccess)
        Assert.assertTrue(resultListEmpty.isSuccess)
        Assert.assertTrue(resultMapEmpty.isSuccess)
        Assert.assertTrue(resultSequenceEmpty.isSuccess)
        Assert.assertTrue(resultListNotEmpty.isSuccess)
        Assert.assertTrue(resultMapNotEmpty.isSuccess)
        Assert.assertTrue(resultSequenceNotEmpty.isSuccess)

        // isLoading
        Assert.assertFalse(resultA.isLoading)
        Assert.assertFalse(resultB.isLoading)
        Assert.assertTrue(resultC.isLoading)
        Assert.assertTrue(resultD.isLoading)
        Assert.assertTrue(resultE.isLoading)
        Assert.assertTrue(resultF.isLoading)
        Assert.assertFalse(resultG.isLoading)
        Assert.assertFalse(resultH.isLoading)
        Assert.assertFalse(resultI.isLoading)
        Assert.assertFalse(resultJ.isLoading)
        Assert.assertFalse(resultListEmpty.isLoading)
        Assert.assertFalse(resultMapEmpty.isLoading)
        Assert.assertFalse(resultSequenceEmpty.isLoading)
        Assert.assertFalse(resultListNotEmpty.isLoading)
        Assert.assertFalse(resultMapNotEmpty.isLoading)
        Assert.assertFalse(resultSequenceNotEmpty.isLoading)

        // isError
        Assert.assertFalse(resultA.isError)
        Assert.assertFalse(resultB.isError)
        Assert.assertFalse(resultC.isError)
        Assert.assertFalse(resultD.isError)
        Assert.assertFalse(resultE.isError)
        Assert.assertFalse(resultF.isError)
        Assert.assertTrue(resultG.isError)
        Assert.assertTrue(resultH.isError)
        Assert.assertTrue(resultI.isError)
        Assert.assertFalse(resultJ.isError)
        Assert.assertFalse(resultListEmpty.isError)
        Assert.assertFalse(resultMapEmpty.isError)
        Assert.assertFalse(resultSequenceEmpty.isError)
        Assert.assertFalse(resultListNotEmpty.isError)
        Assert.assertFalse(resultMapNotEmpty.isError)
        Assert.assertFalse(resultSequenceNotEmpty.isError)

        // isNone
        Assert.assertFalse(resultA.isNone)
        Assert.assertFalse(resultB.isNone)
        Assert.assertFalse(resultC.isNone)
        Assert.assertFalse(resultD.isNone)
        Assert.assertFalse(resultE.isNone)
        Assert.assertFalse(resultF.isNone)
        Assert.assertFalse(resultG.isNone)
        Assert.assertFalse(resultH.isNone)
        Assert.assertFalse(resultI.isNone)
        Assert.assertTrue(resultJ.isNone)
        Assert.assertFalse(resultListEmpty.isNone)
        Assert.assertFalse(resultMapEmpty.isNone)
        Assert.assertFalse(resultSequenceEmpty.isNone)
        Assert.assertFalse(resultListNotEmpty.isNone)
        Assert.assertFalse(resultMapNotEmpty.isNone)
        Assert.assertFalse(resultSequenceNotEmpty.isNone)

        // isListType
        Assert.assertFalse(resultA.isListType)
        Assert.assertFalse(resultB.isListType)
        Assert.assertFalse(resultC.isListType)
        Assert.assertFalse(resultD.isListType)
        Assert.assertFalse(resultE.isListType)
        Assert.assertFalse(resultF.isListType)
        Assert.assertFalse(resultG.isListType)
        Assert.assertFalse(resultH.isListType)
        Assert.assertFalse(resultI.isListType)
        Assert.assertFalse(resultJ.isListType)
        Assert.assertTrue(resultListEmpty.isListType)
        Assert.assertTrue(resultMapEmpty.isListType)
        Assert.assertTrue(resultSequenceEmpty.isListType)
        Assert.assertTrue(resultListNotEmpty.isListType)
        Assert.assertTrue(resultMapNotEmpty.isListType)
        Assert.assertTrue(resultSequenceNotEmpty.isListType)

        // isEmpty
        Assert.assertFalse(resultA.isEmpty)
        Assert.assertFalse(resultB.isEmpty)
        Assert.assertFalse(resultC.isEmpty)
        Assert.assertFalse(resultD.isEmpty)
        Assert.assertFalse(resultE.isEmpty)
        Assert.assertFalse(resultF.isEmpty)
        Assert.assertFalse(resultG.isEmpty)
        Assert.assertFalse(resultH.isEmpty)
        Assert.assertFalse(resultI.isEmpty)
        Assert.assertFalse(resultJ.isEmpty)
        Assert.assertTrue(resultListEmpty.isEmpty)
        Assert.assertTrue(resultMapEmpty.isEmpty)
        Assert.assertTrue(resultSequenceEmpty.isEmpty)
        Assert.assertFalse(resultListNotEmpty.isEmpty)
        Assert.assertFalse(resultMapNotEmpty.isEmpty)
        Assert.assertFalse(resultSequenceNotEmpty.isEmpty)

        // isEmpty
        Assert.assertFalse(resultA.isNotEmpty)
        Assert.assertFalse(resultB.isNotEmpty)
        Assert.assertFalse(resultC.isNotEmpty)
        Assert.assertFalse(resultD.isNotEmpty)
        Assert.assertFalse(resultE.isNotEmpty)
        Assert.assertFalse(resultF.isNotEmpty)
        Assert.assertFalse(resultG.isNotEmpty)
        Assert.assertFalse(resultH.isNotEmpty)
        Assert.assertFalse(resultI.isNotEmpty)
        Assert.assertFalse(resultJ.isNotEmpty)
        Assert.assertFalse(resultListEmpty.isNotEmpty)
        Assert.assertFalse(resultMapEmpty.isNotEmpty)
        Assert.assertFalse(resultSequenceEmpty.isNotEmpty)
        Assert.assertTrue(resultListNotEmpty.isNotEmpty)
        Assert.assertTrue(resultMapNotEmpty.isNotEmpty)
        Assert.assertTrue(resultSequenceNotEmpty.isNotEmpty)
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
