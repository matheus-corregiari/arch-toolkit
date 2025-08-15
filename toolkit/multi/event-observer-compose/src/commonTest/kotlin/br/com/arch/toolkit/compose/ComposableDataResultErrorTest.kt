@file:OptIn(ExperimentalTestApi::class)
@file:Suppress("OPT_IN_USAGE")

package br.com.arch.toolkit.compose

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.test.PlatformTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlin.test.Test

class ComposableDataResultErrorTest : PlatformTest() {
    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `1 - String, ERROR, null`() = scenario(
        result = DataResult("Hello Compose", null, DataResultStatus.ERROR),
        config = stringConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("Hello Compose")
            onNodeWithTag("dataTag2").assertTextEquals("Hello Compose - ERROR")
            onNodeWithTag("dataTag3").assertTextEquals("Hello Compose - ERROR - null")
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertDoesNotExist()
            onNodeWithTag("showLoadingTag2").assertDoesNotExist()
            onNodeWithTag("showLoadingTag3").assertDoesNotExist()
            // Hide Loading
            onNodeWithTag("hideLoadingTag1").assertTextEquals("HideLoading 1")
            onNodeWithTag("hideLoadingTag2").assertTextEquals("HideLoading 2")
            onNodeWithTag("hideLoadingTag3").assertDoesNotExist()
            // Error Without Throwable
            onNodeWithTag("errorTag1").assertTextEquals("Error 1")
            onNodeWithTag("errorTag2").assertTextEquals("Error 2")
            onNodeWithTag("errorTag3").assertDoesNotExist()
            // Error With Throwable
            onNodeWithTag("errorTag4").assertDoesNotExist()
            onNodeWithTag("errorTag5").assertDoesNotExist()
            onNodeWithTag("errorTag6").assertDoesNotExist()
            // Success
            onNodeWithTag("successTag1").assertDoesNotExist()
            onNodeWithTag("successTag2").assertDoesNotExist()
            onNodeWithTag("successTag3").assertDoesNotExist()
            // Empty
            onNodeWithTag("emptyTag").assertDoesNotExist()
            // NotEmpty
            onNodeWithTag("notEmptyTag").assertDoesNotExist()
            // Single
            onNodeWithTag("singleTag").assertDoesNotExist()
            // Many
            onNodeWithTag("manyTag").assertDoesNotExist()
        },
    )

    @Test
    fun `2 - null, ERROR, Throwable`() = scenario(
        result = DataResult(null, RuntimeException("fail"), DataResultStatus.ERROR),
        config = stringConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertDoesNotExist()
            onNodeWithTag("dataTag2").assertDoesNotExist()
            onNodeWithTag("dataTag3").assertDoesNotExist()
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertDoesNotExist()
            onNodeWithTag("showLoadingTag2").assertDoesNotExist()
            onNodeWithTag("showLoadingTag3").assertDoesNotExist()
            // Hide Loading
            onNodeWithTag("hideLoadingTag1").assertTextEquals("HideLoading 1")
            onNodeWithTag("hideLoadingTag2").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag3").assertTextEquals("HideLoading 3")
            // Error Without Throwable
            onNodeWithTag("errorTag1").assertTextEquals("Error 1")
            onNodeWithTag("errorTag2").assertDoesNotExist()
            onNodeWithTag("errorTag3").assertTextEquals("Error 3")
            // Error With Throwable
            onNodeWithTag("errorTag4").assertTextEquals("Error 4 fail")
            onNodeWithTag("errorTag5").assertDoesNotExist()
            onNodeWithTag("errorTag6").assertTextEquals("Error 6 fail")
            // Success
            onNodeWithTag("successTag1").assertDoesNotExist()
            onNodeWithTag("successTag2").assertDoesNotExist()
            onNodeWithTag("successTag3").assertDoesNotExist()
            // Empty
            onNodeWithTag("emptyTag").assertDoesNotExist()
            // NotEmpty
            onNodeWithTag("notEmptyTag").assertDoesNotExist()
            // Single
            onNodeWithTag("singleTag").assertDoesNotExist()
            // Many
            onNodeWithTag("manyTag").assertDoesNotExist()
        },
    )

    @Test
    fun `3 - String, ERROR, Throwable`() = scenario(
        result = DataResult("Hello Compose", RuntimeException("fail"), DataResultStatus.ERROR),
        config = stringConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("Hello Compose")
            onNodeWithTag("dataTag2").assertTextEquals("Hello Compose - ERROR")
            onNodeWithTag("dataTag3").assertTextEquals("Hello Compose - ERROR - fail")
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertDoesNotExist()
            onNodeWithTag("showLoadingTag2").assertDoesNotExist()
            onNodeWithTag("showLoadingTag3").assertDoesNotExist()
            // Hide Loading
            onNodeWithTag("hideLoadingTag1").assertTextEquals("HideLoading 1")
            onNodeWithTag("hideLoadingTag2").assertTextEquals("HideLoading 2")
            onNodeWithTag("hideLoadingTag3").assertDoesNotExist()
            // Error Without Throwable
            onNodeWithTag("errorTag1").assertTextEquals("Error 1")
            onNodeWithTag("errorTag2").assertTextEquals("Error 2")
            onNodeWithTag("errorTag3").assertDoesNotExist()
            // Error With Throwable
            onNodeWithTag("errorTag4").assertTextEquals("Error 4 fail")
            onNodeWithTag("errorTag5").assertTextEquals("Error 5 fail")
            onNodeWithTag("errorTag6").assertDoesNotExist()
            // Success
            onNodeWithTag("successTag1").assertDoesNotExist()
            onNodeWithTag("successTag2").assertDoesNotExist()
            onNodeWithTag("successTag3").assertDoesNotExist()
            // Empty
            onNodeWithTag("emptyTag").assertDoesNotExist()
            // NotEmpty
            onNodeWithTag("notEmptyTag").assertDoesNotExist()
            // Single
            onNodeWithTag("singleTag").assertDoesNotExist()
            // Many
            onNodeWithTag("manyTag").assertDoesNotExist()
        },
    )

    @Test
    fun `4 - null, ERROR, null`() = scenario(
        result = DataResult(null, null, DataResultStatus.ERROR),
        config = stringConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertDoesNotExist()
            onNodeWithTag("dataTag2").assertDoesNotExist()
            onNodeWithTag("dataTag3").assertDoesNotExist()
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertDoesNotExist()
            onNodeWithTag("showLoadingTag2").assertDoesNotExist()
            onNodeWithTag("showLoadingTag3").assertDoesNotExist()
            // Hide Loading
            onNodeWithTag("hideLoadingTag1").assertTextEquals("HideLoading 1")
            onNodeWithTag("hideLoadingTag2").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag3").assertTextEquals("HideLoading 3")
            // Error Without Throwable
            onNodeWithTag("errorTag1").assertTextEquals("Error 1")
            onNodeWithTag("errorTag2").assertDoesNotExist()
            onNodeWithTag("errorTag3").assertTextEquals("Error 3")
            // Error With Throwable
            onNodeWithTag("errorTag4").assertDoesNotExist()
            onNodeWithTag("errorTag5").assertDoesNotExist()
            onNodeWithTag("errorTag6").assertDoesNotExist()
            // Success
            onNodeWithTag("successTag1").assertDoesNotExist()
            onNodeWithTag("successTag2").assertDoesNotExist()
            onNodeWithTag("successTag3").assertDoesNotExist()
            // Empty
            onNodeWithTag("emptyTag").assertDoesNotExist()
            // NotEmpty
            onNodeWithTag("notEmptyTag").assertDoesNotExist()
            // Single
            onNodeWithTag("singleTag").assertDoesNotExist()
            // Many
            onNodeWithTag("manyTag").assertDoesNotExist()
        },
    )
}
