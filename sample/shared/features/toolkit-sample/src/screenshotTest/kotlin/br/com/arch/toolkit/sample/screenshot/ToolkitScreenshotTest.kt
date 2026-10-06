package br.com.arch.toolkit.sample.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.component.AppPage
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.feature.toolkit.DemoCode
import br.com.arch.toolkit.sample.feature.toolkit.DemoSource
import br.com.arch.toolkit.sample.feature.toolkit.EcosystemContent
import br.com.arch.toolkit.sample.feature.toolkit.StorageDemoState
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitContent
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitLibrary
import br.com.arch.toolkit.sample.screenshot.ScreenshotEnvironment
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(
    name = "ToolkitLogs",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitLogs() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        ToolkitContent(
            listOf("INFO: Showcase log entry", "DEBUG: Fixed sample payload"),
            StorageDemoState()
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitStorageEmpty",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitStorageEmpty() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitStorageSaved",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitStorageSaved() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(value = "Arch Toolkit", message = AppText.SAVED),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitStorageRead",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitStorageRead() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(value = "Arch Toolkit", message = AppText.RESULT),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitStorageDeleted",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitStorageDeleted() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(message = AppText.DELETED),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitStorageInvalidKey",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitStorageInvalidKey() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(message = AppText.VALID_KEY),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitStorageError",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitStorageError() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(message = AppText.STORAGE_ERROR),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitStorageBusy",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitStorageBusy() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(busy = true),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitDarkPortuguese",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun ToolkitDarkPortuguese() {
    ScreenshotEnvironment(dark = true, portuguese = true, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(value = "Arch Toolkit", message = AppText.SAVED),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitLargeFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 1.5f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun ToolkitLargeFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(value = "Arch Toolkit", message = AppText.SAVED),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitWide",
    widthDp = 840,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitWide() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(value = "Arch Toolkit", message = AppText.SAVED),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitCatalogue",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitCatalogue() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        EcosystemContent()
    }
}

@PreviewTest
@Preview(
    name = "ToolkitMaximumFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 2.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun ToolkitMaximumFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        ToolkitContent(
            emptyList(),
            StorageDemoState(value = "Arch Toolkit", message = AppText.SAVED),
            library = ToolkitLibrary.STORAGE
        )
    }
}

@PreviewTest
@Preview(
    name = "ToolkitExpandedSource",
    widthDp = 800,
    heightDp = 800,
    locale = "en",
    apiLevel = 35
)
@Composable
fun ToolkitExpandedSource() {
    ScreenshotEnvironment {
        AppPage("Lumber", scrollable = true, onBack = {}, backLabel = "Back to catalogue") {
            AppSection("Lumber") {
                DemoCode(DemoSource.Lumber, initiallyExpanded = true)
            }
        }
    }
}

@PreviewTest
@Preview(
    name = "EcosystemPhone",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun EcosystemPhone() {
    ScreenshotEnvironment(dark = false, portuguese = false) {
        EcosystemContent()
    }
}

@PreviewTest
@Preview(
    name = "EcosystemDarkPortuguese",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun EcosystemDarkPortuguese() {
    ScreenshotEnvironment(dark = true, portuguese = true) {
        EcosystemContent()
    }
}

@PreviewTest
@Preview(
    name = "EcosystemTablet",
    widthDp = 1280,
    heightDp = 1000,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun EcosystemTablet() {
    ScreenshotEnvironment(dark = false, portuguese = false) {
        EcosystemContent()
    }
}

@PreviewTest
@Preview(
    name = "EcosystemMaximumFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 2.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun EcosystemMaximumFont() {
    ScreenshotEnvironment(dark = false, portuguese = true) {
        EcosystemContent()
    }
}

@PreviewTest
@Preview(name = "EventObserverSample", widthDp = 360, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun EventObserverSample() {
    ScreenshotEnvironment {
        ToolkitContent(emptyList(), StorageDemoState(), library = ToolkitLibrary.OBSERVER)
    }
}

@PreviewTest
@Preview(name = "AndroidSample", widthDp = 360, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun AndroidSample() {
    ScreenshotEnvironment {
        ToolkitContent(emptyList(), StorageDemoState(), library = ToolkitLibrary.ANDROID)
    }
}

@PreviewTest
@Preview(name = "ToolkitLandscape", widthDp = 800, heightDp = 360, locale = "en", apiLevel = 35)
@Composable
fun ToolkitLandscape() {
    ScreenshotEnvironment { EcosystemContent() }
}

@PreviewTest
@Preview(
    name = "ObserverMaximumFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 2.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun ObserverMaximumFont() {
    ScreenshotEnvironment(portuguese = true) {
        ToolkitContent(emptyList(), StorageDemoState(), library = ToolkitLibrary.OBSERVER)
    }
}

@PreviewTest
@Preview(name = "StorageLandscape", widthDp = 800, heightDp = 360, locale = "en", apiLevel = 35)
@Composable
fun StorageLandscape() {
    ScreenshotEnvironment {
        ToolkitContent(emptyList(), StorageDemoState(), library = ToolkitLibrary.STORAGE)
    }
}
