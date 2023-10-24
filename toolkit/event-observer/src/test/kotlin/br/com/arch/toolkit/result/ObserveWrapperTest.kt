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
class ObserveWrapperTest {

    private val error = IllegalStateException("Error!")

    // Mock Data
    private val mockedData: (String) -> Unit = mock()
    private val mockedTransform: (String) -> Int = mock()
    private val mockedTransformData: (Int) -> Unit = mock()

    // Mock Loading
    private val mockedLoading: (Boolean) -> Unit = mock()
    private val mockedShowLoading: () -> Unit = mock()
    private val mockedHideLoading: () -> Unit = mock()

    // Mock Error
    private val mockedError: () -> Unit = mock()
    private val mockedExceptionError: (Throwable) -> Unit = mock()
    private val mockedErrorTransform: (Throwable) -> Int = mock()
    private val mockedTransformError: (Int) -> Unit = mock()

    // Mock Status
    private val mockedStatus: (DataResultStatus) -> Unit = mock()
    private val mockedStatusTransform: (DataResultStatus) -> Int = mock()
    private val mockedTransformStatus: (Int) -> Unit = mock()

    // Mock Result
    private val mockedResult: (DataResult<String>) -> Unit = mock()
    private val mockedResultTransform: (DataResult<String>) -> Int = mock()
    private val mockedTransformResult: (Int) -> Unit = mock()

    // Mock Empty
    private val mockedEmpty: () -> Unit = mock()
    private val mockedNotEmpty: () -> Unit = mock()

    // Mock None
    private val mockedNone: () -> Unit = mock()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Before
    fun init() {
        // Mock Data
        whenever(mockedData.invoke("data")) doReturn Unit
        whenever(mockedTransform.invoke("data")) doReturn 123
        whenever(mockedTransformData.invoke(123)) doReturn Unit

        // Mock Loading
        whenever(mockedLoading.invoke(any())) doReturn Unit
        whenever(mockedShowLoading.invoke()) doReturn Unit
        whenever(mockedHideLoading.invoke()) doReturn Unit

        // Mock Error
        whenever(mockedError.invoke()) doReturn Unit
        whenever(mockedExceptionError.invoke(error)) doReturn Unit
        whenever(mockedErrorTransform.invoke(error)) doReturn 123
        whenever(mockedTransformError.invoke(123)) doReturn Unit

        // Mock Status
        whenever(mockedStatus.invoke(any())) doReturn Unit
        whenever(mockedStatusTransform.invoke(any())) doReturn 123
        whenever(mockedTransformStatus.invoke(123)) doReturn Unit

        // Mock Result
        whenever(mockedResult.invoke(any())) doReturn Unit
        whenever(mockedResultTransform.invoke(any())) doReturn 123
        whenever(mockedTransformResult.invoke(123)) doReturn Unit

        // Mock Empty
        whenever(mockedEmpty.invoke()) doReturn Unit
        whenever(mockedNotEmpty.invoke()) doReturn Unit

        // Mock None
        whenever(mockedNone.invoke()) doReturn Unit
    }

    //region SUCCESS
    @Test
    fun `001-1 - null null, SUCCESS`() =
        runTestWith(DataResult(null, null, SUCCESS))

    @Test
    fun `001-2 - null null, SUCCESS`() =
        runTestWith(DataResult(null, null, SUCCESS), single = true)

    @Test
    fun `001-3 - null null, SUCCESS`() =
        runTestWith(DataResult(null, null, SUCCESS), single = false)

    @Test
    fun `001-4 - null null, SUCCESS`() =
        runTestWith(DataResult(null, null, SUCCESS), withData = true)

    @Test
    fun `001-5 - null null, SUCCESS`() =
        runTestWith(DataResult(null, null, SUCCESS), withData = false)

    @Test
    fun `002-1 - data, null, SUCCESS`() =
        runTestWith(DataResult("data", null, SUCCESS))

    @Test
    fun `002-2 - data, null, SUCCESS`() =
        runTestWith(DataResult("data", null, SUCCESS), single = true)

    @Test
    fun `002-3 - data, null, SUCCESS`() =
        runTestWith(DataResult("data", null, SUCCESS), single = false)

    @Test
    fun `002-4 - data, null, SUCCESS`() =
        runTestWith(DataResult("data", null, SUCCESS), withData = true)

    @Test
    fun `002-5 - data, null, SUCCESS`() =
        runTestWith(DataResult("data", null, SUCCESS), withData = false)

    @Test
    fun `003-1 - null, error, SUCCESS`() =
        runTestWith(DataResult(null, error, SUCCESS))

    @Test
    fun `003-2 - null, error, SUCCESS`() =
        runTestWith(DataResult(null, error, SUCCESS), single = true)

    @Test
    fun `003-3 - null, error, SUCCESS`() =
        runTestWith(DataResult(null, error, SUCCESS), single = false)

    @Test
    fun `003-4 - null, error, SUCCESS`() =
        runTestWith(DataResult(null, error, SUCCESS), withData = true)

    @Test
    fun `003-5 - null, error, SUCCESS`() =
        runTestWith(DataResult(null, error, SUCCESS), withData = false)

    @Test
    fun `004-1 - data, error, SUCCESS`() =
        runTestWith(DataResult("data", error, SUCCESS))

    @Test
    fun `004-2 - data, error, SUCCESS`() =
        runTestWith(DataResult("data", error, SUCCESS), single = true)

    @Test
    fun `004-3 - data, error, SUCCESS`() =
        runTestWith(DataResult("data", error, SUCCESS), single = false)

    @Test
    fun `004-4 - data, error, SUCCESS`() =
        runTestWith(DataResult("data", error, SUCCESS), withData = true)

    @Test
    fun `004-5 - data, error, SUCCESS`() =
        runTestWith(DataResult("data", error, SUCCESS), withData = false)
    //endregion

    //region LOADING
    @Test
    fun `005-1 - null null, LOADING`() =
        runTestWith(DataResult(null, null, LOADING))

    @Test
    fun `005-2 - null null, LOADING`() =
        runTestWith(DataResult(null, null, LOADING), single = true)

    @Test
    fun `005-3 - null null, LOADING`() =
        runTestWith(DataResult(null, null, LOADING), single = false)

    @Test
    fun `005-4 - null null, LOADING`() =
        runTestWith(DataResult(null, null, LOADING), withData = true)

    @Test
    fun `005-5 - null null, LOADING`() =
        runTestWith(DataResult(null, null, LOADING), withData = false)

    @Test
    fun `006-1 - data, null, LOADING`() =
        runTestWith(DataResult("data", null, LOADING))

    @Test
    fun `006-2 - data, null, LOADING`() =
        runTestWith(DataResult("data", null, LOADING), single = true)

    @Test
    fun `006-3 - data, null, LOADING`() =
        runTestWith(DataResult("data", null, LOADING), single = false)

    @Test
    fun `006-4 - data, null, LOADING`() =
        runTestWith(DataResult("data", null, LOADING), withData = true)

    @Test
    fun `006-5 - data, null, LOADING`() =
        runTestWith(DataResult("data", null, LOADING), withData = false)

    @Test
    fun `007-1 - null, error, LOADING`() =
        runTestWith(DataResult(null, error, LOADING))

    @Test
    fun `007-2 - null, error, LOADING`() =
        runTestWith(DataResult(null, error, LOADING), single = true)

    @Test
    fun `007-3 - null, error, LOADING`() =
        runTestWith(DataResult(null, error, LOADING), single = false)

    @Test
    fun `007-4 - null, error, LOADING`() =
        runTestWith(DataResult(null, error, LOADING), withData = true)

    @Test
    fun `007-5 - null, error, LOADING`() =
        runTestWith(DataResult(null, error, LOADING), withData = false)

    @Test
    fun `008-1 - data, error, LOADING`() =
        runTestWith(DataResult("data", error, LOADING))

    @Test
    fun `008-2 - data, error, LOADING`() =
        runTestWith(DataResult("data", error, LOADING), single = true)

    @Test
    fun `008-3 - data, error, LOADING`() =
        runTestWith(DataResult("data", error, LOADING), single = false)

    @Test
    fun `008-4 - data, error, LOADING`() =
        runTestWith(DataResult("data", error, LOADING), withData = true)

    @Test
    fun `008-5 - data, error, LOADING`() =
        runTestWith(DataResult("data", error, LOADING), withData = false)
    //endregion

    //region ERROR
    @Test
    fun `009-1 - null null, ERROR`() =
        runTestWith(DataResult(null, null, ERROR))

    @Test
    fun `009-2 - null null, ERROR`() =
        runTestWith(DataResult(null, null, ERROR), single = true)

    @Test
    fun `009-3 - null null, ERROR`() =
        runTestWith(DataResult(null, null, ERROR), single = false)

    @Test
    fun `009-4 - null null, ERROR`() =
        runTestWith(DataResult(null, null, ERROR), withData = true)

    @Test
    fun `009-5 - null null, ERROR`() =
        runTestWith(DataResult(null, null, ERROR), withData = false)

    @Test
    fun `010-1 - data, null, ERROR`() =
        runTestWith(DataResult("data", null, ERROR))

    @Test
    fun `010-2 - data, null, ERROR`() =
        runTestWith(DataResult("data", null, ERROR), single = true)

    @Test
    fun `010-3 - data, null, ERROR`() =
        runTestWith(DataResult("data", null, ERROR), single = false)

    @Test
    fun `010-4 - data, null, ERROR`() =
        runTestWith(DataResult("data", null, ERROR), withData = true)

    @Test
    fun `010-5 - data, null, ERROR`() =
        runTestWith(DataResult("data", null, ERROR), withData = false)

    @Test
    fun `011-1 - null, error, ERROR`() =
        runTestWith(DataResult(null, error, ERROR))

    @Test
    fun `011-2 - null, error, ERROR`() =
        runTestWith(DataResult(null, error, ERROR), single = true)

    @Test
    fun `011-3 - null, error, ERROR`() =
        runTestWith(DataResult(null, error, ERROR), single = false)

    @Test
    fun `011-4 - null, error, ERROR`() =
        runTestWith(DataResult(null, error, ERROR), withData = true)

    @Test
    fun `011-5 - null, error, ERROR`() =
        runTestWith(DataResult(null, error, ERROR), withData = false)

    @Test
    fun `012-1 - data, error, ERROR`() =
        runTestWith(DataResult("data", error, ERROR))

    @Test
    fun `012-2 - data, error, ERROR`() =
        runTestWith(DataResult("data", error, ERROR), single = true)

    @Test
    fun `012-3 - data, error, ERROR`() =
        runTestWith(DataResult("data", error, ERROR), single = false)

    @Test
    fun `012-4 - data, error, ERROR`() =
        runTestWith(DataResult("data", error, ERROR), withData = true)

    @Test
    fun `012-5 - data, error, ERROR`() =
        runTestWith(DataResult("data", error, ERROR), withData = false)
    //endregion

    //region ERROR
    @Test
    fun `013-1 - null null, NONE`() =
        runTestWith(DataResult(null, null, NONE))

    @Test
    fun `013-2 - null null, NONE`() =
        runTestWith(DataResult(null, null, NONE), single = true)

    @Test
    fun `013-3 - null null, NONE`() =
        runTestWith(DataResult(null, null, NONE), single = false)

    @Test
    fun `013-4 - null null, NONE`() =
        runTestWith(DataResult(null, null, NONE), withData = true)

    @Test
    fun `013-5 - null null, NONE`() =
        runTestWith(DataResult(null, null, NONE), withData = false)

    @Test
    fun `014-1 - data, null, NONE`() =
        runTestWith(DataResult("data", null, NONE))

    @Test
    fun `014-2 - data, null, NONE`() =
        runTestWith(DataResult("data", null, NONE), single = true)

    @Test
    fun `014-3 - data, null, NONE`() =
        runTestWith(DataResult("data", null, NONE), single = false)

    @Test
    fun `014-4 - data, null, NONE`() =
        runTestWith(DataResult("data", null, NONE), withData = true)

    @Test
    fun `014-5 - data, null, NONE`() =
        runTestWith(DataResult("data", null, NONE), withData = false)

    @Test
    fun `015-1 - null, error, NONE`() =
        runTestWith(DataResult(null, error, NONE))

    @Test
    fun `015-2 - null, error, NONE`() =
        runTestWith(DataResult(null, error, NONE), single = true)

    @Test
    fun `015-3 - null, error, NONE`() =
        runTestWith(DataResult(null, error, NONE), single = false)

    @Test
    fun `015-4 - null, error, NONE`() =
        runTestWith(DataResult(null, error, NONE), withData = true)

    @Test
    fun `015-5 - null, error, NONE`() =
        runTestWith(DataResult(null, error, NONE), withData = false)

    @Test
    fun `016-1 - data, error, NONE`() =
        runTestWith(DataResult("data", error, NONE))

    @Test
    fun `016-2 - data, error, NONE`() =
        runTestWith(DataResult("data", error, NONE), single = true)

    @Test
    fun `016-3 - data, error, NONE`() =
        runTestWith(DataResult("data", error, NONE), single = false)

    @Test
    fun `016-4 - data, error, NONE`() =
        runTestWith(DataResult("data", error, NONE), withData = true)

    @Test
    fun `016-5 - data, error, NONE`() =
        runTestWith(DataResult("data", error, NONE), withData = false)
    //endregion

    //region Empty
    @Test
    fun `017-1 - empty, null, SUCCESS`() =
        runTestListWith(DataResult(listOf(), null, SUCCESS))

    @Test
    fun `017-2 - notEmpty, null, SUCCESS`() =
        runTestListWith(DataResult(listOf("item"), null, SUCCESS))
    //endregion

    private fun runTestWith(
        result: DataResult<String>,
        single: Boolean? = null,
        withData: Boolean? = null
    ) = runTest {
        unwrap(result, single, withData)
        advanceUntilIdle()

        verifyData(result.hasData, result.isNone)
        verifyLoading(result.isLoading, result.isNone, result.hasData, withData)
        verifyError(result.isError, result.hasError, result.isNone, result.hasData, withData)
        verifyStatus(result.status, result.isNone)
        verifyResult(result, result.isNone)
        verifyNone(result.isNone)
        verifyEmpty(result.isListType, result.isEmpty)
        verifyNotEmpty(result.isListType, result.isNotEmpty)
    }

    private fun runTestListWith(result: DataResult<List<String>>) = runTest {
        result.unwrap {
            empty(observer = mockedEmpty)
            notEmpty(observer = mockedNotEmpty)
        }
        advanceUntilIdle()
        verifyEmpty(result.isListType, result.isEmpty)
        verifyNotEmpty(result.isListType, result.isNotEmpty)
    }

    private fun unwrap(result: DataResult<String>, single: Boolean?, withData: Boolean?) =
        result.unwrap {
            transformDispatcher(Dispatchers.Main.immediate)
            if (single != null) {
                data(single = single, observer = mockedData)
                data(single = single, transformer = mockedTransform, observer = mockedTransformData)
                loading(single = single, observer = mockedLoading)
                showLoading(single = single, observer = mockedShowLoading)
                hideLoading(single = single, observer = mockedHideLoading)
                error(single = single, observer = mockedError)
                error(single = single, observer = mockedExceptionError)
                error(
                    single = single,
                    transformer = mockedErrorTransform,
                    observer = mockedTransformError
                )
                status(single = single, observer = mockedStatus)
                status(
                    single = single,
                    transformer = mockedStatusTransform,
                    observer = mockedTransformStatus
                )
                result(single = single, observer = mockedResult)
                result(
                    single = single,
                    transformer = mockedResultTransform,
                    observer = mockedTransformResult
                )
                none(single = single, observer = mockedNone)
                empty(single = single, observer = mockedEmpty)
                notEmpty(single = single, observer = mockedNotEmpty)
            } else {
                data(observer = mockedData)
                data(transformer = mockedTransform, observer = mockedTransformData)
                if (withData != null) {
                    loading(withData = withData, observer = mockedLoading)
                    showLoading(withData = withData, observer = mockedShowLoading)
                    hideLoading(withData = withData, observer = mockedHideLoading)
                    error(withData = withData, observer = mockedError)
                    error(withData = withData, observer = mockedExceptionError)
                    error(
                        withData = withData,
                        transformer = mockedErrorTransform,
                        observer = mockedTransformError
                    )
                } else {
                    loading(observer = mockedLoading)
                    showLoading(observer = mockedShowLoading)
                    hideLoading(observer = mockedHideLoading)
                    error(observer = mockedError)
                    error(observer = mockedExceptionError)
                    error(
                        transformer = mockedErrorTransform,
                        observer = mockedTransformError
                    )
                }
                status(observer = mockedStatus)
                status(transformer = mockedStatusTransform, observer = mockedTransformStatus)
                result(observer = mockedResult)
                result(transformer = mockedResultTransform, observer = mockedTransformResult)
                none(observer = mockedNone)
                empty(observer = mockedEmpty)
                notEmpty(observer = mockedNotEmpty)
            }
        }

    private fun verifyData(hasData: Boolean, isNone: Boolean) {
        if (hasData && isNone.not()) {
            verifyBlocking(mockedData) { invoke("data") }
            verifyBlocking(mockedTransform) { invoke("data") }
            verifyBlocking(mockedTransformData) { invoke(123) }
        } else {
            verifyNoInteractions(mockedData)
            verifyNoInteractions(mockedTransform)
            verifyNoInteractions(mockedTransformData)
        }
    }

    private fun verifyLoading(
        hasLoading: Boolean,
        isNone: Boolean,
        hasData: Boolean,
        withData: Boolean?
    ) {
        if (isNone || (withData != null && (hasData != withData))) {
            verifyNoInteractions(mockedLoading)
            verifyNoInteractions(mockedShowLoading)
            verifyNoInteractions(mockedHideLoading)
        } else {
            verifyBlocking(mockedLoading) { invoke(hasLoading) }
            if (hasLoading) {
                verifyBlocking(mockedShowLoading) { invoke() }
                verifyNoInteractions(mockedHideLoading)
            } else {
                verifyBlocking(mockedHideLoading) { invoke() }
                verifyNoInteractions(mockedShowLoading)
            }
        }
    }

    private fun verifyError(
        errorStatus: Boolean,
        error: Boolean,
        isNone: Boolean,
        hasData: Boolean,
        withData: Boolean?
    ) {
        if (errorStatus && isNone.not() && (withData == null || (hasData == withData))) {
            verifyBlocking(mockedError) { invoke() }
            if (error) {
                verifyBlocking(mockedExceptionError) { invoke(this@ObserveWrapperTest.error) }
                verifyBlocking(mockedErrorTransform) { invoke(this@ObserveWrapperTest.error) }
                verifyBlocking(mockedTransformError) { invoke(123) }
            } else {
                verifyNoInteractions(mockedExceptionError)
                verifyNoInteractions(mockedErrorTransform)
                verifyNoInteractions(mockedTransformError)
            }
        } else {
            verifyNoInteractions(mockedError)
            verifyNoInteractions(mockedExceptionError)
            verifyNoInteractions(mockedErrorTransform)
            verifyNoInteractions(mockedTransformError)
        }
    }

    private fun verifyStatus(status: DataResultStatus, isNone: Boolean) {
        if (isNone.not()) {
            verifyBlocking(mockedStatus) { invoke(status) }
            verifyBlocking(mockedStatusTransform) { invoke(status) }
            verifyBlocking(mockedTransformStatus) { invoke(123) }
        } else {
            verifyNoInteractions(mockedStatus)
            verifyNoInteractions(mockedStatusTransform)
            verifyNoInteractions(mockedTransformStatus)
        }
    }

    private fun verifyResult(result: DataResult<String>, isNone: Boolean) {
        if (isNone.not()) {
            verifyBlocking(mockedResult) { invoke(result) }
            verifyBlocking(mockedResultTransform) { invoke(result) }
            verifyBlocking(mockedTransformResult) { invoke(123) }
        } else {
            verifyNoInteractions(mockedResult)
            verifyNoInteractions(mockedResultTransform)
            verifyNoInteractions(mockedTransformResult)
        }
    }

    private fun verifyNone(isNone: Boolean) {
        if (isNone) {
            verifyBlocking(mockedNone) { invoke() }
        } else {
            verifyNoInteractions(mockedNone)
        }
    }

    private fun verifyEmpty(isListType: Boolean, isEmpty: Boolean) {
        if (isListType.not()) {
            verifyNoInteractions(mockedEmpty)
        } else if (isEmpty) {
            verifyBlocking(mockedEmpty) { invoke() }
        } else {
            verifyNoInteractions(mockedEmpty)
        }
    }

    private fun verifyNotEmpty(isListType: Boolean, isNotEmpty: Boolean) {
        if (isListType.not()) {
            verifyNoInteractions(mockedNotEmpty)
        } else if (isNotEmpty) {
            verifyBlocking(mockedNotEmpty) { invoke() }
        } else {
            verifyNoInteractions(mockedNotEmpty)
        }
    }
}