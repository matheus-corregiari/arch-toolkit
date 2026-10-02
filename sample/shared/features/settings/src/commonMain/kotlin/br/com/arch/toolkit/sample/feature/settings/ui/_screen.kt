package br.com.arch.toolkit.sample.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppPage
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppSectionGrid
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
        AppSectionGrid {
            item(key = "theme") { ThemeSetting(Modifier, theme) }
            item(key = "contrast") { ContrastSetting(Modifier, contrast) }
            item(key = "language") { LanguageSetting(Modifier, language) }
        }
    }
}
