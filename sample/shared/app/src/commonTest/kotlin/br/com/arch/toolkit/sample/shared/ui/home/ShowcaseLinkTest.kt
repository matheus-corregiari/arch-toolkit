package br.com.arch.toolkit.sample.shared.ui.home

import br.com.arch.toolkit.sample.feature.github.ui.GithubDetailRoute
import com.pedrobneto.easy.navigation.core.model.NavigationDeeplink
import com.pedrobneto.easy.navigation.registry.GithubDirectionRegistry
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ShowcaseLinkTest {
    @Test
    fun resolvesIdentityThroughGeneratedFeatureRegistry() {
        val path = showcaseLinkPath("archtoolkit://github/matheus-corregiari/arch-toolkit")!!
        val route = NavigationDeeplink(path).resolve(
            Json { ignoreUnknownKeys = true },
            GithubDirectionRegistry.directions
        )
        assertEquals(GithubDetailRoute("matheus-corregiari", "arch-toolkit"), route)
    }

    @Test
    fun rejectsForeignSchemesAndTraversal() {
        assertNull(showcaseLinkPath("https://example.com/github/a/b"))
        assertNull(showcaseLinkPath("archtoolkit://github/../b"))
        assertEquals("/settings", showcaseLinkPath("/settings"))
    }
}
