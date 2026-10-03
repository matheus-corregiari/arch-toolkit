package br.com.arch.toolkit.sample.feature.settings.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.core.model.AppLanguage
import br.com.arch.toolkit.sample.core.model.ContrastMode
import br.com.arch.toolkit.sample.core.model.ThemeMode
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.component.AppPage
import br.com.arch.toolkit.sample.design.text
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val language = viewModel.language()
    val theme = viewModel.themeMode()
    val contrast = viewModel.contrastMode()
    SettingsContent(
        language.value,
        theme.value,
        contrast.value,
        onLanguageChange = { language.value = it },
        onThemeChange = { theme.value = it },
        onContrastChange = { contrast.value = it }
    )
}

@Composable
fun SettingsContent(
    language: AppLanguage,
    theme: ThemeMode,
    contrast: ContrastMode,
    onLanguageChange: (AppLanguage) -> Unit = {},
    onThemeChange: (ThemeMode) -> Unit = {},
    onContrastChange: (ContrastMode) -> Unit = {}
) {
    AppPage(
        text(AppText.SETTINGS),
        maxWidth = AppTheme.dimen.readingMaxWidth,
        description = text(AppText.SETTINGS_INTRO)
    ) {
        Column(
            Modifier.verticalScroll(
                rememberScrollState()
            ).padding(bottom = AppTheme.dimen.spacingXl)
        ) {
            Surface(
                Modifier.fillMaxWidth(),
                shape = AppTheme.dimen.shapes().large,
                color = AppTheme.color.backgroundSurfaceSecondary,
                border = BorderStroke(AppTheme.dimen.borderWidthS, AppTheme.color.surfaceOutline)
            ) {
                Column(
                    Modifier.padding(AppTheme.dimen.spacingXl),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXl)
                ) {
                    ThemeSetting(theme, onThemeChange)
                    HorizontalDivider(color = AppTheme.color.surfaceOutline)
                    ContrastSetting(contrast, onContrastChange)
                    HorizontalDivider(color = AppTheme.color.surfaceOutline)
                    LanguageSetting(language, onLanguageChange)
                }
            }
        }
    }
}
