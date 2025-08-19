package br.com.arch.toolkit.sample.github.shared.structure.core.model

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScreenInfo(
    val width: Dp = 0.dp,
    val height: Dp = 0.dp,
    val size: WindowSize = WindowSize.SMALL,
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val contrast: ContrastMode = ContrastMode.STANDARD,
    val type: DeviceType = DeviceType.MOBILE,
    val navigationSuiteType: NavigationSuiteType = NavigationSuiteType.None,
    val orientation: Orientation = Orientation.LANDSCAPE
) {
    val isLandscape = orientation == Orientation.LANDSCAPE
    val isPortrait = orientation == Orientation.PORTRAIT

    override fun equals(other: Any?) = if (other == null || other !is ScreenInfo) {
        super.equals(other)
    } else {
        size == other.size &&
            theme == other.theme &&
            contrast == other.contrast &&
            type == other.type &&
            navigationSuiteType == other.navigationSuiteType &&
            orientation == other.orientation
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + theme.hashCode()
        result = 31 * result + contrast.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + navigationSuiteType.hashCode()
        result = 31 * result + orientation.hashCode()
        return result
    }
}
