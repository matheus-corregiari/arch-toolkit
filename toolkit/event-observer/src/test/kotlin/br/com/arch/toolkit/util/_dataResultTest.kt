@file:Suppress("LongMethod", "ClassNaming", "ClassName")

package br.com.arch.toolkit.util

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import org.junit.Assert.assertEquals
import org.junit.Test

class _dataResultTest {

    private val error = IllegalStateException("error")

    @Test
    fun validate_creationMethods() {
        listOf(
            // None
            dataResultNone<String>() to DataResult<String>(null, null, NONE),

            // Success
            dataResultSuccess<String>(null) to DataResult(null, null, SUCCESS),
            dataResultSuccess("data") to DataResult("data", null, SUCCESS),

            // Loading
            dataResultLoading<String>() to DataResult<String>(null, null, LOADING),
            dataResultLoading("data") to DataResult("data", null, LOADING),
            dataResultLoading<String>(error = error) to DataResult<String>(null, error, LOADING),
            dataResultLoading("data", error) to DataResult("data", error, LOADING),

            // Error
            dataResultError<String>(null) to DataResult<String>(null, null, ERROR),
            dataResultError(null, "data") to DataResult("data", null, ERROR),
            dataResultError<String>(error) to DataResult<String>(null, error, ERROR),
            dataResultError(
                error, "data"
            ) to DataResult("data", error, ERROR),
        ).onEach { (actual, expected) ->
            assertEquals(
                "Assert Status: ${expected.status}, " +
                    "with data: ${expected.data}, " +
                    "with error: ${expected.error}",
                expected,
                actual
            )
        }
    }
}
