package br.com.arch.toolkit.sample.feature.github.ui.list

import androidx.lifecycle.ViewModel
import br.com.arch.toolkit.sample.repository.RecentRepository

class RecentViewModel(repository: RecentRepository) : ViewModel() {
    val items = repository.items
}
