package br.com.arch.toolkit.sample.github.shared.structure.core.model

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.ui.unit.DpSize

data class ScreenInfo(
    val size: DpSize = DpSize.Zero,
    val windowSize: WindowSize = WindowSize.SMALL,
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val contrast: ContrastMode = ContrastMode.STANDARD,
    val type: DeviceType = DeviceType.MOBILE,
    val navigationSuiteType: NavigationSuiteType = NavigationSuiteType.None,
    val orientation: Orientation = Orientation.LANDSCAPE
) {
    val isLandscape = orientation == Orientation.LANDSCAPE
    val isPortrait = orientation == Orientation.PORTRAIT
    val isValid = size != DpSize.Zero

}
