package br.com.arch.toolkit.sample.screenshot

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.arch.toolkit.sample.feature.design.DesignContent
import br.com.arch.toolkit.sample.screenshot.ScreenshotEnvironment
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(
    name = "DesignLight",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun DesignLight() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        DesignContent()
    }
}

@PreviewTest
@Preview(
    name = "DesignDarkPortuguese",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun DesignDarkPortuguese() {
    ScreenshotEnvironment(dark = true, portuguese = true, highContrast = false) {
        DesignContent()
    }
}

@PreviewTest
@Preview(
    name = "DesignLargeFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 1.5f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun DesignLargeFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        DesignContent()
    }
}

@PreviewTest
@Preview(
    name = "DesignWide",
    widthDp = 840,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun DesignWide() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        DesignContent()
    }
}

@PreviewTest
@Preview(
    name = "DesignHighContrast",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun DesignHighContrast() {
    ScreenshotEnvironment(dark = true, portuguese = false, highContrast = true) {
        DesignContent()
    }
}

@PreviewTest
@Preview(
    name = "DesignWidgetsSelected",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun DesignWidgetsSelected() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        DesignContent(selected = true, scrollState = rememberScrollState(initial = Int.MAX_VALUE))
    }
}

@PreviewTest
@Preview(
    name = "DesignWidgetsUnselected",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun DesignWidgetsUnselected() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        DesignContent(scrollState = rememberScrollState(initial = Int.MAX_VALUE))
    }
}

@PreviewTest
@Preview(
    name = "DesignMaximumFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 2.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun DesignMaximumFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        DesignContent()
    }
}
