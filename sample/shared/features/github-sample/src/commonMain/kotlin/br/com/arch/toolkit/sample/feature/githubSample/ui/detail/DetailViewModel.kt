package br.com.arch.toolkit.sample.feature.githubSample.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubException
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubFailure
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubRepository
import br.com.arch.toolkit.sample.github.shared.structure.repository.RecentRepository
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.RepoRO
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GithubDetailState(
    val item: RepoRO? = null,
    val loading: Boolean = false,
    val failure: GithubFailure? = null
)

class DetailViewModel(
    private val repository: GithubRepository,
    private val owner: String,
    private val name: String,
    private val recent: RecentRepository
) : ViewModel() {
    private val mutableState = MutableStateFlow(GithubDetailState())
    val state = mutableState.asStateFlow()

    fun load() {
        if (state.value.loading) return
        mutableState.value = state.value.copy(loading = true, failure = null)
        viewModelScope.launch {
            try {
                val item = repository.detail(owner, name)
                mutableState.value = GithubDetailState(item = item)
                try {
                    recent.remember(item.owner.login, item.name)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // History is optional: a local write failure must not hide remote content.
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: GithubException) {
                mutableState.value = GithubDetailState(failure = failure.failure)
            }
        }
    }
}
