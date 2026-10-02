package br.com.arch.toolkit.sample.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppPage
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    SettingsContent(viewModel.language(), viewModel.themeMode(), viewModel.contrastMode())
}

@Composable
fun SettingsContent(
    language: MutableState<AppLanguage>,
    theme: MutableState<ThemeMode>,
    contrast: MutableState<ContrastMode>
) {
    AppPage(text(AppText.SETTINGS)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM),
            contentPadding = PaddingValues(AppTheme.dimen.spacingM)
        ) {
            item { ThemeSetting(Modifier, theme) }
            item { ContrastSetting(Modifier, contrast) }
            item { LanguageSetting(Modifier, language) }
        }
    }
}
