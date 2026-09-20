package br.com.arch.toolkit.sample.feature.githubSample.ui

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.RepositoryListScreen
import com.pedrobneto.easy.navigation.core.annotation.Deeplink
import com.pedrobneto.easy.navigation.core.annotation.ParentRoute
import com.pedrobneto.easy.navigation.core.annotation.Route
import com.pedrobneto.easy.navigation.core.annotation.Scope
import com.pedrobneto.easy.navigation.core.model.NavigationRoute
import kotlinx.serialization.Serializable

@Serializable
data object GithubRoute : NavigationRoute

@Route(GithubRoute::class)
@Scope("github")
@Deeplink("/github")
@Composable
fun GithubDestination() { RepositoryListScreen() }

@Serializable
data class GithubDetailRoute(val owner: String, val name: String) : NavigationRoute

@Route(GithubDetailRoute::class)
@Scope("github")
@Deeplink("/github/{owner}/{name}")
@ParentRoute(GithubRoute::class)
@Composable
fun GithubDetailDestination(route: GithubDetailRoute) {
    br.com.arch.toolkit.sample.feature.githubSample.ui.detail.RepositoryDetailScreen(route)
}
