package br.com.arch.toolkit.result

import org.junit.Assert.assertEquals
import org.junit.Test

class DataResultTest {

    @Test
    fun success_validateCreationMethods() {
        val resultSuccessWithNull = dataResultSuccess<String>(null)
        val resultSuccessWithData = dataResultSuccess("data")
        assertEquals(
            /* message = */ "Fail to assert Success with null data",
            /* expected = */ resultSuccessWithNull,
            /* actual = */ DataResult(null, null, DataResultStatus.SUCCESS)
        )
        assertEquals(
            /* message = */ "Fail to assert Success with data",
            /* expected = */ resultSuccessWithData,
            /* actual = */ DataResult("data", null, DataResultStatus.SUCCESS)
        )
    }

    @Test
    fun loading_validateCreationMethods() {
        val error = IllegalStateException("error")

        val resultLoadingWithNull = dataResultLoading<String>()
        val resultLoadingWithData = dataResultLoading("data")
        val resultLoadingWithError = dataResultLoading<String>(error = error)
        val resultLoadingWithDataAndError = dataResultLoading("data", error)

        assertEquals(
            /* message = */ "Fail to assert Loading with null data",
            /* expected = */ resultLoadingWithNull,
            /* actual = */ DataResult<String>(null, null, DataResultStatus.LOADING)
        )
        assertEquals(
            /* message = */ "Fail to assert Loading with data",
            /* expected = */ resultLoadingWithData,
            /* actual = */ DataResult("data", null, DataResultStatus.LOADING)
        )
        assertEquals(
            /* message = */ "Fail to assert Loading with error",
            /* expected = */ resultLoadingWithError,
            /* actual = */ DataResult<String>(null, error, DataResultStatus.LOADING)
        )
        assertEquals(
            /* message = */ "Fail to assert Loading with data and error",
            /* expected = */ resultLoadingWithDataAndError,
            /* actual = */ DataResult("data", error, DataResultStatus.LOADING)
        )
    }

    @Test
    fun error_validateCreationMethods() {
        val error = IllegalStateException("error")

        val resultErrorWithNull = dataResultError<String>(null)
        val resultErrorWithData = dataResultError(null, "data")
        val resultErrorWithError = dataResultError<String>(error)
        val resultErrorWithDataAndError = dataResultError(error, "data")

        assertEquals(
            /* message = */ "Fail to assert Error with null data",
            /* expected = */ resultErrorWithNull,
            /* actual = */ DataResult<String>(null, null, DataResultStatus.ERROR)
        )
        assertEquals(
            /* message = */ "Fail to assert Error with data",
            /* expected = */ resultErrorWithData,
            /* actual = */ DataResult("data", null, DataResultStatus.ERROR)
        )
        assertEquals(
            /* message = */ "Fail to assert Error with error",
            /* expected = */ resultErrorWithError,
            /* actual = */ DataResult<String>(null, error, DataResultStatus.ERROR)
        )
        assertEquals(
            /* message = */ "Fail to assert Error with data and error",
            /* expected = */ resultErrorWithDataAndError,
            /* actual = */ DataResult("data", error, DataResultStatus.ERROR)
        )
    }

    @Test
    fun none_validateCreationMethods() {

        val resultNone = dataResultNone<String>()

        assertEquals(
            /* message = */ "Fail to assert None",
            /* expected = */ resultNone,
            /* actual = */ DataResult<String>(null, null, DataResultStatus.NONE)
        )
    }

    @Test
    fun validate_mergeWith() {
        /* TODO */
    }

    @Test
    fun validate_mergeAll() {
        /* TODO */
    }
}