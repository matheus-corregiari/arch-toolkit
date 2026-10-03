@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package br.com.arch.toolkit.sample.design

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import br.com.arch.toolkit.sample.core.model.ContrastMode
import br.com.arch.toolkit.sample.core.model.DeviceType
import br.com.arch.toolkit.sample.core.model.Orientation
import br.com.arch.toolkit.sample.core.model.Orientation.LANDSCAPE
import br.com.arch.toolkit.sample.core.model.Orientation.PORTRAIT
import br.com.arch.toolkit.sample.core.model.ScreenInfo
import br.com.arch.toolkit.sample.core.model.ThemeMode
import br.com.arch.toolkit.sample.core.model.WindowSize

internal val LocalScreenInfo = compositionLocalOf { ScreenInfo() }

@Composable
internal expect fun screenWidth(): Dp

@Composable
internal expect fun screenHeight(): Dp

@Composable
internal expect fun deviceType(): DeviceType

@Composable
internal fun getCurrentScreenInfo(theme: ThemeMode, contrast: ContrastMode): ScreenInfo {
    // Window Size
    val screenSize = currentWindowSize()
    val width = screenSize.width
    val height = screenSize.height
    val orientation = if (height < width) LANDSCAPE else PORTRAIT

    // Computed Info
    val widthSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val size = widthSizeClass.screenSize()
    val windowDpSize = currentWindowDpSize()
    val navigationSuiteType = remember(widthSizeClass, orientation, windowDpSize) {
        widthSizeClass.navigationSuiteType(orientation, windowDpSize.width)
    }

    // Creating Screen Info
    val info = ScreenInfo(
        size = windowDpSize,
        windowSize = size,
        type = deviceType(),
        theme = theme,
        contrast = contrast,
        orientation = orientation,
        navigationSuiteType = navigationSuiteType
    )

    // State \o/
    return info
}

private fun WindowSizeClass.screenSize() = when {
    isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> WindowSize.LARGE
    isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> WindowSize.MEDIUM
    else -> WindowSize.SMALL
}

private fun WindowSizeClass.navigationSuiteType(orientation: Orientation, width: Dp) =
    when (screenSize()) {
        WindowSize.SMALL -> if (orientation == LANDSCAPE) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteType.NavigationBar
        }
        WindowSize.MEDIUM -> NavigationSuiteType.NavigationRail
        WindowSize.LARGE -> if (width.value >= DRAWER_MIN_WIDTH) {
            NavigationSuiteType.NavigationDrawer
        } else {
            NavigationSuiteType.NavigationRail
        }
    }

private const val DRAWER_MIN_WIDTH = 1200
