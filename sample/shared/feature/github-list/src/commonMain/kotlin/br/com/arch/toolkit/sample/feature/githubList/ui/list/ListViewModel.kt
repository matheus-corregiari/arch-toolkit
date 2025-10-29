package br.com.arch.toolkit.sample.feature.githubList.ui.list

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.com.arch.toolkit.compose.ComposableDataResult
import br.com.arch.toolkit.compose.composable
import br.com.arch.toolkit.sample.feature.githubList.ui.list.model.RepoVO
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubRepository
import br.com.arch.toolkit.stateHandle.saveResponseState
import kotlinx.coroutines.flow.map

class ListViewModel(
    private val repository: GithubRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val lastPageState by state.saveResponseState<List<RepoVO>>()

    @get:Composable
    val stateList: ComposableDataResult<List<RepoVO>>
        get() = lastPageState.flow().composable

    fun loadRepositories() {
        lastPageState.load {
            repository.lisRepositories().map { result ->
                result.transform { page -> page.items.map(::RepoVO) }
            }
        }
    }

    fun reload() = loadRepositories()
}
