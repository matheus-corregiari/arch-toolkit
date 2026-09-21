@file:Suppress("FunctionNaming")

package br.com.arch.toolkit.sample.feature.githubSample.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubDetailRoute
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubError
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.model.RepoVO
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.state.ManyListState
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.EmptyState
import br.com.arch.toolkit.sample.github.shared.designSystem.component.ScreenTitle
import com.pedrobneto.easy.navigation.core.LocalNavigationController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RepositoryListScreen(viewModel: ListViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val navigation = LocalNavigationController.current
    val recent: RecentViewModel = koinViewModel()
    val history by recent.items.collectAsState(initial = emptyList())
    LaunchedEffect(viewModel) { viewModel.loadIfNeeded() }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingS)
    ) {
        ScreenTitle(Modifier.fillMaxWidth(), text(AppText.GITHUB))
        SearchControls(state, viewModel::search)
        if (history.isNotEmpty()) {
            LazyRow {
                item {
                    Text(
                        text(AppText.RECENT),
                        modifier = Modifier.padding(AppTheme.dimen.spacingS)
                    )
                }
                items(history.size) { index ->
                    val item = history[index]
                    TextButton(onClick = {
                        navigation.navigateTo(GithubDetailRoute(item.owner, item.name))
                    }) { Text("${item.owner}/${item.name}") }
                }
            }
        }
        val items = remember(state.items) { state.items.map(::RepoVO) }
        ManyListState(items, PaddingValues(0.dp)) { item ->
            navigation.navigateTo(GithubDetailRoute(item.owner.login, item.name))
        }.Draw(Modifier.weight(1f).fillMaxWidth())
        if (!state.loading &&
            state.failure == null &&
            state.items.isEmpty()
        ) {
            EmptyState(Modifier, text(AppText.EMPTY))
        }
        if (state.loading) CircularProgressIndicator()
        state.failure?.let { GithubError(it, viewModel::loadMore) }
        if (!state.loading && state.failure == null && state.nextPage != null) {
            Button(onClick = viewModel::loadMore) { Text(text(AppText.LOAD_MORE)) }
        }
    }
}

@Composable
private fun SearchControls(state: GithubListState, onSearch: (String, String) -> Unit) {
    OutlinedTextField(
        value = state.query,
        onValueChange = { onSearch(it, state.language) },
        label = { Text(text(AppText.SEARCH)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().padding(horizontal = AppTheme.dimen.spacingM)
    )
    Row(
        Modifier.padding(horizontal = AppTheme.dimen.spacingM),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("", "Kotlin", "Java").forEach { language ->
            FilterChip(
                selected = state.language == language,
                onClick = { onSearch(state.query, language) },
                label = {
                    Text(
                        if (language.isEmpty()) text(AppText.ALL_LANGUAGES) else language
                    )
                }
            )
        }
    }
}
