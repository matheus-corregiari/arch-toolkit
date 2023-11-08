package br.com.arch.toolkit.sample.github.ui.xml.list.withoutPagination

import androidx.lifecycle.ViewModel
import br.com.arch.toolkit.sample.github.data.RepositoryProvider

internal class RepositoryListViewModel : ViewModel() {

    fun listLiveData() = RepositoryProvider.githubRepository.listRepositories()
}
