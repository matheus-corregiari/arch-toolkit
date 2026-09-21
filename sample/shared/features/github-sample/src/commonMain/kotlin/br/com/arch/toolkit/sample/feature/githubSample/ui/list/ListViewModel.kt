package br.com.arch.toolkit.sample.feature.githubSample.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubException
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubFailure
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubRepository
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.RepoRO
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GithubListState(
    val query: String = "kotlin",
    val language: String = "",
    val items: List<RepoRO> = emptyList(),
    val nextPage: Int? = 1,
    val loading: Boolean = false,
    val failure: GithubFailure? = null
)

class ListViewModel(
    private val repository: GithubRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val mutableState = MutableStateFlow(restore())
    val state = mutableState.asStateFlow()
    private var request: Job? = null
    private var generation = 0

    fun search(query: String, language: String = state.value.language) {
        request?.cancel()
        generation++
        update(GithubListState(query = query, language = language))
        loadMore()
    }

    fun loadIfNeeded() {
        if (state.value.items.isEmpty() && state.value.failure == null) loadMore()
    }

    fun loadMore() {
        val snapshot = state.value
        val page = snapshot.nextPage ?: return
        if (snapshot.loading) return
        val currentGeneration = generation
        update(snapshot.copy(loading = true, failure = null))
        request = viewModelScope.launch {
            try {
                val result = repository.search(snapshot.query, snapshot.language, page)
                if (generation != currentGeneration) return@launch
                update(
                    snapshot.copy(
                        items = (snapshot.items + result.items).distinctBy { it.id },
                        nextPage = result.nextPage
                    )
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: GithubException) {
                if (generation ==
                    currentGeneration
                ) {
                    update(snapshot.copy(failure = failure.failure))
                }
            }
        }
    }

    private fun update(value: GithubListState) {
        mutableState.value = value
        savedState[STATE_KEY] = Json.encodeToString(value.copy(loading = false))
    }

    private fun restore(): GithubListState {
        val snapshot = savedState.get<String>(STATE_KEY) ?: return GithubListState()
        return try {
            Json.decodeFromString<GithubListState>(snapshot).copy(loading = false)
        } catch (_: IllegalArgumentException) {
            GithubListState()
        }
    }

    private companion object {
        const val STATE_KEY = "github-search-v3"
    }
}
