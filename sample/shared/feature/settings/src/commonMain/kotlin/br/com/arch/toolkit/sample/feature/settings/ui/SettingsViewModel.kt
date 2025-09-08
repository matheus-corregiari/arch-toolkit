package br.com.arch.toolkit.sample.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import br.com.arch.toolkit.sample.github.shared.structure.repository.SettingsRepository

class SettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {
    @Composable
    fun themeMode() = repository.themeMode.state()

    @Composable
    fun contrastMode() = repository.contrastMode.state()
}
