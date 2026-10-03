package br.com.arch.toolkit.sample.shared

import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import br.com.arch.toolkit.sample.core.model.AppLanguage
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitScreen
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitViewModel
import br.com.arch.toolkit.sample.repository.ToolkitDemoRepository
import br.com.arch.toolkit.storage.memory.MemoryStoreProvider
import org.jetbrains.skia.Image
import java.io.File
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ToolkitUiTest {
    @Test
    fun splinterControlsRunTheLocalTasks() = runComposeUiTest {
        val repository = ToolkitDemoRepository(MemoryStoreProvider(database = mutableMapOf()))
        val model = ToolkitViewModel(repository)
        try {
            setContent {
                CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ENGLISH) {
                    AppTheme { Surface { ToolkitScreen(model) } }
                }
            }
            onNode(hasScrollAction()).performScrollToNode(hasText("Run task"))
            onNodeWithText("Run task").performClick()
            waitUntil(timeoutMillis = 5_000) { model.splinter.request.value.isSuccess }
            onNodeWithText("Success\nArch Toolkit").performScrollTo().assertIsDisplayed()
            onNodeWithText("Simulate failure").performScrollTo().performClick()
            waitUntil(timeoutMillis = 5_000) { model.splinter.request.value.isError }
            onNodeWithText("Failure").performScrollTo().assertIsDisplayed()
            onNode(hasScrollAction()).performScrollToNode(hasText("Start polling"))
            onNodeWithText("Start polling").performClick()
            waitUntil(timeoutMillis = 5_000) { model.splinter.polling.value.isSuccess }
            onNodeWithText("Success\n3 / 3").performScrollTo().assertIsDisplayed()
        } finally {
            model.splinter.close()
            repository.close()
        }
    }

    @Test
    fun logClearAndStorageControlsAreUsable() = runComposeUiTest {
        val repository = ToolkitDemoRepository(MemoryStoreProvider(database = mutableMapOf()))
        val model = ToolkitViewModel(repository)
        try {
            setContent {
                CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ENGLISH) {
                    AppTheme {
                        Surface { ToolkitScreen(model) }
                    }
                }
            }
            onNodeWithText(
                "Write log"
            ).performSemanticsAction(SemanticsActions.RequestFocus) { it() }
            onNodeWithText("Write log").performKeyInput { pressKey(Key.Enter) }
            onNodeWithText("Info: Hello from Arch Toolkit").assertIsDisplayed()
            onNodeWithText("Clear").assertIsDisplayed().performClick()
            onNodeWithText("Save").performScrollTo().performClick()
            waitUntil(timeoutMillis = 5_000) { model.storage.value.message == AppText.SAVED }
            onNodeWithText("Saved").performScrollTo().assertIsDisplayed()
            onNodeWithText("Delete").performScrollTo().performClick()
            waitUntil(timeoutMillis = 5_000) { model.storage.value.message == AppText.DELETED }
            onNodeWithText("No saved value").performScrollTo().assertIsDisplayed()
            val output = File("build/reports/showcase/toolkit.png")
            output.parentFile.mkdirs()
            val bitmap = onRoot().captureToImage().asSkiaBitmap()
            output.writeBytes(requireNotNull(Image.makeFromBitmap(bitmap).encodeToData()).bytes)
        } finally {
            model.splinter.close()
            repository.close()
        }
    }
}
