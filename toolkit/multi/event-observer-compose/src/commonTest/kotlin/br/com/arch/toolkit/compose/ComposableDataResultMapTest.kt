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

class ComposableDataResultMapTest : PlatformTest() {
    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `1 - Empty Map, SUCCESS, null`() = scenario(
        result = DataResult(mapOf(), null, DataResultStatus.SUCCESS),
        config = mapConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("{}")
            onNodeWithTag("dataTag2").assertTextEquals("{} - SUCCESS")
            onNodeWithTag("dataTag3").assertTextEquals("{} - SUCCESS - null")
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
    fun `2 - Single Map, SUCCESS, null`() = scenario(
        result = DataResult(mapOf("1" to "Hello Compose"), null, DataResultStatus.SUCCESS),
        config = mapConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("{1=Hello Compose}")
            onNodeWithTag("dataTag2").assertTextEquals("{1=Hello Compose} - SUCCESS")
            onNodeWithTag("dataTag3").assertTextEquals("{1=Hello Compose} - SUCCESS - null")
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
            onNodeWithTag("notEmptyTag").assertTextEquals("{1=Hello Compose}")
            // Single
            onNodeWithTag("singleTag").assertTextEquals("(1, Hello Compose)")
            // Many
            onNodeWithTag("manyTag").assertDoesNotExist()
        },
    )

    @Test
    fun `3 - Many Map, SUCCESS, null`() = scenario(
        result = DataResult(
            mapOf("1" to "Hello Compose", "2" to "Bye Compose"),
            null,
            DataResultStatus.SUCCESS,
        ),
        config = mapConfig,
        assert = {
            // Data
            onNodeWithTag("dataTag1").assertTextEquals("{1=Hello Compose, 2=Bye Compose}")
            onNodeWithTag("dataTag2").assertTextEquals("{1=Hello Compose, 2=Bye Compose} - SUCCESS")
            onNodeWithTag("dataTag3").assertTextEquals("{1=Hello Compose, 2=Bye Compose} - SUCCESS - null")
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
            onNodeWithTag("notEmptyTag").assertTextEquals("{1=Hello Compose, 2=Bye Compose}")
            // Single
            onNodeWithTag("singleTag").assertDoesNotExist()
            // Many
            onNodeWithTag("manyTag").assertTextEquals("{1=Hello Compose, 2=Bye Compose}")
        },
    )
}
