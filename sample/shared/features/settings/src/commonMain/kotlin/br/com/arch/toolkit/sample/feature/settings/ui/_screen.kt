@file:Suppress("FunctionNaming")

package br.com.arch.toolkit.sample.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.ScreenTitle
import br.com.arch.toolkit.sample.github.shared.designSystem.component.containerRadiusM
import br.com.arch.toolkit.sample.github.shared.designSystem.component.fillAdjustableSize
import br.com.arch.toolkit.sample.github.shared.designSystem.component.haze
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
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
    val hazeState = rememberHazeState()
    Scaffold(
        topBar = {
            ScreenTitle(
                modifier = Modifier.fillMaxWidth().haze(hazeState),
                text = text(AppText.SETTINGS)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.hazeSource(hazeState).fillAdjustableSize(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
                start =
                padding.calculateStartPadding(LocalLayoutDirection.current) +
                    AppTheme.dimen.spacingM,
                end =
                padding.calculateEndPadding(LocalLayoutDirection.current) +
                    AppTheme.dimen.spacingM
            )
        ) {
            item { Group(allAppStyleOptions(language, theme, contrast)) }
        }
    }
}

@Composable
private fun Group(items: List<@Composable (modifier: Modifier) -> Unit>) {
    if (items.isEmpty()) return
    Column(modifier = Modifier.containerRadiusM().height(IntrinsicSize.Max)) {
        items.onEachIndexed { index, envContent ->
            envContent(Modifier.weight(1f))
            if (index != items.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = AppTheme.dimen.spacingM),
                    color = AppTheme.color.stroke8,
                    thickness = AppTheme.dimen.borderWidthS
                )
            }
        }
    }
}

private fun allAppStyleOptions(
    language: MutableState<AppLanguage>,
    theme: MutableState<ThemeMode>,
    contrast: MutableState<ContrastMode>
) =
    listOf<@Composable (Modifier) -> Unit>(
        { LanguageSetting(it, language) },
        { ThemeSetting(it, theme) },
        { ContrastSetting(it, contrast) }
    )
