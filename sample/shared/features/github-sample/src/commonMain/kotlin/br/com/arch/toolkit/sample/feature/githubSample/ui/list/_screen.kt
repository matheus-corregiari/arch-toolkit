@file:Suppress("FunctionNaming")

package br.com.arch.toolkit.sample.feature.githubSample.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubDetailRoute
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubError
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.model.RepoVO
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.state.ManyListState
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppButton
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppChoiceGroup
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppPage
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppSection
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppTextField
import br.com.arch.toolkit.sample.github.shared.designSystem.component.EmptyState
import br.com.arch.toolkit.sample.github.shared.structure.repository.RecentRepositoryRO
import com.pedrobneto.easy.navigation.core.LocalNavigationController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RepositoryListScreen(viewModel: ListViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val navigation = LocalNavigationController.current
    val recent: RecentViewModel = koinViewModel()
    val history by recent.items.collectAsState(initial = emptyList())
    LaunchedEffect(viewModel) { viewModel.loadIfNeeded() }
    RepositoryListContent(
        state = state,
        history = history,
        onSearch = viewModel::search,
        onLoadMore = viewModel::loadMore,
        onSelect = { owner, name -> navigation.navigateTo(GithubDetailRoute(owner, name)) }
    )
}

@Composable
fun RepositoryListContent(
    state: GithubListState,
    history: List<RecentRepositoryRO> = emptyList(),
    onSearch: (String, String) -> Unit = { _, _ -> },
    onLoadMore: () -> Unit = {},
    onSelect: (String, String) -> Unit = { _, _ -> }
) {
    AppPage(text(AppText.GITHUB)) {
        SearchControls(state, onSearch)
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
                        onSelect(item.owner, item.name)
                    }) { Text("${item.owner}/${item.name}") }
                }
            }
        }
        val items = remember(state.items) { state.items.map(::RepoVO) }
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (items.isNotEmpty()) {
                ManyListState(
                    items,
                    PaddingValues(AppTheme.dimen.spacingM, AppTheme.dimen.spacingXs)
                ) { item ->
                    onSelect(item.owner.login, item.name)
                }.Draw(Modifier.fillMaxSize())
            }
            if (items.isEmpty()) {
                when {
                    state.loading -> CircularProgressIndicator()
                    state.failure != null -> GithubError(state.failure, onLoadMore)
                    else -> EmptyState(
                        Modifier.fillMaxWidth().padding(AppTheme.dimen.spacingXl),
                        text(AppText.EMPTY),
                        text(AppText.SEARCH_HINT)
                    )
                }
            }
        }
        if (items.isNotEmpty()) {
            if (state.loading) {
                CircularProgressIndicator(
                    Modifier.align(Alignment.CenterHorizontally)
                )
            }
            state.failure?.let { GithubError(it, onLoadMore) }
        }
        if (!state.loading && state.failure == null && state.nextPage != null) {
            AppButton(
                text(AppText.LOAD_MORE),
                onLoadMore,
                modifier = Modifier.fillMaxWidth().padding(AppTheme.dimen.spacingM),
                style = AppButton.Style.Secondary
            )
        }
    }
}

@Composable
private fun SearchControls(state: GithubListState, onSearch: (String, String) -> Unit) {
    AppSection(
        text(AppText.SEARCH_ACTION),
        Modifier.padding(horizontal = AppTheme.dimen.spacingM)
    ) {
        AppTextField(
            state.query,
            { onSearch(it, state.language) },
            text(AppText.SEARCH),
            singleLine = true
        )
        AppChoiceGroup(
            listOf("", "Kotlin", "Java"),
            state.language,
            { onSearch(state.query, it) },
            label = { if (it.isEmpty()) text(AppText.ALL_LANGUAGES) else it }
        )
    }
}
