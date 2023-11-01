@file:Suppress("LongMethod")

package br.com.arch.toolkit.result

import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import org.junit.Assert.assertEquals
import org.junit.Test

class DataResultCreationTest {

    private val error = IllegalStateException("error")

    @Test
    fun validate_creationMethods() {
        listOf(
            // None
            dataResultNone<String>() to
                DataResult<String>(null, null, DataResultStatus.NONE),

            // Success
            dataResultSuccess<String>(null) to
                DataResult(null, null, DataResultStatus.SUCCESS),
            dataResultSuccess("data") to
                DataResult("data", null, DataResultStatus.SUCCESS),

            // Loading
            dataResultLoading<String>() to
                DataResult<String>(null, null, DataResultStatus.LOADING),
            dataResultLoading("data") to
                DataResult("data", null, DataResultStatus.LOADING),
            dataResultLoading<String>(error = error) to
                DataResult<String>(null, error, DataResultStatus.LOADING),
            dataResultLoading("data", error) to
                DataResult("data", error, DataResultStatus.LOADING),

            // Error
            dataResultError<String>(null) to
                DataResult<String>(null, null, DataResultStatus.ERROR),
            dataResultError(null, "data") to
                DataResult("data", null, DataResultStatus.ERROR),
            dataResultError<String>(error) to
                DataResult<String>(null, error, DataResultStatus.ERROR),
            dataResultError(error, "data") to
                DataResult("data", error, DataResultStatus.ERROR),
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
