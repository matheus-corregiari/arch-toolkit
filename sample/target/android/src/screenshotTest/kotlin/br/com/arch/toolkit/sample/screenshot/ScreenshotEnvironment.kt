package br.com.arch.toolkit.sample.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.core.model.AppLanguage
import br.com.arch.toolkit.sample.core.model.ContrastMode
import br.com.arch.toolkit.sample.core.model.ThemeMode
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.LocalAppLanguage

@Composable
fun ScreenshotEnvironment(
    dark: Boolean = false,
    portuguese: Boolean = false,
    highContrast: Boolean = false,
    contrast: ContrastMode = if (highContrast) ContrastMode.HIGH else ContrastMode.STANDARD,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppLanguage provides
            if (portuguese) AppLanguage.PORTUGUESE_BRAZIL else AppLanguage.ENGLISH
    ) {
        AppTheme(
            if (dark) ThemeMode.DARK else ThemeMode.LIGHT,
            contrast
        ) {
            Surface(Modifier.fillMaxSize(), content = content)
        }
    }
}
