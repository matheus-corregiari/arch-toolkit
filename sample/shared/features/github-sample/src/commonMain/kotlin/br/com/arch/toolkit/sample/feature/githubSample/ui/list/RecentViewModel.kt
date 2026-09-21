package br.com.arch.toolkit.sample.feature.githubSample.ui.list

import androidx.lifecycle.ViewModel
import br.com.arch.toolkit.sample.github.shared.structure.repository.RecentRepository

class RecentViewModel(repository: RecentRepository) : ViewModel() {
    val items = repository.items
}
