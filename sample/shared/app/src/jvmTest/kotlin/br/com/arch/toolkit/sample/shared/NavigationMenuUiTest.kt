package br.com.arch.toolkit.sample.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppNavigationMenu
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NavigationMenuUiTest {
    @Test
    fun enlargedPortugueseLabelsCanNavigateThroughTheMenu() = runComposeUiTest {
        val labels = listOf("GitHub", "Toolkit", "Design", "Configurações")
        var selected by mutableIntStateOf(labels.lastIndex)
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 2f)) {
                AppTheme {
                    Box(Modifier.width(NARROW_WIDTH.dp)) {
                        AppNavigationMenu(labels, labels[selected], "Seções") { selected = it }
                    }
                }
            }
        }
        onNodeWithText("Configurações").assertIsDisplayed()
        onNodeWithContentDescription("Seções").performClick()
        onNodeWithText("Design").assertIsDisplayed().performClick()
        runOnIdle { assertEquals(2, selected) }
        onNodeWithText("Design").assertIsDisplayed()
    }

    private companion object {
        const val NARROW_WIDTH = 320
    }
}
