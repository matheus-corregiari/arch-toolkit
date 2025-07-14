@file:Suppress("Filename")

package br.com.arch.toolkit.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.EventDataStatus

// Setup a fake LifecycleOwner
val alwaysOnOwner = object : LifecycleOwner {
    override val lifecycle = object : Lifecycle() {
        override val currentState: State = State.RESUMED

        override fun addObserver(observer: LifecycleObserver) = Unit

        override fun removeObserver(observer: LifecycleObserver) = Unit
    }
}

@OptIn(ExperimentalTestApi::class)
fun <T> scenario(
    result: DataResult<T>,
    config: @Composable ComposableDataResult<T>.() -> Unit,
    assert: ComposeUiTest.() -> Unit,
) = runComposeUiTest {
    val flow = ResponseFlow(result)
    val comp by flow.collectAsComposableState()
    setContent {
        CompositionLocalProvider(LocalLifecycleOwner provides alwaysOnOwner) {
            Column {
                comp.Unwrap(config)
            }
        }
    }
    assert.invoke(this)
    waitForIdle()
}

val stringConfig: @Composable ComposableDataResult<String>.() -> Unit = {
    // Data
    OnData { data ->
        Text(data, modifier = Modifier.testTag("dataTag1"))
    }
    OnData { data, status ->
        Text("$data - $status", modifier = Modifier.testTag("dataTag2"))
    }
    OnData { data, status, error ->
        Text("$data - $status - ${error?.message}", modifier = Modifier.testTag("dataTag3"))
    }
    // ShowLoading
    OnShowLoading {
        Text("ShowLoading 1", modifier = Modifier.testTag("showLoadingTag1"))
    }
    OnShowLoading(EventDataStatus.WithData) {
        Text("ShowLoading 2", modifier = Modifier.testTag("showLoadingTag2"))
    }
    OnShowLoading(EventDataStatus.WithoutData) {
        Text("ShowLoading 3", modifier = Modifier.testTag("showLoadingTag3"))
    }
    // HideLoading
    OnHideLoading {
        Text("HideLoading 1", modifier = Modifier.testTag("hideLoadingTag1"))
    }
    OnHideLoading(EventDataStatus.WithData) {
        Text("HideLoading 2", modifier = Modifier.testTag("hideLoadingTag2"))
    }
    OnHideLoading(EventDataStatus.WithoutData) {
        Text("HideLoading 3", modifier = Modifier.testTag("hideLoadingTag3"))
    }
    // Error Without Throwable
    OnError { ->
        Text("Error 1", modifier = Modifier.testTag("errorTag1"))
    }
    OnError(EventDataStatus.WithData) { ->
        Text("Error 2", modifier = Modifier.testTag("errorTag2"))
    }
    OnError(EventDataStatus.WithoutData) { ->
        Text("Error 3", modifier = Modifier.testTag("errorTag3"))
    }
    // Error With Throwable
    OnError { throwable ->
        Text("Error 4 ${throwable.message}", modifier = Modifier.testTag("errorTag4"))
    }
    OnError(EventDataStatus.WithData) { throwable ->
        Text("Error 5 ${throwable.message}", modifier = Modifier.testTag("errorTag5"))
    }
    OnError(EventDataStatus.WithoutData) { throwable ->
        Text("Error 6 ${throwable.message}", modifier = Modifier.testTag("errorTag6"))
    }
    // Success
    OnSuccess {
        Text("Success 1", modifier = Modifier.testTag("successTag1"))
    }
    OnSuccess(EventDataStatus.WithData) {
        Text("Success 2", modifier = Modifier.testTag("successTag2"))
    }
    OnSuccess(EventDataStatus.WithoutData) {
        Text("Success 3", modifier = Modifier.testTag("successTag3"))
    }
    // Empty
    OnEmpty {
        Text("Empty", modifier = Modifier.testTag("emptyTag"))
    }
    // NotEmpty
    OnNotEmpty { data ->
        Text(data, modifier = Modifier.testTag("notEmptyTag"))
    }
    // Single
    OnSingle<String> { data ->
        Text(data, modifier = Modifier.testTag("singleTag"))
    }
    // Many
    OnMany { data ->
        Text(data, modifier = Modifier.testTag("manyTag"))
    }
}

val iterableConfig: @Composable ComposableDataResult<Collection<String>>.() -> Unit = {
    // Data
    OnData { data ->
        Text("$data", modifier = Modifier.testTag("dataTag1"))
    }
    OnData { data, status ->
        Text("$data - $status", modifier = Modifier.testTag("dataTag2"))
    }
    OnData { data, status, error ->
        Text("$data - $status - ${error?.message}", modifier = Modifier.testTag("dataTag3"))
    }
    // ShowLoading
    OnShowLoading {
        Text("ShowLoading 1", modifier = Modifier.testTag("showLoadingTag1"))
    }
    OnShowLoading(EventDataStatus.WithData) {
        Text("ShowLoading 2", modifier = Modifier.testTag("showLoadingTag2"))
    }
    OnShowLoading(EventDataStatus.WithoutData) {
        Text("ShowLoading 3", modifier = Modifier.testTag("showLoadingTag3"))
    }
    // HideLoading
    OnHideLoading {
        Text("HideLoading 1", modifier = Modifier.testTag("hideLoadingTag1"))
    }
    OnHideLoading(EventDataStatus.WithData) {
        Text("HideLoading 2", modifier = Modifier.testTag("hideLoadingTag2"))
    }
    OnHideLoading(EventDataStatus.WithoutData) {
        Text("HideLoading 3", modifier = Modifier.testTag("hideLoadingTag3"))
    }
    // Error Without Throwable
    OnError { ->
        Text("Error 1", modifier = Modifier.testTag("errorTag1"))
    }
    OnError(EventDataStatus.WithData) { ->
        Text("Error 2", modifier = Modifier.testTag("errorTag2"))
    }
    OnError(EventDataStatus.WithoutData) { ->
        Text("Error 3", modifier = Modifier.testTag("errorTag3"))
    }
    // Error With Throwable
    OnError { throwable ->
        Text("Error 4 ${throwable.message}", modifier = Modifier.testTag("errorTag4"))
    }
    OnError(EventDataStatus.WithData) { throwable ->
        Text("Error 5 ${throwable.message}", modifier = Modifier.testTag("errorTag5"))
    }
    OnError(EventDataStatus.WithoutData) { throwable ->
        Text("Error 6 ${throwable.message}", modifier = Modifier.testTag("errorTag6"))
    }
    // Success
    OnSuccess {
        Text("Success 1", modifier = Modifier.testTag("successTag1"))
    }
    OnSuccess(EventDataStatus.WithData) {
        Text("Success 2", modifier = Modifier.testTag("successTag2"))
    }
    OnSuccess(EventDataStatus.WithoutData) {
        Text("Success 3", modifier = Modifier.testTag("successTag3"))
    }
    // Empty
    OnEmpty {
        Text("Empty", modifier = Modifier.testTag("emptyTag"))
    }
    // NotEmpty
    OnNotEmpty { data ->
        Text("$data", modifier = Modifier.testTag("notEmptyTag"))
    }
    // Single
    OnSingle<String> { data ->
        Text(data, modifier = Modifier.testTag("singleTag"))
    }
    // Many
    OnMany { data ->
        Text("$data", modifier = Modifier.testTag("manyTag"))
    }
}

val mapConfig: @Composable ComposableDataResult<Map<String, String>>.() -> Unit = {
    animation { enabled = false }
    outsideComposable { /* See ObserveWrapper Tests */ }

    // Data
    OnData { data ->
        Text("$data", modifier = Modifier.testTag("dataTag1"))
    }
    OnData { data, status ->
        Text("$data - $status", modifier = Modifier.testTag("dataTag2"))
    }
    OnData { data, status, error ->
        Text("$data - $status - ${error?.message}", modifier = Modifier.testTag("dataTag3"))
    }
    // ShowLoading
    OnShowLoading {
        Text("ShowLoading 1", modifier = Modifier.testTag("showLoadingTag1"))
    }
    OnShowLoading(EventDataStatus.WithData) {
        Text("ShowLoading 2", modifier = Modifier.testTag("showLoadingTag2"))
    }
    OnShowLoading(EventDataStatus.WithoutData) {
        Text("ShowLoading 3", modifier = Modifier.testTag("showLoadingTag3"))
    }
    // HideLoading
    OnHideLoading {
        Text("HideLoading 1", modifier = Modifier.testTag("hideLoadingTag1"))
    }
    OnHideLoading(EventDataStatus.WithData) {
        Text("HideLoading 2", modifier = Modifier.testTag("hideLoadingTag2"))
    }
    OnHideLoading(EventDataStatus.WithoutData) {
        Text("HideLoading 3", modifier = Modifier.testTag("hideLoadingTag3"))
    }
    // Error Without Throwable
    OnError { ->
        Text("Error 1", modifier = Modifier.testTag("errorTag1"))
    }
    OnError(EventDataStatus.WithData) { ->
        Text("Error 2", modifier = Modifier.testTag("errorTag2"))
    }
    OnError(EventDataStatus.WithoutData) { ->
        Text("Error 3", modifier = Modifier.testTag("errorTag3"))
    }
    // Error With Throwable
    OnError { throwable ->
        Text("Error 4 ${throwable.message}", modifier = Modifier.testTag("errorTag4"))
    }
    OnError(EventDataStatus.WithData) { throwable ->
        Text("Error 5 ${throwable.message}", modifier = Modifier.testTag("errorTag5"))
    }
    OnError(EventDataStatus.WithoutData) { throwable ->
        Text("Error 6 ${throwable.message}", modifier = Modifier.testTag("errorTag6"))
    }
    // Success
    OnSuccess {
        Text("Success 1", modifier = Modifier.testTag("successTag1"))
    }
    OnSuccess(EventDataStatus.WithData) {
        Text("Success 2", modifier = Modifier.testTag("successTag2"))
    }
    OnSuccess(EventDataStatus.WithoutData) {
        Text("Success 3", modifier = Modifier.testTag("successTag3"))
    }
    // Empty
    OnEmpty {
        Text("Empty", modifier = Modifier.testTag("emptyTag"))
    }
    // NotEmpty
    OnNotEmpty { data ->
        Text("$data", modifier = Modifier.testTag("notEmptyTag"))
    }
    // Single
    OnSingle<Pair<String, String>> { data ->
        Text("$data", modifier = Modifier.testTag("singleTag"))
    }
    // Many
    OnMany { data ->
        Text("$data", modifier = Modifier.testTag("manyTag"))
    }
}
