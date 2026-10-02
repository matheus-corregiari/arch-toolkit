package br.com.arch.toolkit.sample.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.GithubListState
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.RepositoryListContent
import br.com.arch.toolkit.sample.feature.toolkit.StorageDemoState
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitContent
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ResponsiveLayoutUiTest {
    @Test
    fun resizingKeepsEditedStorageInputs() = runComposeUiTest {
        var width by mutableStateOf(WIDE_WIDTH.dp)
        setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ENGLISH) {
                AppTheme {
                    Box(Modifier.requiredSize(width, CONTENT_HEIGHT.dp)) {
                        ToolkitContent(emptyList(), StorageDemoState())
                    }
                }
            }
        }
        val lumber = onNodeWithText("Lumber").fetchSemanticsNode().boundsInRoot
        val storage = onNodeWithText("Storage").fetchSemanticsNode().boundsInRoot
        assertEquals(lumber.top, storage.top)
        onNode(hasText("Key") and hasSetTextAction()).performTextReplacement("responsive-key")
        runOnIdle { width = COMPACT_WIDTH.dp }
        onNodeWithText("responsive-key").performScrollTo().assertIsDisplayed()
        runOnIdle { width = WIDE_WIDTH.dp }
        onNodeWithText("responsive-key").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun shortLandscapeKeepsResultsAndSearchAccessibleAtDoubleFontSize() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(
                LocalAppLanguage provides AppLanguage.ENGLISH,
                LocalDensity provides Density(1f, 2f)
            ) {
                AppTheme {
                    Box(Modifier.requiredSize(LANDSCAPE_WIDTH.dp, LANDSCAPE_HEIGHT.dp)) {
                        RepositoryListContent(GithubListState(nextPage = null))
                    }
                }
            }
        }
        onNodeWithText("No repositories found").assertIsDisplayed()
        onNode(hasText("Search repositories") and hasSetTextAction())
            .performScrollTo().assertIsDisplayed()
        onNodeWithText("Java").performScrollTo().assertIsDisplayed()
    }

    private companion object {
        const val WIDE_WIDTH = 900
        const val COMPACT_WIDTH = 360
        const val CONTENT_HEIGHT = 700
        const val LANDSCAPE_WIDTH = 800
        const val LANDSCAPE_HEIGHT = 360
    }
}
