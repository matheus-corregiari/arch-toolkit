@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package br.com.arch.toolkit.sample.github.shared.designSystem

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.unit.Dp
import androidx.window.core.layout.WindowWidthSizeClass
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.DeviceType
import br.com.arch.toolkit.sample.github.shared.structure.core.model.Orientation
import br.com.arch.toolkit.sample.github.shared.structure.core.model.Orientation.LANDSCAPE
import br.com.arch.toolkit.sample.github.shared.structure.core.model.Orientation.PORTRAIT
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ScreenInfo
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.WindowSize
import br.com.arch.toolkit.sample.github.shared.structure.core.rememberDefaultStorage

internal val LocalScreenInfo = compositionLocalOf { ScreenInfo() }

@Composable
internal expect fun screenWidth(): Dp

@Composable
internal expect fun screenHeight(): Dp

@Composable
internal expect fun deviceType(): DeviceType

@Composable
internal fun getCurrentScreenInfo(): State<ScreenInfo> {
    // Window Size
    val screenSize = currentWindowSize()
    val width = remember(screenSize) { screenSize.width }
    val height = remember(screenSize) { screenSize.height }
    val orientation = remember(screenSize) { if (height < width) LANDSCAPE else PORTRAIT }

    // Computed Info
    val widthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val size = remember(widthSizeClass) { widthSizeClass.screenSize() }
    val navigationSuiteType = remember(widthSizeClass, orientation) {
        widthSizeClass.navigationSuiteType(orientation)
    }

    // Saved Enums
    val storage = rememberDefaultStorage()
    val theme by storage.enum("theme", ThemeMode.SYSTEM).state()
    val contrast by storage.enum("contrast", ContrastMode.STANDARD).state()

    // Creating Screen Info
    val info = ScreenInfo(
        size = currentWindowDpSize(),
        windowSize = size,
        type = deviceType(),
        theme = theme,
        contrast = contrast,
        orientation = orientation,
        navigationSuiteType = navigationSuiteType
    )

    // State \o/
    return remember(info) { mutableStateOf(info, structuralEqualityPolicy()) }
}

private fun WindowWidthSizeClass.screenSize() = when (this) {
    WindowWidthSizeClass.COMPACT -> WindowSize.SMALL
    WindowWidthSizeClass.MEDIUM -> WindowSize.MEDIUM
    WindowWidthSizeClass.EXPANDED -> WindowSize.LARGE
    else -> WindowSize.SMALL
}

private fun WindowWidthSizeClass.navigationSuiteType(orientation: Orientation) =
    when (this) {
        WindowWidthSizeClass.COMPACT -> if (orientation == LANDSCAPE) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteType.NavigationBar
        }

        WindowWidthSizeClass.MEDIUM -> NavigationSuiteType.NavigationRail
        WindowWidthSizeClass.EXPANDED -> NavigationSuiteType.NavigationDrawer
        else -> NavigationSuiteType.None
    }
