package br.com.arch.toolkit.sample.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.ComposeUiTest
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
import br.com.arch.toolkit.sample.feature.design.DesignRoute
import br.com.arch.toolkit.sample.feature.toolkit.StorageDemoState
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitContent
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitLibrary
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitRoute
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitSampleRoute
import br.com.arch.toolkit.sample.shared.ui.home.AppHomeContent
import br.com.arch.toolkit.sample.shared.ui.home.ShowcaseNavigation
import com.pedrobneto.easy.navigation.core.NavigationController
import com.pedrobneto.easy.navigation.core.model.LaunchStrategy
import com.pedrobneto.easy.navigation.core.rememberNavigationController
import com.pedrobneto.easy.navigation.registry.DesignDirectionRegistry
import com.pedrobneto.easy.navigation.registry.ToolkitDirectionRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalMaterial3AdaptiveApi::class)
class ToolkitNavigationUiTest {
    @Test
    fun toolbarReturnsToCatalogueWhenASampleWasOpenedDirectly() = runComposeUiTest {
        lateinit var navigation: NavigationController
        setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ENGLISH) {
                Box(Modifier.requiredSize(360.dp, 800.dp)) {
                    AppTheme {
                        navigation = rememberNavigationController(
                            "/toolkit/OBSERVER",
                            listOf(ToolkitDirectionRegistry)
                        )
                        ShowcaseNavigation(navigation, Modifier.fillMaxSize())
                    }
                }
            }
        }
        runOnIdle { navigation.navigateTo(ToolkitSampleRoute(ToolkitLibrary.ANDROID)) }
        onNodeWithContentDescription("Back to catalogue").performClick()
        runOnIdle {
            assertEquals(ToolkitRoute, navigation.currentRoute)
            assertEquals(0, navigation.currentIndex)
        }
        onNodeWithContentDescription("Open sample: Arch Toolkit").assertIsDisplayed()
    }

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
                                listOf(ToolkitDirectionRegistry, DesignDirectionRegistry)
                            )
                        AppHomeContent(
                            currentRoute = navigation.currentRoute,
                            onNavigate = { navigation.navigateTo(it, LaunchStrategy.NewStack) }
                        ) {
                            ShowcaseNavigation(
                                modifier = Modifier.fillMaxSize(),
                                controller = navigation
                            )
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
        val entering = onNodeWithText("DataResult").fetchSemanticsNode().boundsInRoot
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
        assertTrue(
            entering.left > onNodeWithText("DataResult").fetchSemanticsNode().boundsInRoot.left
        )
        runOnIdle { navigation.navigateTo(ToolkitSampleRoute(ToolkitLibrary.ANDROID)) }
        onNodeWithContentDescription("Back to catalogue").assertIsDisplayed().performClick()
        runOnIdle { assertEquals(ToolkitRoute, navigation.currentRoute) }
        onNodeWithContentDescription(
            "Open sample: Arch Toolkit"
        ).performScrollTo().assertIsDisplayed()
        assertTabFade(navigation, bounds)
    }

    private fun ComposeUiTest.assertTabFade(navigation: NavigationController, chromeBounds: Rect) {
        mainClock.autoAdvance = false
        onNode(hasText("Design") and hasClickAction()).performClick()
        mainClock.advanceTimeBy(90)
        assertEquals(
            chromeBounds,
            onNode(hasText("Toolkit") and hasClickAction()).fetchSemanticsNode().boundsInRoot
        )
        val heading = onNode(hasText("Design") and !hasClickAction())
        val midpoint = heading.fetchSemanticsNode().boundsInRoot
        mainClock.autoAdvance = true
        waitForIdle()
        assertEquals(midpoint, heading.fetchSemanticsNode().boundsInRoot)
        onNode(hasText("Design") and hasClickAction()).assertIsSelected()
        runOnIdle { assertEquals(DesignRoute, navigation.currentRoute) }
    }

    @Test
    fun libraryPageScrollKeepsTheToolbarVisibleAboveTheBottomLink() = runComposeUiTest {
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
        onNodeWithContentDescription("Back to catalogue").assertIsDisplayed()
        onAllNodesWithText("Storage").onFirst().assertIsDisplayed()
        onAllNodesWithText("Storage")[1].assertIsNotDisplayed()
        onAllNodesWithText("Storage")[1].performScrollTo().assertIsDisplayed()
    }
}
