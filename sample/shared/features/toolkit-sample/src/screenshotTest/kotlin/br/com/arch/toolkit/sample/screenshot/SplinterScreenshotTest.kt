package br.com.arch.toolkit.sample.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.arch.toolkit.sample.core.model.ContrastMode
import br.com.arch.toolkit.sample.design.component.AppPage
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.feature.toolkit.DemoCode
import br.com.arch.toolkit.sample.feature.toolkit.DemoSource
import br.com.arch.toolkit.sample.feature.toolkit.StorageDemoState
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitContent
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitLibrary
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultSuccess
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(name = "SplinterLight", widthDp = 360, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun SplinterLight() = SplinterPreview(false, false, ContrastMode.STANDARD)

@PreviewTest
@Preview(name = "SplinterDark", widthDp = 360, heightDp = 800, locale = "pt-rBR", apiLevel = 35)
@Composable
fun SplinterDark() = SplinterPreview(true, true, ContrastMode.STANDARD)

@PreviewTest
@Preview(name = "SplinterLightMedium", widthDp = 360, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun SplinterLightMedium() = SplinterPreview(false, false, ContrastMode.MEDIUM)

@PreviewTest
@Preview(
    name = "SplinterDarkMedium",
    widthDp = 360,
    heightDp = 800,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun SplinterDarkMedium() = SplinterPreview(true, true, ContrastMode.MEDIUM)

@PreviewTest
@Preview(name = "SplinterLightHigh", widthDp = 360, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun SplinterLightHigh() = SplinterPreview(false, false, ContrastMode.HIGH)

@PreviewTest
@Preview(name = "SplinterDarkHigh", widthDp = 360, heightDp = 800, locale = "pt-rBR", apiLevel = 35)
@Composable
fun SplinterDarkHigh() = SplinterPreview(true, true, ContrastMode.HIGH)

@PreviewTest
@Preview(name = "SplinterWide", widthDp = 1280, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun SplinterWide() = SplinterPreview(false, false, ContrastMode.STANDARD)

@PreviewTest
@Preview(
    name = "SplinterLargeFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 2f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun SplinterLargeFont() = SplinterPreview(true, true, ContrastMode.HIGH)

@PreviewTest
@Preview(name = "SplinterFailure", widthDp = 360, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun SplinterFailure() {
    ScreenshotEnvironment {
        ToolkitContent(
            emptyList(),
            StorageDemoState(),
            library = ToolkitLibrary.SPLINTER,
            request = dataResultError(IllegalStateException("Demo request failed"))
        )
    }
}

@Composable
@PreviewTest
@Preview(name = "SplinterPolling", widthDp = 360, heightDp = 800, locale = "pt-rBR", apiLevel = 35)
fun SplinterPolling() {
    ScreenshotEnvironment(dark = true, portuguese = true) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(),
            library = ToolkitLibrary.SPLINTER,
            polling = dataResultLoading("2 / 3")
        )
    }
}

@PreviewTest
@Preview(
    name = "SplinterExpandedSource",
    widthDp = 840,
    heightDp = 1200,
    locale = "en",
    apiLevel = 35
)
@Composable
fun SplinterExpandedSource() {
    ScreenshotEnvironment {
        AppPage("Splinter", scrollable = true) {
            AppSection("OneShot") { DemoCode(DemoSource.OneShot, initiallyExpanded = true) }
        }
    }
}

@Composable
private fun SplinterPreview(dark: Boolean, portuguese: Boolean, contrast: ContrastMode) {
    ScreenshotEnvironment(dark = dark, portuguese = portuguese, contrast = contrast) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(),
            library = ToolkitLibrary.SPLINTER,
            request = dataResultLoading("1 / 2"),
            polling = dataResultSuccess("3 / 3")
        )
    }
}
