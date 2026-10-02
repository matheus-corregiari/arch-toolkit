package br.com.arch.toolkit.sample.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import br.com.arch.toolkit.sample.core.model.ContrastMode
import br.com.arch.toolkit.sample.core.model.ScreenInfo
import br.com.arch.toolkit.sample.core.model.ThemeMode

@Immutable
data object AppTheme {
    val screen: ScreenInfo
        @Composable @ReadOnlyComposable
        get() = LocalScreenInfo.current

    val color: AppColor
        @Composable @ReadOnlyComposable
        get() = LocalAppColor.current

    val dimen: AppDimen
        @Composable @ReadOnlyComposable
        get() = LocalAppDimen.current

    val textStyle: AppTextStyle
        @Composable @ReadOnlyComposable
        get() = LocalAppTextStyle.current
}

@Composable
@Suppress("FunctionNaming")
fun AppTheme(
    theme: ThemeMode = ThemeMode.SYSTEM,
    contrast: ContrastMode = ContrastMode.STANDARD,
    content: @Composable () -> Unit
) {
    val screenInfo = getCurrentScreenInfo(theme, contrast)
    if (screenInfo.isValid.not()) return

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val color = remember(screenInfo.theme, screenInfo.contrast, isSystemInDarkTheme) {
        AppColor(screenInfo.theme, screenInfo.contrast, isSystemInDarkTheme)
    }
    val dimen = remember(screenInfo.windowSize) { AppDimen(screenInfo.windowSize) }
    val textStyle = remember(screenInfo.windowSize) { AppTextStyle(screenInfo.windowSize) }

    CompositionLocalProvider(
        LocalScreenInfo provides screenInfo,
        LocalAppColor provides color,
        LocalAppDimen provides dimen,
        LocalAppTextStyle provides textStyle
    ) {
        MaterialTheme(
            colorScheme = color.colorScheme(),
            typography = textStyle.typography(),
            shapes = dimen.shapes(),
            content = content
        )
    }
}
