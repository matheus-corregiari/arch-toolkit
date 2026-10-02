package br.com.arch.toolkit.sample.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import br.com.arch.toolkit.sample.feature.design.DesignContent
import br.com.arch.toolkit.sample.feature.design.DesignRoute
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.GithubListState
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.RepositoryListContent
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsContent
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsRoute
import br.com.arch.toolkit.sample.feature.toolkit.StorageDemoState
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitContent
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitRoute
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import br.com.arch.toolkit.sample.screenshot.ScreenshotEnvironment
import br.com.arch.toolkit.sample.shared.ui.home.AppHomeContent
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(
    name = "AppShellSettingsLargeWindow",
    widthDp = 1600,
    heightDp = 1000,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellSettingsLargeWindow() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent(currentRoute = SettingsRoute) {
            SettingsContent(
                remember { mutableStateOf(AppLanguage.PORTUGUESE_BRAZIL) },
                remember { mutableStateOf(ThemeMode.LIGHT) },
                remember { mutableStateOf(ContrastMode.STANDARD) }
            )
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellCompact",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun AppShellCompact() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        AppHomeContent { RepositoryListContent(GithubListState(nextPage = null)) }
    }
}

@PreviewTest
@Preview(
    name = "AppShellMedium",
    widthDp = 600,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun AppShellMedium() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        AppHomeContent { RepositoryListContent(GithubListState(nextPage = null)) }
    }
}

@PreviewTest
@Preview(
    name = "AppShellExpanded",
    widthDp = 840,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun AppShellExpanded() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        AppHomeContent { RepositoryListContent(GithubListState(nextPage = null)) }
    }
}

@PreviewTest
@Preview(
    name = "AppShellDarkPortuguese",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellDarkPortuguese() {
    ScreenshotEnvironment(dark = true, portuguese = true, highContrast = false) {
        AppHomeContent { RepositoryListContent(GithubListState(nextPage = null)) }
    }
}

@PreviewTest
@Preview(
    name = "AppShellLargeFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 1.5f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellLargeFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        AppHomeContent { RepositoryListContent(GithubListState(nextPage = null)) }
    }
}

@PreviewTest
@Preview(
    name = "AppShellMaximumFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 2.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellMaximumFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        AppHomeContent { RepositoryListContent(GithubListState(nextPage = null)) }
    }
}

@PreviewTest
@Preview(name = "AppShellLandscape", widthDp = 800, heightDp = 360, locale = "en", apiLevel = 35)
@Composable
fun AppShellLandscape() {
    ScreenshotEnvironment {
        AppHomeContent {
            RepositoryListContent(
                GithubListState(nextPage = null)
            )
        }
    }
}

@PreviewTest
@Preview(name = "AppShellDesign", widthDp = 360, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun AppShellDesign() {
    ScreenshotEnvironment { AppHomeContent(currentRoute = DesignRoute) { DesignContent() } }
}

@PreviewTest
@Preview(name = "AppShellToolkit", widthDp = 360, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun AppShellToolkit() {
    ScreenshotEnvironment {
        AppHomeContent(
            currentRoute = ToolkitRoute
        ) { ToolkitContent(emptyList(), StorageDemoState()) }
    }
}

@PreviewTest
@Preview(name = "AppShellSettings", widthDp = 360, heightDp = 800, locale = "en", apiLevel = 35)
@Composable
fun AppShellSettings() {
    ScreenshotEnvironment {
        AppHomeContent(currentRoute = SettingsRoute) {
            SettingsContent(
                remember {
                    mutableStateOf(AppLanguage.ENGLISH)
                },
                remember {
                    mutableStateOf(ThemeMode.LIGHT)
                },
                remember { mutableStateOf(ContrastMode.STANDARD) }
            )
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellGithubTabletLandscape",
    widthDp = 1280,
    heightDp = 800,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellGithubTabletLandscape() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent {
            RepositoryListContent(GithubListState(nextPage = null))
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellToolkitTabletLandscape",
    widthDp = 1280,
    heightDp = 800,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellToolkitTabletLandscape() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent(currentRoute = ToolkitRoute) {
            ToolkitContent(emptyList(), StorageDemoState())
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellDesignTabletLandscape",
    widthDp = 1280,
    heightDp = 800,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellDesignTabletLandscape() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent(currentRoute = DesignRoute) {
            DesignContent()
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellSettingsTabletLandscape",
    widthDp = 1280,
    heightDp = 800,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellSettingsTabletLandscape() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent(currentRoute = SettingsRoute) {
            SettingsContent(
                remember { mutableStateOf(AppLanguage.PORTUGUESE_BRAZIL) },
                remember { mutableStateOf(ThemeMode.LIGHT) },
                remember { mutableStateOf(ContrastMode.STANDARD) }
            )
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellGithubPhoneLandscape",
    widthDp = 800,
    heightDp = 360,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellGithubPhoneLandscape() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent {
            RepositoryListContent(GithubListState(nextPage = null))
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellToolkitPhoneLandscape",
    widthDp = 800,
    heightDp = 360,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellToolkitPhoneLandscape() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent(currentRoute = ToolkitRoute) {
            ToolkitContent(emptyList(), StorageDemoState())
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellDesignPhoneLandscape",
    widthDp = 800,
    heightDp = 360,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellDesignPhoneLandscape() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent(currentRoute = DesignRoute) {
            DesignContent()
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellSettingsPhoneLandscape",
    widthDp = 800,
    heightDp = 360,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellSettingsPhoneLandscape() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent(currentRoute = SettingsRoute) {
            SettingsContent(
                remember { mutableStateOf(AppLanguage.PORTUGUESE_BRAZIL) },
                remember { mutableStateOf(ThemeMode.LIGHT) },
                remember { mutableStateOf(ContrastMode.STANDARD) }
            )
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellSettingsTabletPortrait",
    widthDp = 800,
    heightDp = 1280,
    fontScale = 1.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellSettingsTabletPortrait() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent(currentRoute = SettingsRoute) {
            SettingsContent(
                remember { mutableStateOf(AppLanguage.PORTUGUESE_BRAZIL) },
                remember { mutableStateOf(ThemeMode.LIGHT) },
                remember { mutableStateOf(ContrastMode.STANDARD) }
            )
        }
    }
}

@PreviewTest
@Preview(
    name = "AppShellToolkitTabletMaximumFont",
    widthDp = 1280,
    heightDp = 800,
    fontScale = 2.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun AppShellToolkitTabletMaximumFont() {
    ScreenshotEnvironment(portuguese = true) {
        AppHomeContent(currentRoute = ToolkitRoute) {
            ToolkitContent(emptyList(), StorageDemoState())
        }
    }
}
