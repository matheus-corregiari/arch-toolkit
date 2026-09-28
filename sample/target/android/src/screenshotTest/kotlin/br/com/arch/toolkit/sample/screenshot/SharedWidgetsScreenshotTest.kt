package br.com.arch.toolkit.sample.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppButton
import br.com.arch.toolkit.sample.github.shared.designSystem.component.EmptyState
import br.com.arch.toolkit.sample.github.shared.designSystem.component.ErrorState
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(name = "english_light", widthDp = 360, heightDp = 760, locale = "en", apiLevel = 35)
@Composable
fun EnglishLight() = SharedWidgets(AppLanguage.ENGLISH, ThemeMode.LIGHT)

@PreviewTest
@Preview(name = "portuguese_dark", widthDp = 360, heightDp = 760, locale = "pt-rBR", apiLevel = 35)
@Composable
fun PortugueseDark() = SharedWidgets(AppLanguage.PORTUGUESE_BRAZIL, ThemeMode.DARK)

@PreviewTest
@Preview(
    name = "large_font",
    widthDp = 360,
    heightDp = 1000,
    fontScale = 1.5f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun PortugueseLargeFont() = SharedWidgets(AppLanguage.PORTUGUESE_BRAZIL, ThemeMode.LIGHT)

@PreviewTest
@Preview(name = "wide_high_contrast", widthDp = 840, heightDp = 760, locale = "en", apiLevel = 35)
@Composable
fun WideHighContrast() = SharedWidgets(AppLanguage.ENGLISH, ThemeMode.DARK, ContrastMode.HIGH)

@Composable
private fun SharedWidgets(
    language: AppLanguage,
    theme: ThemeMode,
    contrast: ContrastMode = ContrastMode.STANDARD
) {
    CompositionLocalProvider(LocalAppLanguage provides language) {
        AppTheme(theme, contrast) {
            Surface {
                Column(
                    Modifier.fillMaxWidth().padding(AppTheme.dimen.spacingM),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)
                ) {
                    Text(text(AppText.DESIGN), style = AppTheme.textStyle.titleXLRegular)
                    AppButton(text(AppText.SAVE), {})
                    AppButton(text(AppText.DISABLED), {}, enabled = false)
                    EmptyState(Modifier.fillMaxWidth(), text(AppText.EMPTY))
                    ErrorState(
                        Modifier.fillMaxWidth(),
                        text(AppText.CONNECTION_ERROR),
                        retryLabel = text(AppText.RETRY),
                        retry = {}
                    )
                }
            }
        }
    }
}
