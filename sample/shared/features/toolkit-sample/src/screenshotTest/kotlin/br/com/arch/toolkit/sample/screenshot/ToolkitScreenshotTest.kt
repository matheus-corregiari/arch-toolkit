package br.com.arch.toolkit.sample.screenshot

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.feature.toolkit.StorageDemoState
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitContent
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
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
        ToolkitContent(
            emptyList(),
            StorageDemoState(),
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 2)
        )
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
            gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 1)
        )
    }
}
