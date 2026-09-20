@file:Suppress("FunctionNaming")

package br.com.arch.toolkit.sample.feature.settings.ui

import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import arch_toolkit.sample.shared.features.settings.generated.resources.Res
import arch_toolkit.sample.shared.features.settings.generated.resources.sample_settings_contrast
import arch_toolkit.sample.shared.features.settings.generated.resources.sample_settings_theme
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import org.jetbrains.compose.resources.stringResource
import kotlin.enums.EnumEntries

@Composable
internal fun ThemeSetting(modifier: Modifier, state: MutableState<ThemeMode>) = EnumSetting(
    modifier = modifier,
    name = text(AppText.THEME),
    entries = ThemeMode.entries,
    state = state
)

@Composable
internal fun ContrastSetting(modifier: Modifier, state: MutableState<ContrastMode>) = EnumSetting(
    modifier = modifier,
    name = text(AppText.CONTRAST),
    entries = ContrastMode.entries,
    state = state
)

@Composable
private inline fun <reified T : Enum<T>> EnumSetting(
    modifier: Modifier,
    name: String,
    entries: EnumEntries<T>,
    state: MutableState<T>
) {
    var savedEnum: T by state

    Column(
        modifier = modifier.padding(
            horizontal = AppTheme.dimen.spacingM,
            vertical = AppTheme.dimen.spacingS
        )
    ) {
        Text(
            name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.size(AppTheme.dimen.spacingXs))
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            entries.forEachIndexed { index, entry ->
                SegmentedButton(
                    selected = entry == savedEnum,
                    onClick = { savedEnum = entry },
                    colors = SegmentedButtonColors(
                        activeContainerColor = AppTheme.color.backgroundSurfaceTertiary,
                        activeContentColor = AppTheme.color.textTitle,
                        activeBorderColor = AppTheme.color.stroke16,
                        inactiveContainerColor = Color.Unspecified,
                        inactiveContentColor = AppTheme.color.textParagraph,
                        inactiveBorderColor = AppTheme.color.stroke8,
                        disabledActiveContainerColor = AppTheme.color.backgroundSurfaceTertiary
                            .copy(alpha = AppTheme.dimen.opacityLevel4),
                        disabledActiveContentColor = AppTheme.color.textDisabled,
                        disabledActiveBorderColor = AppTheme.color.stroke8,
                        disabledInactiveContainerColor = Color.Unspecified,
                        disabledInactiveContentColor = AppTheme.color.textDisabled,
                        disabledInactiveBorderColor = AppTheme.color.stroke8
                    ),
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = entries.size)
                ) {
                    Text(
                        text = when (entry.name) {
                            "ENGLISH" -> "English"
                            "PORTUGUESE_BRAZIL" -> "Português do Brasil"
                            else -> AppText.valueOf(entry.name).resolve(LocalAppLanguage.current)
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
internal fun LanguageSetting(modifier: Modifier, state: MutableState<AppLanguage>) = EnumSetting(
    modifier = modifier,
    name = text(AppText.APP_LANGUAGE),
    entries = AppLanguage.entries,
    state = state
)
