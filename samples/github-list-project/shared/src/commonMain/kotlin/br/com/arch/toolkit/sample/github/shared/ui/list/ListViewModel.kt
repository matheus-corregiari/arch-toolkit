package br.com.arch.toolkit.sample.github.shared.ui.list

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arch.toolkit.compose.ComposableDataResult
import br.com.arch.toolkit.compose.composable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.PageDTO
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.RepoDTO
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubRepository
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ListViewModel(
    private val repository: GithubRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private var lastPage: PageDTO?
        get() = state["list"]
        set(value) = state.set("list", value)

    private var job: Job = Job().apply(CompletableJob::complete)
    private val _repositoryList = MutableStateFlow<DataResult<PageDTO>>(dataResultNone())

    @get:Composable
    val stateList: ComposableDataResult<List<RepoDTO>>
        get() = _repositoryList.map { it.transform(PageDTO::items) }.composable

    fun loadRepositories() {
        val statePage = lastPage
        if (statePage == null) {
            if (job.isActive) return
            job = repository.lisRepositories()
                .onEach(_repositoryList::emit)
                .onEach { (data, _, _) -> if (data != null) lastPage = data }
                .launchIn(viewModelScope)
        } else {
            _repositoryList.tryEmit(dataResultSuccess(statePage))
        }
    }

    fun reload() = loadRepositories()
}
