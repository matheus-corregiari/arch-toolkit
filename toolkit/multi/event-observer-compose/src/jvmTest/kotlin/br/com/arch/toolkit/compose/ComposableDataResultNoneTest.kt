@file:OptIn(ExperimentalTestApi::class)
@file:Suppress("OPT_IN_USAGE")

package br.com.arch.toolkit.compose

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ComposableDataResultNoneTest {
    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `1 - String, NONE, null`() = scenario(
        result = DataResult("Hello Compose", null, DataResultStatus.NONE),
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
            onNodeWithTag("hideLoadingTag1").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag2").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag3").assertDoesNotExist()
            // Error Without Throwable
            onNodeWithTag("errorTag1").assertDoesNotExist()
            onNodeWithTag("errorTag2").assertDoesNotExist()
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
    fun `2 - null, NONE, Throwable`() = scenario(
        result = DataResult(null, RuntimeException("fail"), DataResultStatus.NONE),
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
            onNodeWithTag("hideLoadingTag1").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag2").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag3").assertDoesNotExist()
            // Error Without Throwable
            onNodeWithTag("errorTag1").assertDoesNotExist()
            onNodeWithTag("errorTag2").assertDoesNotExist()
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
    fun `3 - String, NONE, Throwable`() = scenario(
        result = DataResult("Hello Compose", RuntimeException("fail"), DataResultStatus.NONE),
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
            onNodeWithTag("hideLoadingTag1").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag2").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag3").assertDoesNotExist()
            // Error Without Throwable
            onNodeWithTag("errorTag1").assertDoesNotExist()
            onNodeWithTag("errorTag2").assertDoesNotExist()
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
    fun `4 - null, NONE, null`() = scenario(
        result = DataResult(null, null, DataResultStatus.NONE),
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
            onNodeWithTag("hideLoadingTag1").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag2").assertDoesNotExist()
            onNodeWithTag("hideLoadingTag3").assertDoesNotExist()
            // Error Without Throwable
            onNodeWithTag("errorTag1").assertDoesNotExist()
            onNodeWithTag("errorTag2").assertDoesNotExist()
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
}
