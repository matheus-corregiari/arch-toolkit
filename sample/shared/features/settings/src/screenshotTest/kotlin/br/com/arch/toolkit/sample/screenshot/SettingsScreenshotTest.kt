package br.com.arch.toolkit.sample.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsContent
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import br.com.arch.toolkit.sample.screenshot.ScreenshotEnvironment
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(
    name = "SettingsLight",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun SettingsLight() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        SettingsContent(
            remember {
                mutableStateOf(AppLanguage.ENGLISH)
            },
            remember {
                mutableStateOf(
                    ThemeMode.LIGHT
                )
            },
            remember { mutableStateOf(ContrastMode.STANDARD) }
        )
    }
}

@PreviewTest
@Preview(
    name = "SettingsDarkPortuguese",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun SettingsDarkPortuguese() {
    ScreenshotEnvironment(dark = true, portuguese = true, highContrast = false) {
        SettingsContent(
            remember {
                mutableStateOf(AppLanguage.PORTUGUESE_BRAZIL)
            },
            remember {
                mutableStateOf(
                    ThemeMode.DARK
                )
            },
            remember { mutableStateOf(ContrastMode.STANDARD) }
        )
    }
}

@PreviewTest
@Preview(
    name = "SettingsSystem",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun SettingsSystem() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        SettingsContent(
            remember {
                mutableStateOf(AppLanguage.ENGLISH)
            },
            remember {
                mutableStateOf(ThemeMode.SYSTEM)
            },
            remember { mutableStateOf(ContrastMode.STANDARD) }
        )
    }
}

@PreviewTest
@Preview(
    name = "SettingsHighContrast",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun SettingsHighContrast() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = true) {
        SettingsContent(
            remember {
                mutableStateOf(AppLanguage.ENGLISH)
            },
            remember {
                mutableStateOf(
                    ThemeMode.LIGHT
                )
            },
            remember { mutableStateOf(ContrastMode.HIGH) }
        )
    }
}

@PreviewTest
@Preview(
    name = "SettingsLargeFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 1.5f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun SettingsLargeFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        SettingsContent(
            remember {
                mutableStateOf(AppLanguage.PORTUGUESE_BRAZIL)
            },
            remember {
                mutableStateOf(
                    ThemeMode.LIGHT
                )
            },
            remember { mutableStateOf(ContrastMode.STANDARD) }
        )
    }
}

@PreviewTest
@Preview(
    name = "SettingsWide",
    widthDp = 840,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun SettingsWide() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        SettingsContent(
            remember {
                mutableStateOf(AppLanguage.ENGLISH)
            },
            remember {
                mutableStateOf(
                    ThemeMode.LIGHT
                )
            },
            remember { mutableStateOf(ContrastMode.STANDARD) }
        )
    }
}

@PreviewTest
@Preview(
    name = "SettingsMaximumFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 2.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun SettingsMaximumFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        SettingsContent(
            remember {
                mutableStateOf(AppLanguage.PORTUGUESE_BRAZIL)
            },
            remember {
                mutableStateOf(
                    ThemeMode.LIGHT
                )
            },
            remember { mutableStateOf(ContrastMode.STANDARD) }
        )
    }
}
