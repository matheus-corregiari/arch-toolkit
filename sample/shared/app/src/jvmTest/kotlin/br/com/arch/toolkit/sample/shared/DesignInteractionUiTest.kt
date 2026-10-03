package br.com.arch.toolkit.sample.shared

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import br.com.arch.toolkit.sample.core.model.AppLanguage
import br.com.arch.toolkit.sample.core.model.ContrastMode
import br.com.arch.toolkit.sample.core.model.ThemeMode
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.feature.github.ui.list.GithubListState
import br.com.arch.toolkit.sample.feature.github.ui.list.RepositoryListContent
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsContent
import br.com.arch.toolkit.sample.feature.toolkit.EcosystemContent
import br.com.arch.toolkit.sample.feature.toolkit.StorageDemoState
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitContent
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class DesignInteractionUiTest {
    @Test
    fun preferencesAreValuesAndCallbacks() = runComposeUiTest {
        var language by mutableStateOf(AppLanguage.ENGLISH)
        var theme by mutableStateOf(ThemeMode.LIGHT)
        var contrast by mutableStateOf(ContrastMode.STANDARD)
        setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ENGLISH) {
                AppTheme {
                    SettingsContent(
                        language,
                        theme,
                        contrast,
                        onLanguageChange = { language = it },
                        onThemeChange = { theme = it },
                        onContrastChange = { contrast = it }
                    )
                }
            }
        }
        onNodeWithText("Dark").performScrollTo().performClick()
        onNodeWithText("High").performScrollTo().performClick()
        onNodeWithText("Português do Brasil").performScrollTo().performClick()
        runOnIdle {
            assertEquals(ThemeMode.DARK, theme)
            assertEquals(ContrastMode.HIGH, contrast)
            assertEquals(AppLanguage.PORTUGUESE_BRAZIL, language)
        }
    }

    @Test
    fun keyboardSubmitUsesTheEditedQueryImmediately() = runComposeUiTest {
        var state by mutableStateOf(GithubListState(nextPage = null))
        var submitted: String? = null
        setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ENGLISH) {
                AppTheme {
                    RepositoryListContent(
                        state,
                        onQueryChange = { state = state.copy(editingQuery = it) },
                        onSearch = { query, _ -> submitted = query }
                    )
                }
            }
        }
        val field = onNode(hasText("Search repositories") and hasSetTextAction())
        field.performTextReplacement("storage")
        field.performImeAction()
        runOnIdle { assertEquals("storage", submitted) }
    }

    @Test
    fun executableSourceIsAvailableThroughDisclosure() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ENGLISH) {
                AppTheme { ToolkitContent(emptyList(), StorageDemoState()) }
            }
        }
        onAllNodesWithText("View executed code").onFirst().performScrollTo().performClick()
        onNodeWithText("Lumber.tag", substring = true).performScrollTo().assertIsDisplayed()
        onNodeWithText("Hide executed code").performScrollTo().performClick()
        onAllNodesWithText("View executed code").onFirst().assertIsDisplayed()
    }

    @Test
    fun eachRepositoryOpensItsOwnGithubPage() = runComposeUiTest {
        val opened = mutableListOf<String>()
        setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.PORTUGUESE_BRAZIL) {
                AppTheme { EcosystemContent(onOpenRepository = opened::add) }
            }
        }
        val repositories = listOf(
            "Arch Toolkit" to "arch-toolkit",
            "Arch Android" to "arch-android",
            "Event Observer" to "arch-event-observer",
            "Lumber" to "arch-lumber",
            "Storage" to "arch-storage"
        )
        repositories.forEach { (title, repository) ->
            onNode(
                hasScrollAction()
            ).performScrollToNode(hasContentDescription("Abrir no GitHub: $title"))
            onNodeWithContentDescription("Abrir no GitHub: $title").performScrollTo().performClick()
            runOnIdle {
                assertEquals(
                    "https://github.com/matheus-corregiari/$repository",
                    opened.last()
                )
            }
        }
        runOnIdle { assertEquals(5, opened.size) }
    }
}
