package br.com.arch.toolkit.sample.shared.ui.home

import androidx.lifecycle.ViewModel
import br.com.arch.toolkit.sample.github.shared.structure.repository.FeatureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val repository: FeatureRepository,
) : ViewModel() {
    val featureFlow = MutableStateFlow(repository.homeComposable).asStateFlow()
    fun itemById(id: String) = featureFlow.value.find { it.id == id } ?: featureFlow.value.first()
}
