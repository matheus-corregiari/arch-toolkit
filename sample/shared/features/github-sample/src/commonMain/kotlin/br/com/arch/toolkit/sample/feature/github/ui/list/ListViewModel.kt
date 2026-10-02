package br.com.arch.toolkit.sample.feature.github.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arch.toolkit.sample.repository.GithubException
import br.com.arch.toolkit.sample.repository.GithubFailure
import br.com.arch.toolkit.sample.repository.GithubRepository
import br.com.arch.toolkit.sample.repository.model.RepoRO
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

sealed interface LoadStatus {
    data object Idle : LoadStatus
    data object Loading : LoadStatus
    data class Failed(val reason: GithubFailure) : LoadStatus
}

data class GithubListState(
    val query: String = "kotlin",
    val language: String = "",
    val items: List<RepoRO> = emptyList(),
    val nextPage: Int? = 1,
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val editingQuery: String = query
) {
    val loading: Boolean get() = loadStatus == LoadStatus.Loading
    val failure: GithubFailure? get() = (loadStatus as? LoadStatus.Failed)?.reason
}

/** Only durable search data is serialized. Loading/error changes never rewrite rows. */
@Serializable
internal data class SearchSnapshot(
    val query: String = "kotlin",
    val language: String = "",
    val items: List<RepoRO> = emptyList(),
    val nextPage: Int? = 1
) {
    fun state() = GithubListState(query, language, items, nextPage)
}

class ListViewModel(
    private val repository: GithubRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val mutableState = MutableStateFlow(restore())
    val state = mutableState.asStateFlow()
    private var lastSnapshot = snapshot(state.value)
    private var request: Job? = null
    private var pendingSearch: Job? = null
    private var generation = 0

    fun editQuery(query: String) {
        pendingSearch?.cancel()
        request?.cancel()
        generation++
        update(state.value.copy(editingQuery = query, loadStatus = LoadStatus.Idle))
        pendingSearch = viewModelScope.launch {
            delay(SEARCH_DELAY_MILLIS)
            pendingSearch = null
            search(query)
        }
    }

    fun search(query: String, language: String = state.value.language) {
        pendingSearch?.cancel()
        pendingSearch = null
        request?.cancel()
        generation++
        update(GithubListState(query = query, language = language))
        loadMore()
    }

    fun loadIfNeeded() {
        if (state.value.editingQuery != state.value.query) {
            editQuery(state.value.editingQuery)
        } else if (state.value.items.isEmpty() && state.value.failure == null) {
            loadMore()
        }
    }

    fun loadMore() {
        val current = state.value
        val page = current.nextPage ?: return
        if (current.loading || pendingSearch != null) return
        val currentGeneration = generation
        update(current.copy(loadStatus = LoadStatus.Loading))
        request = viewModelScope.launch {
            try {
                val result = repository.search(current.query, current.language, page)
                if (generation != currentGeneration) return@launch
                update(
                    current.copy(
                        items = (current.items + result.items).distinctBy { it.id },
                        nextPage = result.nextPage,
                        loadStatus = LoadStatus.Idle
                    )
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: GithubException) {
                if (generation == currentGeneration) {
                    update(current.copy(loadStatus = LoadStatus.Failed(failure.failure)))
                }
            }
        }
    }

    private fun update(value: GithubListState) {
        mutableState.value = value
        savedState[DRAFT_KEY] = value.editingQuery
        val nextSnapshot = snapshot(value)
        if (nextSnapshot != lastSnapshot) {
            savedState[STATE_KEY] = Json.encodeToString(nextSnapshot)
            lastSnapshot = nextSnapshot
        }
    }

    private fun snapshot(state: GithubListState) =
        SearchSnapshot(state.query, state.language, state.items, state.nextPage)

    private fun restore(): GithubListState {
        val encoded = savedState.get<String>(STATE_KEY) ?: savedState.get<String>(LEGACY_STATE_KEY)
        val restored = try {
            encoded?.let { snapshotJson.decodeFromString<SearchSnapshot>(it).state() }
                ?: GithubListState()
        } catch (_: IllegalArgumentException) {
            GithubListState()
        }
        return restored.copy(editingQuery = savedState.get<String>(DRAFT_KEY) ?: restored.query)
    }

    private companion object {
        const val SEARCH_DELAY_MILLIS = 300L
        const val STATE_KEY = "github-search-v4"
        const val LEGACY_STATE_KEY = "github-search-v3"
        const val DRAFT_KEY = "github-search-draft"
        val snapshotJson = Json { ignoreUnknownKeys = true }
    }
}
