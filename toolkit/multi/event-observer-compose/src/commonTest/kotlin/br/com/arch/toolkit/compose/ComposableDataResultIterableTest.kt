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

class ComposableDataResultIterableTest : PlatformTest() {
    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `1 - Empty Iterable - SUCCESS - null`() = scenario(
        result = DataResult(listOf(), null, DataResultStatus.SUCCESS),
        config = iterableConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("[]")
            onNodeWithTag("dataTag2").assertTextEquals("[] - SUCCESS")
            onNodeWithTag("dataTag3").assertTextEquals("[] - SUCCESS - null")
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertDoesNotExist()
            onNodeWithTag("showLoadingTag2").assertDoesNotExist()
            onNodeWithTag("showLoadingTag3").assertDoesNotExist()
            // Hide Loading
            onNodeWithTag("hideLoadingTag1").assertTextEquals("HideLoading 1")
            onNodeWithTag("hideLoadingTag2").assertTextEquals("HideLoading 2")
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
            onNodeWithTag("successTag1").assertTextEquals("Success 1")
            onNodeWithTag("successTag2").assertTextEquals("Success 2")
            onNodeWithTag("successTag3").assertDoesNotExist()
            // Empty
            onNodeWithTag("emptyTag").assertTextEquals("Empty")
            // NotEmpty
            onNodeWithTag("notEmptyTag").assertDoesNotExist()
            // Single
            onNodeWithTag("singleTag").assertDoesNotExist()
            // Many
            onNodeWithTag("manyTag").assertDoesNotExist()
        },
    )

    @Test
    fun `2 - Single Iterable - SUCCESS - null`() = scenario(
        result = DataResult(listOf("Hello Compose"), null, DataResultStatus.SUCCESS),
        config = iterableConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("[Hello Compose]")
            onNodeWithTag("dataTag2").assertTextEquals("[Hello Compose] - SUCCESS")
            onNodeWithTag("dataTag3").assertTextEquals("[Hello Compose] - SUCCESS - null")
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertDoesNotExist()
            onNodeWithTag("showLoadingTag2").assertDoesNotExist()
            onNodeWithTag("showLoadingTag3").assertDoesNotExist()
            // Hide Loading
            onNodeWithTag("hideLoadingTag1").assertTextEquals("HideLoading 1")
            onNodeWithTag("hideLoadingTag2").assertTextEquals("HideLoading 2")
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
            onNodeWithTag("successTag1").assertTextEquals("Success 1")
            onNodeWithTag("successTag2").assertTextEquals("Success 2")
            onNodeWithTag("successTag3").assertDoesNotExist()
            // Empty
            onNodeWithTag("emptyTag").assertDoesNotExist()
            // NotEmpty
            onNodeWithTag("notEmptyTag").assertTextEquals("[Hello Compose]")
            // Single
            onNodeWithTag("singleTag").assertTextEquals("Hello Compose")
            // Many
            onNodeWithTag("manyTag").assertDoesNotExist()
        },
    )

    @Test
    fun `3 - Many Iterable - SUCCESS - null`() = scenario(
        result = DataResult(listOf("Hello Compose", "Bye Compose"), null, DataResultStatus.SUCCESS),
        config = iterableConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("[Hello Compose, Bye Compose]")
            onNodeWithTag("dataTag2").assertTextEquals("[Hello Compose, Bye Compose] - SUCCESS")
            onNodeWithTag("dataTag3").assertTextEquals("[Hello Compose, Bye Compose] - SUCCESS - null")
            // Show Loading
            onNodeWithTag("showLoadingTag1").assertDoesNotExist()
            onNodeWithTag("showLoadingTag2").assertDoesNotExist()
            onNodeWithTag("showLoadingTag3").assertDoesNotExist()
            // Hide Loading
            onNodeWithTag("hideLoadingTag1").assertTextEquals("HideLoading 1")
            onNodeWithTag("hideLoadingTag2").assertTextEquals("HideLoading 2")
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
            onNodeWithTag("successTag1").assertTextEquals("Success 1")
            onNodeWithTag("successTag2").assertTextEquals("Success 2")
            onNodeWithTag("successTag3").assertDoesNotExist()
            // Empty
            onNodeWithTag("emptyTag").assertDoesNotExist()
            // NotEmpty
            onNodeWithTag("notEmptyTag").assertTextEquals("[Hello Compose, Bye Compose]")
            // Single
            onNodeWithTag("singleTag").assertDoesNotExist()
            // Many
            onNodeWithTag("manyTag").assertTextEquals("[Hello Compose, Bye Compose]")
        },
    )

}
