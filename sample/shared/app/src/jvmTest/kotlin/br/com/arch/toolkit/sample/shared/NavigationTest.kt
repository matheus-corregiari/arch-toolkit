package br.com.arch.toolkit.sample.shared

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import br.com.arch.toolkit.sample.feature.github.ui.GithubDetailRoute
import br.com.arch.toolkit.sample.feature.github.ui.GithubRoute
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitLibrary
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitRoute
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitSampleRoute
import com.pedrobneto.easy.navigation.core.NavigationController
import com.pedrobneto.easy.navigation.core.rememberNavigationController
import com.pedrobneto.easy.navigation.registry.GithubDirectionRegistry
import com.pedrobneto.easy.navigation.registry.ToolkitDirectionRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class NavigationTest {
    @Test
    fun libraryLinksSaveTheirTypedRouteAndReturnToCatalogue() = runComposeUiTest {
        lateinit var navigation: NavigationController
        var registry = SaveableStateRegistry(null) { true }
        var visible by mutableStateOf(true)
        setContent {
            if (visible) {
                CompositionLocalProvider(LocalSaveableStateRegistry provides registry) {
                    navigation =
                        rememberNavigationController(ToolkitRoute, listOf(ToolkitDirectionRegistry))
                }
            }
        }
        ToolkitLibrary.entries.forEach { library ->
            runOnIdle {
                assertTrue(navigation.safeNavigateTo("/toolkit/${library.name}"))
                assertEquals(ToolkitSampleRoute(library), navigation.currentRoute)
                assertTrue(navigation.safeNavigateUp())
                assertEquals(ToolkitRoute, navigation.currentRoute)
            }
        }
        runOnIdle { navigation.navigateTo(ToolkitSampleRoute(ToolkitLibrary.STORAGE)) }
        waitForIdle()
        runOnIdle {
            registry = SaveableStateRegistry(registry.performSave()) { true }
            visible = false
        }
        waitForIdle()
        runOnIdle { visible = true }
        waitForIdle()
        runOnIdle {
            assertEquals(ToolkitSampleRoute(ToolkitLibrary.STORAGE), navigation.currentRoute)
            assertTrue(navigation.safeNavigateUp())
            assertEquals(ToolkitRoute, navigation.currentRoute)
        }
    }

    @Test
    fun directLinkBackAndSavedStack() = runComposeUiTest {
        lateinit var navigation: NavigationController
        var registry = SaveableStateRegistry(null) { true }
        var visible by mutableStateOf(true)
        setContent {
            if (visible) {
                CompositionLocalProvider(LocalSaveableStateRegistry provides registry) {
                    navigation = rememberNavigationController(
                        GithubRoute,
                        listOf(GithubDirectionRegistry)
                    )
                }
            }
        }
        val detail = GithubDetailRoute("matheus-corregiari", "arch-toolkit")
        runOnIdle {
            assertTrue(navigation.safeNavigateTo("/github/matheus-corregiari/arch-toolkit"))
            assertEquals(detail, navigation.currentRoute)
        }
        waitForIdle()
        runOnIdle {
            registry = SaveableStateRegistry(registry.performSave()) { true }
            visible = false
        }
        waitForIdle()
        runOnIdle { visible = true }
        waitForIdle()
        runOnIdle {
            assertEquals(detail, navigation.currentRoute)
            assertTrue(navigation.safeNavigateUp())
            assertEquals(GithubRoute, navigation.currentRoute)
        }
    }
}
