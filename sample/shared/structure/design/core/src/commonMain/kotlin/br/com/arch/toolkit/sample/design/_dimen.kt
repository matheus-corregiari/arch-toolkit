@file:Suppress("MagicNumber")

package br.com.arch.toolkit.sample.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.arch.toolkit.sample.core.model.WindowSize

internal val LocalAppDimen = compositionLocalOf<AppDimen> { SmallScreenDimen }

sealed class AppDimen {

    val contentMaxWidth: Dp = 1120.dp

    val readingMaxWidth: Dp = 760.dp

    val sectionMinWidth: Dp = 360.dp

    val searchFieldMinWidth: Dp = 280.dp

    val searchActionWidth: Dp = 120.dp

    // Border
    open val borderWidthS: Dp = 1.dp
    open val borderWidthM: Dp = 2.dp
    open val borderWidthL: Dp = 4.dp
    open val borderWidthXl: Dp = 8.dp

    // Elevation
    open val elevationLv1Elevation: Dp = 1.dp
    open val elevationLv2Elevation: Dp = 2.dp
    open val elevationLv3Elevation: Dp = 4.dp
    open val elevationLv4Elevation: Dp = 8.dp

    // Semantic text roles for the application.
    open val pageHeadingSize = 24.sp
    open val pageHeadingLeading = 30.sp
    open val sectionHeadingSize = 18.sp
    open val sectionHeadingLeading = 24.sp
    val bodySize = 16.sp
    val bodyLeading = 24.sp
    open val metadataSize = 13.sp
    open val metadataLeading = 18.sp
    val codeLeading = 22.sp

    // Font Line Height
    open val fontLineHeightXs: TextUnit = 10.0.sp
    open val fontLineHeightS: TextUnit = 12.0.sp
    open val fontLineHeightM: TextUnit = 14.0.sp
    open val fontLineHeightL: TextUnit = 16.0.sp
    open val fontLineHeightXl: TextUnit = 18.0.sp
    open val fontLineHeightXxl: TextUnit = 20.sp

    // Font Size
    open val fontSizeXs: TextUnit = 10.sp
    open val fontSizeS: TextUnit = 12.sp
    open val fontSizeM: TextUnit = 14.sp
    open val fontSizeL: TextUnit = 16.sp
    open val fontSizeXl: TextUnit = 18.sp
    open val fontSizeXxl: TextUnit = 20.sp
    open val fontSizeXxxl: TextUnit = 24.sp
    open val fontSizeH: TextUnit = 32.sp
    open val fontSizeG: TextUnit = 40.sp
    open val fontSizeXg: TextUnit = 48.sp
    open val fontSizeXxg: TextUnit = 56.sp

    // Opacity
    open val opacityLevel1: Float = 0.08f
    open val opacityLevel2: Float = 0.16f
    open val opacityLevel3: Float = 0.24f
    open val opacityLevel4: Float = 0.32f
    open val opacityLevel5: Float = 0.64f
    open val opacityLevel6: Float = 0.88f

    // Radius
    open val radiusXs: Dp = 4.dp
    open val radiusS: Dp = 8.dp
    open val radiusM: Dp = 12.dp
    open val radiusL: Dp = 16.dp
    open val radiusXl: Dp = 24.dp
    open val radiusXxl: Dp = 32.dp
    open val radiusPill: Dp = 100.dp

    // Spacing
    open val spacingXxxs: Dp = 2.dp
    open val spacingXxs: Dp = 4.dp
    open val spacingXs: Dp = 8.dp
    open val spacingS: Dp = 12.dp
    open val spacingM: Dp = 16.dp
    open val spacingL: Dp = 20.dp
    open val spacingXl: Dp = 24.dp
    open val spacingXxl: Dp = 32.dp
    open val spacingXxxl: Dp = 40.dp
    open val spacingH: Dp = 48.dp
    open val spacingXh: Dp = 56.dp
    open val spacingG: Dp = 64.dp

    // Spacing
    open val iconS: Dp = 8.dp
    open val iconM: Dp = 16.dp
    open val iconL: Dp = 24.dp
    open val iconXl: Dp = 32.dp

    fun shapes() = Shapes(
        extraSmall = RoundedCornerShape(radiusXs),
        small = RoundedCornerShape(radiusS),
        medium = RoundedCornerShape(radiusM),
        large = RoundedCornerShape(radiusL),
        extraLarge = RoundedCornerShape(radiusXl)
    )

    companion object {
        operator fun invoke(size: WindowSize) = when (size) {
            WindowSize.SMALL -> SmallScreenDimen
            WindowSize.MEDIUM -> MediumScreenDimen
            WindowSize.LARGE -> LargeScreenDimen
        }
    }
}

@Immutable
private data object SmallScreenDimen : AppDimen()

@Immutable
private data object MediumScreenDimen : AppDimen() {
    override val pageHeadingSize = 28.sp
    override val pageHeadingLeading = 34.sp
    override val sectionHeadingSize = 20.sp
    override val sectionHeadingLeading = 26.sp
    override val metadataSize = 14.sp
    override val metadataLeading = 20.sp
    override val fontSizeS = 13.sp
    override val fontSizeM = 15.sp
    override val fontSizeL = 17.sp
    override val fontSizeXl = 20.sp
    override val fontLineHeightL = 18.sp
    override val fontLineHeightM = 22.sp
    override val fontLineHeightXxl = 22.sp
}

@Immutable
private data object LargeScreenDimen : AppDimen() {
    override val pageHeadingSize = 28.sp
    override val pageHeadingLeading = 34.sp
    override val sectionHeadingSize = 20.sp
    override val sectionHeadingLeading = 26.sp
    override val metadataSize = 14.sp
    override val metadataLeading = 20.sp
    override val fontSizeS = 14.sp
    override val fontSizeM = 16.sp
    override val fontSizeL = 18.sp
    override val fontSizeXl = 22.sp
    override val fontLineHeightL = 20.sp
    override val fontLineHeightM = 24.sp
    override val fontLineHeightXxl = 24.sp
}
