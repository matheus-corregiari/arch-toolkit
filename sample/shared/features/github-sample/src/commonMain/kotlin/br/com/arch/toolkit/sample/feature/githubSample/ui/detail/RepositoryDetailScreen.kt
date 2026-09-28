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
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubDetailRoute
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubError
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.core.extension.localized
import com.pedrobneto.easy.navigation.core.LocalNavigationController
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RepositoryDetailScreen(route: GithubDetailRoute) {
    val model: DetailViewModel =
        koinViewModel(
            key = "${route.owner}/${route.name}"
        ) { parametersOf(route.owner, route.name) }
    val state by model.state.collectAsState()
    val navigation = LocalNavigationController.current
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(model) { model.load() }
    RepositoryDetailContent(
        state = state,
        onBack = { navigation.safeNavigateUp() },
        onRetry = model::load,
        onOpenRepository = { owner, name -> uriHandler.openUri("https://github.com/$owner/$name") }
    )
}

@Composable
fun RepositoryDetailContent(
    state: GithubDetailState,
    onBack: () -> Unit = {},
    onRetry: () -> Unit = {},
    onOpenRepository: (String, String) -> Unit = { _, _ -> }
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(
            rememberScrollState()
        ).padding(AppTheme.dimen.spacingM),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)
    ) {
        Button(onClick = onBack) { Text(text(AppText.BACK)) }
        if (state.loading) CircularProgressIndicator()
        state.failure?.let { GithubError(it, onRetry) }
        state.item?.let { item ->
            Text(item.fullName, style = AppTheme.textStyle.titleXLRegular)
            Text(item.description ?: text(AppText.NO_DESCRIPTION))
            Text(
                "${text(
                    AppText.STARS
                )}: ${item.stargazersCount.localized(LocalAppLanguage.current)}"
            )
            Text("${text(AppText.FORKS)}: ${item.forksCount.localized(LocalAppLanguage.current)}")
            Text(item.language.orEmpty())
            Text(item.topics.joinToString(" · "))
            Button(
                onClick = {
                    onOpenRepository(item.owner.login, item.name)
                }
            ) {
                Text(text(AppText.OPEN_GITHUB))
            }
        }
    }
}
