package br.com.arch.toolkit.sample.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppChoiceGroup
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppSection
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import kotlin.enums.EnumEntries

@Composable
internal fun ThemeSetting(modifier: Modifier, state: MutableState<ThemeMode>) = EnumSetting(
    modifier,
    text(AppText.THEME),
    text(AppText.THEME_DESCRIPTION),
    ThemeMode.entries,
    state
)

@Composable
internal fun ContrastSetting(modifier: Modifier, state: MutableState<ContrastMode>) = EnumSetting(
    modifier,
    text(AppText.CONTRAST),
    text(AppText.CONTRAST_DESCRIPTION),
    ContrastMode.entries,
    state
)

@Composable
internal fun LanguageSetting(modifier: Modifier, state: MutableState<AppLanguage>) = EnumSetting(
    modifier,
    text(AppText.APP_LANGUAGE),
    null,
    AppLanguage.entries,
    state
)

@Composable
private fun <T : Enum<T>> EnumSetting(
    modifier: Modifier,
    name: String,
    description: String?,
    entries: EnumEntries<T>,
    state: MutableState<T>
) {
    AppSection(name, modifier, description) {
        AppChoiceGroup(entries, state.value, { state.value = it }, label = { entry ->
            when (entry.name) {
                "ENGLISH" -> "English"
                "PORTUGUESE_BRAZIL" -> "Português do Brasil"
                else -> AppText.valueOf(entry.name).resolve(LocalAppLanguage.current)
            }
        })
    }
}
