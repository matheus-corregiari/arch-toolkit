@file:Suppress("LongMethod")

package br.com.arch.toolkit.result

import io.mockk.mockk
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlin.test.Test
import kotlin.test.assertEquals

class DataResultCreationTest {

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
            dataResultError(error, "data") to DataResult("data", error, ERROR),
        ).onEach { (actual, expected) -> assertEquals(expected, actual) }
    }
}
