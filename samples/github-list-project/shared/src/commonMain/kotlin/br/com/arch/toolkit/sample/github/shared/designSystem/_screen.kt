package br.com.arch.toolkit.sample.github.shared.designSystem

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.window.core.layout.WindowWidthSizeClass
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.DeviceType
import br.com.arch.toolkit.sample.github.shared.structure.core.model.Orientation.LANDSCAPE
import br.com.arch.toolkit.sample.github.shared.structure.core.model.Orientation.PORTRAIT
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ScreenInfo
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.WindowSize
import br.com.arch.toolkit.sample.github.shared.structure.data.local.dataStoreEnum

internal val LocalScreenInfo = compositionLocalOf { ScreenInfo() }

@Composable
internal expect fun screenWidth(): Dp

@Composable
internal expect fun screenHeight(): Dp

@Composable
internal expect fun deviceType(): DeviceType

@Composable
internal fun getCurrentScreenInfo(): State<ScreenInfo> {
    // Window Size and Device Type
    val width = screenWidth()
    val height = screenHeight()
    val type = deviceType()

    // Computed Info
    val orientation = if (height < width) LANDSCAPE else PORTRAIT
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val widthSizeClass = adaptiveInfo.windowSizeClass.windowWidthSizeClass
    val size = widthSizeClass.screenSize()
    val navigationSuiteType = widthSizeClass.navigationSuiteType(orientation == LANDSCAPE)

    // Saved Enums
    val theme: ThemeMode by dataStoreEnum("theme", ThemeMode.SYSTEM)
    val contrast: ContrastMode by dataStoreEnum("contrast", ContrastMode.STANDARD)

    // Creating Screen Info
    val info = ScreenInfo(
        width = width,
        height = height,
        size = size,
        type = type,
        theme = theme,
        contrast = contrast,
        orientation = orientation,
        navigationSuiteType = navigationSuiteType
    )

    // State \o/
    return mutableStateOf(info)
}

@Composable
private fun WindowWidthSizeClass.screenSize() = when (this) {
    WindowWidthSizeClass.COMPACT -> WindowSize.SMALL
    WindowWidthSizeClass.MEDIUM -> WindowSize.MEDIUM
    WindowWidthSizeClass.EXPANDED -> WindowSize.LARGE
    else -> WindowSize.SMALL
}

@Composable
private fun WindowWidthSizeClass.navigationSuiteType(isLandscape: Boolean) =
    when (this) {
        WindowWidthSizeClass.COMPACT -> if (isLandscape) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteType.NavigationBar
        }

        WindowWidthSizeClass.MEDIUM -> NavigationSuiteType.NavigationRail
        WindowWidthSizeClass.EXPANDED -> NavigationSuiteType.NavigationDrawer
        else -> NavigationSuiteType.None
    }
