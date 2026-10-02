package br.com.arch.toolkit.sample.feature.github.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppChoiceGroup
import br.com.arch.toolkit.sample.design.component.AppDisclosure
import br.com.arch.toolkit.sample.design.component.AppPage
import br.com.arch.toolkit.sample.design.component.AppSearchLayout
import br.com.arch.toolkit.sample.design.component.AppTextField
import br.com.arch.toolkit.sample.design.component.EmptyState
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.feature.github.ui.GithubDetailRoute
import br.com.arch.toolkit.sample.feature.github.ui.GithubError
import br.com.arch.toolkit.sample.feature.github.ui.list.state.RepositoryList
import br.com.arch.toolkit.sample.repository.RecentRepositoryRO
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
        onQueryChange = viewModel::editQuery,
        onLoadMore = viewModel::loadMore,
        onSelect = { owner, name -> navigation.navigateTo(GithubDetailRoute(owner, name)) }
    )
}

@Composable
fun RepositoryListContent(
    state: GithubListState,
    history: List<RecentRepositoryRO> = emptyList(),
    onSearch: (String, String) -> Unit = { _, _ -> },
    onQueryChange: (String) -> Unit = {},
    onLoadMore: () -> Unit = {},
    onSelect: (String, String) -> Unit = { _, _ -> }
) {
    AppPage(text(AppText.REPOSITORIES), description = text(AppText.GITHUB_INTRO)) {
        AppSearchLayout(
            controls = {
                SearchControls(state, onSearch, onQueryChange)
                if (history.isNotEmpty()) {
                    AppDisclosure(text(AppText.SHOW_HISTORY), text(AppText.HIDE_HISTORY)) {
                        history.forEach { item ->
                            TextButton(onClick = { onSelect(item.owner, item.name) }) {
                                Text("${item.owner}/${item.name}")
                            }
                        }
                    }
                }
            }
        ) {
            RepositoryResults(state, onLoadMore, onSelect)
        }
    }
}

@Composable
private fun ColumnScope.RepositoryResults(
    state: GithubListState,
    onLoadMore: () -> Unit,
    onSelect: (String, String) -> Unit
) {
    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        if (state.items.isNotEmpty()) {
            RepositoryList(
                state.items,
                Modifier.fillMaxSize(),
                onSelect = { onSelect(it.owner.login, it.name) }
            ) {
                when (val status = state.loadStatus) {
                    LoadStatus.Loading -> CircularProgressIndicator()
                    is LoadStatus.Failed -> GithubError(status.reason, onLoadMore)
                    LoadStatus.Idle -> if (state.nextPage != null) {
                        AppButton(
                            text(AppText.LOAD_MORE),
                            onLoadMore,
                            modifier = Modifier.fillMaxWidth(),
                            style = AppButton.Style.Secondary
                        )
                    }
                }
            }
        } else {
            Column(
                Modifier.fillMaxWidth().verticalScroll(
                    rememberScrollState()
                ).padding(AppTheme.dimen.spacingM)
            ) {
                when (val status = state.loadStatus) {
                    LoadStatus.Loading -> CircularProgressIndicator()
                    is LoadStatus.Failed -> GithubError(status.reason, onLoadMore)
                    LoadStatus.Idle -> EmptyState(
                        Modifier.fillMaxWidth(),
                        text(AppText.EMPTY),
                        text(AppText.SEARCH_HINT)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchControls(
    state: GithubListState,
    onSearch: (String, String) -> Unit,
    onQueryChange: (String) -> Unit
) {
    val fontScale = LocalDensity.current.fontScale.coerceAtLeast(1f)
    val actionWidth = AppTheme.dimen.searchActionWidth * fontScale
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val inline = maxWidth >= AppTheme.dimen.searchFieldMinWidth * fontScale +
            actionWidth + AppTheme.dimen.spacingM
        Column(
            Modifier.fillMaxWidth().padding(bottom = AppTheme.dimen.spacingM),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs)
        ) {
            if (inline) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchQueryField(state, onSearch, onQueryChange, Modifier.weight(1f))
                    SearchSubmitButton(state, onSearch, Modifier.width(actionWidth))
                }
            } else {
                SearchQueryField(state, onSearch, onQueryChange)
                SearchSubmitButton(state, onSearch)
            }
            AppChoiceGroup(
                listOf("", "Kotlin", "Java"),
                state.language,
                { onSearch(state.editingQuery, it) },
                label = { if (it.isEmpty()) text(AppText.ALL_LANGUAGES) else it }
            )
        }
    }
}

@Composable
private fun SearchQueryField(
    state: GithubListState,
    onSearch: (String, String) -> Unit,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) = AppTextField(
    state.editingQuery,
    onQueryChange,
    text(AppText.SEARCH),
    modifier,
    singleLine = true,
    onSubmit = { onSearch(state.editingQuery, state.language) }
)

@Composable
private fun SearchSubmitButton(
    state: GithubListState,
    onSearch: (String, String) -> Unit,
    modifier: Modifier = Modifier
) = AppButton(
    text(AppText.SEARCH_ACTION),
    { onSearch(state.editingQuery, state.language) },
    modifier = modifier
)
