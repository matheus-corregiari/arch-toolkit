package br.com.arch.toolkit.sample.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import br.com.arch.toolkit.sample.core.model.AppLanguage
import br.com.arch.toolkit.sample.core.model.ContrastMode
import br.com.arch.toolkit.sample.core.model.ThemeMode
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.design.component.AppChoiceGroup
import br.com.arch.toolkit.sample.design.text
import kotlin.enums.EnumEntries

@Composable
internal fun ThemeSetting(value: ThemeMode, onChange: (ThemeMode) -> Unit) = EnumSetting(
    Modifier,
    text(AppText.THEME),
    text(AppText.THEME_DESCRIPTION),
    ThemeMode.entries,
    value,
    onChange
)

@Composable
internal fun ContrastSetting(value: ContrastMode, onChange: (ContrastMode) -> Unit) = EnumSetting(
    Modifier,
    text(AppText.CONTRAST),
    text(AppText.CONTRAST_DESCRIPTION),
    ContrastMode.entries,
    value,
    onChange
)

@Composable
internal fun LanguageSetting(value: AppLanguage, onChange: (AppLanguage) -> Unit) = EnumSetting(
    Modifier,
    text(AppText.APP_LANGUAGE),
    null,
    AppLanguage.entries,
    value,
    onChange
)

@Composable
private fun <T : Enum<T>> EnumSetting(
    modifier: Modifier,
    name: String,
    description: String?,
    entries: EnumEntries<T>,
    value: T,
    onChange: (T) -> Unit
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs)) {
        Text(
            name,
            modifier = Modifier.semantics {
                heading()
            },
            style = AppTheme.textStyle.sectionHeading,
            color = AppTheme.color.textTitle
        )
        description?.let {
            Text(
                it,
                style = AppTheme.textStyle.body,
                color = AppTheme.color.textParagraph
            )
        }
        AppChoiceGroup(entries, value, onChange, label = { entry ->
            when (entry.name) {
                "ENGLISH" -> "English"
                "PORTUGUESE_BRAZIL" -> "Português do Brasil"
                else -> AppText.valueOf(entry.name).resolve(LocalAppLanguage.current)
            }
        })
    }
}
