@file:Suppress("FunctionNaming")

package br.com.arch.toolkit.sample.feature.githubList.ui.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import arch_toolkit.sample.shared.feature.github_list.generated.resources.Res
import arch_toolkit.sample.shared.feature.github_list.generated.resources.sample_github_list_title
import br.com.arch.toolkit.sample.github.shared.designSystem.component.ScreenTitle
import br.com.arch.toolkit.sample.github.shared.designSystem.component.fillAdjustableSize
import br.com.arch.toolkit.sample.github.shared.designSystem.component.haze
import br.com.arch.toolkit.sample.feature.githubList.ui.list.state.EmptyListState
import br.com.arch.toolkit.sample.feature.githubList.ui.list.state.ErrorListState
import br.com.arch.toolkit.sample.feature.githubList.ui.list.state.LoadingListState
import br.com.arch.toolkit.sample.feature.githubList.ui.list.state.ManyListState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RepositoryListScreen(
    viewModel: ListViewModel = koinViewModel()
) {
    val hazeState = rememberHazeState()
    Scaffold(
        topBar = {
            ScreenTitle(
                modifier = Modifier.fillMaxWidth().haze(hazeState),
                text = stringResource(Res.string.sample_github_list_title)
            )
        }
    ) { padding ->
        val modifier = Modifier.fillAdjustableSize()
        viewModel.stateList.Unwrap {
            animation { enabled = true }
            OnMany { ManyListState(it, padding).Draw(modifier.hazeSource(hazeState)) }
            OnEmpty { EmptyListState.Draw(modifier.padding(padding)) }
            OnShowLoading { LoadingListState.Draw(modifier.padding(padding)) }
            OnError { error ->
                ErrorListState(error, viewModel::reload).Draw(modifier.padding(padding))
            }
        }
    }
    LaunchedEffect(Unit) { viewModel.loadRepositories() }
}




