@file:OptIn(ExperimentalTestApi::class)
@file:Suppress("OPT_IN_USAGE")

package br.com.arch.toolkit.compose

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
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
class ComposableDataResultLoadingTest {
    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `1 - String, LOADING, null`() = scenario(
        result = DataResult("Hello Compose", null, DataResultStatus.LOADING),
        config = stringConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("Hello Compose")
            onNodeWithTag("dataTag2").assertTextEquals("Hello Compose - LOADING")
            onNodeWithTag("dataTag3").assertTextEquals("Hello Compose - LOADING - null")
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertTextEquals("ShowLoading 1")
            onNodeWithTag("showLoadingTag2").assertTextEquals("ShowLoading 2")
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
    fun `2 - null, LOADING, Throwable`() = scenario(
        result = DataResult(null, RuntimeException("fail"), DataResultStatus.LOADING),
        config = stringConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertDoesNotExist()
            onNodeWithTag("dataTag2").assertDoesNotExist()
            onNodeWithTag("dataTag3").assertDoesNotExist()
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertTextEquals("ShowLoading 1")
            onNodeWithTag("showLoadingTag2").assertDoesNotExist()
            onNodeWithTag("showLoadingTag3").assertTextEquals("ShowLoading 3")
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
    fun `3 - String, LOADING, Throwable`() = scenario(
        result = DataResult("Hello Compose", RuntimeException("fail"), DataResultStatus.LOADING),
        config = stringConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("Hello Compose")
            onNodeWithTag("dataTag2").assertTextEquals("Hello Compose - LOADING")
            onNodeWithTag("dataTag3").assertTextEquals("Hello Compose - LOADING - fail")
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertTextEquals("ShowLoading 1")
            onNodeWithTag("showLoadingTag2").assertTextEquals("ShowLoading 2")
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
    fun `4 - null, LOADING, null`() = scenario(
        result = DataResult(null, null, DataResultStatus.LOADING),
        config = stringConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertDoesNotExist()
            onNodeWithTag("dataTag2").assertDoesNotExist()
            onNodeWithTag("dataTag3").assertDoesNotExist()
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertTextEquals("ShowLoading 1")
            onNodeWithTag("showLoadingTag2").assertDoesNotExist()
            onNodeWithTag("showLoadingTag3").assertTextEquals("ShowLoading 3")
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
