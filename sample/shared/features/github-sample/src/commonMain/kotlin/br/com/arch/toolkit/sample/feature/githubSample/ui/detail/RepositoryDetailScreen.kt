package br.com.arch.toolkit.sample.feature.githubSample.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubDetailRoute
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubError
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import com.pedrobneto.easy.navigation.core.LocalNavigationController
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RepositoryDetailScreen(route: GithubDetailRoute) {
    val model: DetailViewModel = koinViewModel(key = "${route.owner}/${route.name}") { parametersOf(route.owner, route.name) }
    val state by model.state.collectAsState()
    val navigation = LocalNavigationController.current
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(model) { model.load() }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(AppTheme.dimen.spacingM),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)
    ) {
        Button(onClick = { navigation.safeNavigateUp() }) { Text(text(AppText.BACK)) }
        if (state.loading) CircularProgressIndicator()
        state.failure?.let { GithubError(it, model::load) }
        state.item?.let { item ->
            Text(item.fullName, style = AppTheme.textStyle.titleXLRegular)
            Text(item.description ?: text(AppText.NO_DESCRIPTION))
            Text("${text(AppText.STARS)}: ${item.stargazersCount}")
            Text("${text(AppText.FORKS)}: ${item.forksCount}")
            Text(item.language.orEmpty())
            Text(item.topics.joinToString(" · "))
            Button(onClick = { uriHandler.openUri("https://github.com/${item.owner.login}/${item.name}") }) {
                Text(text(AppText.OPEN_GITHUB))
            }
        }
    }
}
