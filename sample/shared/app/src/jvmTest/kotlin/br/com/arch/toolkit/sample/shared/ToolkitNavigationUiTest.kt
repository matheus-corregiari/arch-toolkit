package br.com.arch.toolkit.sample.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.core.model.AppLanguage
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.feature.toolkit.StorageDemoState
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitContent
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitLibrary
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitRoute
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitSampleRoute
import br.com.arch.toolkit.sample.shared.ui.home.AppHomeContent
import com.pedrobneto.easy.navigation.core.Navigation
import com.pedrobneto.easy.navigation.core.NavigationController
import com.pedrobneto.easy.navigation.core.rememberNavigationController
import com.pedrobneto.easy.navigation.registry.ToolkitDirectionRegistry
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class, ExperimentalMaterial3AdaptiveApi::class)
class ToolkitNavigationUiTest {
    @Test
    fun sampleNavigationKeepsChromeInPlaceAndReturnsToCatalogue() = runComposeUiTest {
        lateinit var navigation: NavigationController
        setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ENGLISH) {
                Box(Modifier.requiredSize(360.dp, 800.dp)) {
                    AppTheme {
                        navigation =
                            rememberNavigationController(
                                ToolkitRoute,
                                listOf(ToolkitDirectionRegistry)
                            )
                        AppHomeContent(currentRoute = navigation.currentRoute) {
                            Navigation(modifier = Modifier.fillMaxSize(), controller = navigation)
                        }
                    }
                }
            }
        }
        val chrome = onNode(hasText("Toolkit") and hasClickAction())
        val bounds = chrome.fetchSemanticsNode().boundsInRoot
        chrome.assertIsSelected()
        val sample = onNodeWithContentDescription("Open sample: Event Observer")
        sample.performScrollTo()
        // Check midway through the destination animation as well as after it settles.
        mainClock.autoAdvance = false
        sample.performClick()
        mainClock.advanceTimeBy(100)
        assertEquals(bounds, chrome.fetchSemanticsNode().boundsInRoot)
        mainClock.autoAdvance = true
        waitForIdle()
        chrome.assertIsSelected()
        assertEquals(bounds, chrome.fetchSemanticsNode().boundsInRoot)
        runOnIdle {
            assertEquals(
                ToolkitSampleRoute(ToolkitLibrary.OBSERVER),
                navigation.currentRoute
            )
        }
        onNodeWithText("DataResult").assertIsDisplayed()
        onNodeWithText("Back to catalogue").performScrollTo().performClick()
        runOnIdle { assertEquals(ToolkitRoute, navigation.currentRoute) }
        onNodeWithContentDescription(
            "Open sample: Arch Toolkit"
        ).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun libraryPageScrollIncludesTheHeadingAndBottomLink() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ENGLISH) {
                Box(Modifier.requiredSize(320.dp, 360.dp)) {
                    AppTheme {
                        ToolkitContent(
                            emptyList(),
                            StorageDemoState(),
                            library = ToolkitLibrary.STORAGE
                        )
                    }
                }
            }
        }
        onAllNodes(hasScrollAction()).assertCountEquals(1)
        onNodeWithText("Open on GitHub").performScrollTo().assertIsDisplayed()
        onAllNodesWithText("Storage").onFirst().assertIsNotDisplayed()
        onAllNodesWithText("Storage").onFirst().performScrollTo().assertIsDisplayed()
    }
}
