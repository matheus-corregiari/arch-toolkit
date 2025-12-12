@file:Suppress("MagicNumber")

package br.com.arch.toolkit.sample.github.shared.designSystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode

internal val LocalAppColor = compositionLocalOf<AppColor> { LightColor.StandardContrast }

sealed class AppColor {

    abstract val backgroundBrand: Color
    abstract val backgroundInformativePrimary: Color
    abstract val backgroundInformativeSecondary: Color
    abstract val backgroundNegativeSecondary: Color
    abstract val backgroundOverlay: Color
    abstract val backgroundPositivePrimary: Color
    abstract val backgroundPositiveSecondary: Color
    abstract val backgroundSurfaceDefault: Color
    abstract val backgroundSurfaceHover: Color
    abstract val backgroundSurfaceInverse: Color
    abstract val backgroundSurfaceSecondary: Color
    abstract val backgroundSurfaceTertiary: Color
    abstract val backgroundSurfaceTertiaryDisabled: Color
    abstract val buttonBackgroundDisabled: Color
    abstract val buttonDestructiveBackground: Color
    abstract val buttonDestructiveBackgroundHover: Color
    abstract val buttonDestructiveBackgroundPressed: Color
    abstract val buttonPrimarylinkBackground: Color
    abstract val buttonPrimarylinkBackgroundHover: Color
    abstract val buttonSecondaryBackground: Color
    abstract val buttonSecondaryBackgroundHover: Color
    abstract val buttonSecondaryBackgroundPressed: Color
    abstract val buttonSecondaryStrokeDisable: Color
    abstract val buttonSecondaryStrokeEnable: Color
    abstract val buttonSecondarylinkBackgroundHover: Color
    abstract val buttonSecondarylinkBackgroundPressed: Color
    abstract val componentsFixed: Color
    abstract val componentsRipple: Color
    abstract val fillBackgroundCardEnd: Color
    abstract val fillBackgroundCardStart: Color
    abstract val fillLink16: Color
    abstract val fillLink32: Color
    abstract val fillLink8: Color
    abstract val fillNegative16: Color
    abstract val fillNegative32: Color
    abstract val fillNegative8: Color
    abstract val fillNeutral16: Color
    abstract val fillNeutral2: Color
    abstract val fillNeutral24: Color
    abstract val fillNeutral8: Color
    abstract val fillPrimary: Color
    abstract val fillSecondary: Color
    abstract val iconDisabled: Color
    abstract val iconNegative: Color
    abstract val iconPositive: Color
    abstract val iconPrimary: Color
    abstract val iconSecondary: Color
    abstract val iconTertiary: Color
    abstract val statusActivePositive: Color
    abstract val statusActivePositive16: Color
    abstract val statusActivePositive24: Color
    abstract val statusActivePositive8: Color
    abstract val statusActivePositiveOpacity: Color
    abstract val statusAttention: Color
    abstract val statusAttention16: Color
    abstract val statusAttention24: Color
    abstract val statusAttention8: Color
    abstract val statusAttentionOpacity: Color
    abstract val statusErrorInactive: Color
    abstract val statusErrorInactive16: Color
    abstract val statusErrorInactive24: Color
    abstract val statusErrorInactive8: Color
    abstract val statusErrorInactiveOpacity: Color
    abstract val statusIntermediate: Color
    abstract val statusIntermediate16: Color
    abstract val statusIntermediate24: Color
    abstract val statusIntermediate8: Color
    abstract val stroke2: Color
    abstract val stroke4: Color
    abstract val stroke8: Color
    abstract val stroke16: Color
    abstract val stroke32: Color
    abstract val stroke72: Color
    abstract val stroke100: Color
    abstract val supportBlue: Color
    abstract val supportGreen: Color
    abstract val supportGrey: Color
    abstract val supportOrange: Color
    abstract val supportPink: Color
    abstract val supportPurple: Color
    abstract val textDisabled: Color
    abstract val textLabelInverse: Color
    abstract val textLink: Color
    abstract val textLinkHighlight: Color
    abstract val textNegative: Color
    abstract val textParagraph: Color
    abstract val textPositive: Color
    abstract val textSubtitle: Color
    abstract val textTitle: Color
    abstract val buttonPrimaryLinkBackgroundPressed: Color
    abstract val buttonSecondaryLinkBackground: Color

    val backgroundBrandPrimary: Color = Color(0xFFFFCE2E)

    abstract fun colorScheme(): ColorScheme

    companion object {
        @Suppress("CyclomaticComplexMethod")
        operator fun invoke(
            theme: ThemeMode,
            contrast: ContrastMode,
            isSystemInDarkTheme: Boolean
        ): AppColor = when (theme) {

            ThemeMode.DARK -> when (contrast) {
                ContrastMode.STANDARD -> DarkColor.LowContrast
                ContrastMode.MEDIUM -> DarkColor.MediumContrast
                ContrastMode.HIGH -> DarkColor.HighContrast
            }

            ThemeMode.SYSTEM if isSystemInDarkTheme -> when (contrast) {
                ContrastMode.STANDARD -> DarkColor.LowContrast
                ContrastMode.MEDIUM -> DarkColor.MediumContrast
                ContrastMode.HIGH -> DarkColor.HighContrast
            }

            else -> when (contrast) {
                ContrastMode.STANDARD -> LightColor.StandardContrast
                ContrastMode.MEDIUM -> LightColor.MediumContrast
                ContrastMode.HIGH -> LightColor.HighContrast
            }
        }
    }
}

private sealed class LightColor : AppColor() {

    @Immutable
    data object StandardContrast : LightColor() {
        override val backgroundSurfaceSecondary: Color = Color(0x66F2F2F2)
        override val backgroundSurfaceTertiary: Color = Color(0x99FBFBFB)
    }

    @Immutable
    data object MediumContrast : LightColor() {
        override val backgroundSurfaceSecondary: Color = Color(0x99F2F2F2)
        override val backgroundSurfaceTertiary: Color = Color(0xCCFBFBFB)
    }

    @Immutable
    data object HighContrast : LightColor() {
        override val backgroundSurfaceSecondary: Color = Color(0xCCF2F2F2)
        override val backgroundSurfaceTertiary: Color = Color(0xFFFBFBFB)
    }

    override val backgroundBrand: Color = Color(0xFF242424)
    override val backgroundInformativePrimary: Color = Color(0xFF0080A8)
    override val backgroundInformativeSecondary: Color = Color(0xFFEDF5F9)
    override val backgroundNegativeSecondary: Color = Color(0xFFFFEBE9)
    override val backgroundOverlay: Color = Color(0x85121212)
    override val backgroundPositivePrimary: Color = Color(0xFF00796C)
    override val backgroundPositiveSecondary: Color = Color(0xFFE9F6F3)
    override val backgroundSurfaceDefault: Color = Color(0xFFE0E0E0)
    override val backgroundSurfaceHover: Color = Color(0x14242424)
    override val backgroundSurfaceInverse: Color = Color(0xFF303030)
    override val backgroundSurfaceTertiaryDisabled: Color = Color(0x66FBFBFB)
    override val buttonBackgroundDisabled: Color = Color(0x0A000000)
    override val buttonDestructiveBackground: Color = Color(0x1FD70015)
    override val buttonDestructiveBackgroundHover: Color = Color(0x29D70015)
    override val buttonDestructiveBackgroundPressed: Color = Color(0x52D70015)
    override val buttonPrimarylinkBackground: Color = Color(0x140275C9)
    override val buttonPrimarylinkBackgroundHover: Color = Color(0x290275C9)
    override val buttonSecondaryBackground: Color = Color(0x14000000)
    override val buttonSecondaryBackgroundHover: Color = Color(0x29000000)
    override val buttonSecondaryBackgroundPressed: Color = Color(0x52121212)
    override val buttonSecondaryStrokeDisable: Color = Color(0x05000000)
    override val buttonSecondaryStrokeEnable: Color = Color(0x5C000000)
    override val buttonPrimaryLinkBackgroundPressed: Color = Color(0x52121212)
    override val buttonSecondaryLinkBackground: Color = Color(0xFFE0E0E0)
    override val buttonSecondarylinkBackgroundHover: Color = Color(0x05000000)
    override val buttonSecondarylinkBackgroundPressed: Color = Color(0x0A000000)
    override val componentsFixed: Color = Color(0xFFFFFFFF)
    override val componentsRipple: Color = Color(0x52121212)
    override val fillBackgroundCardEnd: Color = Color(0xFFFAFAFA)
    override val fillBackgroundCardStart: Color = Color(0xFFFFFFFF)
    override val fillLink16: Color = Color(0x290065D4)
    override val fillLink32: Color = Color(0x520065D4)
    override val fillLink8: Color = Color(0x140065D4)
    override val fillNegative16: Color = Color(0x29B6140C)
    override val fillNegative32: Color = Color(0x52B6140C)
    override val fillNegative8: Color = Color(0x14B6140C)
    override val fillNeutral16: Color = Color(0x29000000)
    override val fillNeutral2: Color = Color(0x05000000)
    override val fillNeutral24: Color = Color(0x3D000000)
    override val fillNeutral8: Color = Color(0x14000000)
    override val fillPrimary: Color = Color(0xFFC6C6C6)
    override val fillSecondary: Color = Color(0xFFD1D1D1)
    override val iconDisabled: Color = Color(0x73000000)
    override val iconNegative: Color = Color(0xFFB6140C)
    override val iconPositive: Color = Color(0xFF2B7551)
    override val iconPrimary: Color = Color(0xCC000000)
    override val iconSecondary: Color = Color(0xFF000000)
    override val iconTertiary: Color = Color(0xFFFFFFFF)
    override val statusActivePositive: Color = Color(0xFF2B7551)
    override val statusActivePositive16: Color = Color(0x292B7551)
    override val statusActivePositive24: Color = Color(0x3D2B7551)
    override val statusActivePositive8: Color = Color(0x142B7551)
    override val statusActivePositiveOpacity: Color = Color(0x1F00A167)
    override val statusAttention: Color = Color(0xFF926C1D)
    override val statusAttention16: Color = Color(0x29926C1D)
    override val statusAttention24: Color = Color(0x3D926C1D)
    override val statusAttention8: Color = Color(0x14926C1D)
    override val statusAttentionOpacity: Color = Color(0x1FFF8946)
    override val statusErrorInactive: Color = Color(0xFFB6140C)
    override val statusErrorInactive16: Color = Color(0x29B6140C)
    override val statusErrorInactive24: Color = Color(0x3DB6140C)
    override val statusErrorInactive8: Color = Color(0x14B6140C)
    override val statusErrorInactiveOpacity: Color = Color(0x1FD70015)
    override val statusIntermediate: Color = Color(0xFF25738A)
    override val statusIntermediate16: Color = Color(0x2925738A)
    override val statusIntermediate24: Color = Color(0x3D25738A)
    override val statusIntermediate8: Color = Color(0x1425738A)
    override val stroke100: Color = Color(0xFF242424)
    override val stroke16: Color = Color(0x29242424)
    override val stroke2: Color = Color(0x05242424)
    override val stroke32: Color = Color(0x52242424)
    override val stroke4: Color = Color(0x0A242424)
    override val stroke72: Color = Color(0xB8242424)
    override val stroke8: Color = Color(0x14242424)
    override val supportBlue: Color = Color(0xFF25738A)
    override val supportGreen: Color = Color(0xFF2B7551)
    override val supportGrey: Color = Color(0xFF242424)
    override val supportOrange: Color = Color(0xFFFF8946)
    override val supportPink: Color = Color(0xFFD30F45)
    override val supportPurple: Color = Color(0xFF884A97)

    override val textDisabled: Color = Color(0x73000000)
    override val textLabelInverse: Color = Color(0xFFFFFFFF)
    override val textLink: Color = Color(0xFF0065D4)
    override val textLinkHighlight: Color = Color(0xFFF4BF00)
    override val textNegative: Color = Color(0xFFB6140C)
    override val textPositive: Color = Color(0xFF2B7551)
    override val textParagraph: Color = Color(0x99000000)
    override val textSubtitle: Color = Color(0xB3000000)
    override val textTitle: Color = Color(0xFF000000)

    override fun colorScheme() = lightColorScheme().copy(
        primary = backgroundSurfaceDefault,
        primaryContainer = backgroundSurfaceDefault,
        onPrimary = textTitle,
        secondary = backgroundBrandPrimary,
        secondaryContainer = backgroundBrandPrimary,
        onSecondary = textTitle,
        onSurface = textParagraph,
        background = backgroundSurfaceDefault,
        onBackground = backgroundSurfaceDefault,
        error = textNegative,
        onError = textTitle
    )

}

private sealed class DarkColor : AppColor() {

    @Immutable
    data object LowContrast : DarkColor() {
        override val backgroundSurfaceSecondary: Color = Color(0x662c2c2c)
        override val backgroundSurfaceTertiary: Color = Color(0x99393939)
    }

    @Immutable
    data object MediumContrast : DarkColor() {
        override val backgroundSurfaceSecondary: Color = Color(0x992c2c2c)
        override val backgroundSurfaceTertiary: Color = Color(0xCC393939)
    }

    @Immutable
    data object HighContrast : DarkColor() {
        override val backgroundSurfaceSecondary: Color = Color(0xCC2c2c2c)
        override val backgroundSurfaceTertiary: Color = Color(0xe6393939)
    }

    override val backgroundBrand: Color = Color(0xFFFBFBFB)
    override val backgroundInformativePrimary: Color = Color(0xFF006C8E)
    override val backgroundInformativeSecondary: Color = Color(0xFF003B4F)
    override val backgroundNegativeSecondary: Color = Color(0xFF5D0001)
    override val backgroundOverlay: Color = Color(0xE0000000)
    override val backgroundPositivePrimary: Color = Color(0xFF00A090)
    override val backgroundPositiveSecondary: Color = Color(0xFF20352F)
    override val backgroundSurfaceDefault: Color = Color(0xFF121212)
    override val backgroundSurfaceHover: Color = Color(0x14FFFFFF)
    override val backgroundSurfaceInverse: Color = Color(0xFFFBFBFB)
    override val backgroundSurfaceTertiaryDisabled: Color = Color(0x66303030)
    override val buttonBackgroundDisabled: Color = Color(0x0AFFFFFF)
    override val buttonDestructiveBackground: Color = Color(0x1FFF6961)
    override val buttonDestructiveBackgroundHover: Color = Color(0x29FF6961)
    override val buttonDestructiveBackgroundPressed: Color = Color(0x3DFF6961)
    override val buttonPrimarylinkBackground: Color = Color(0x14409CFF)
    override val buttonPrimarylinkBackgroundHover: Color = Color(0x29409CFF)
    override val buttonSecondaryBackground: Color = Color(0x14FFFFFF)
    override val buttonSecondaryBackgroundHover: Color = Color(0x29FFFFFF)
    override val buttonSecondaryBackgroundPressed: Color = Color(0x3DFFFFFF)
    override val buttonSecondaryStrokeDisable: Color = Color(0x05FFFFFF)
    override val buttonSecondaryStrokeEnable: Color = Color(0xB8FFFFFF)
    override val buttonPrimaryLinkBackgroundPressed: Color = Color(0x52F8F8F8)
    override val buttonSecondaryLinkBackground: Color = Color(0xFF121212)
    override val buttonSecondarylinkBackgroundHover: Color = Color(0x05FFFFFF)
    override val buttonSecondarylinkBackgroundPressed: Color = Color(0x0AFFFFFF)
    override val componentsFixed: Color = Color(0xFF121212)
    override val componentsRipple: Color = Color(0x52F8F8F8)
    override val fillBackgroundCardEnd: Color = Color(0x1A404040)
    override val fillBackgroundCardStart: Color = Color(0x24BFBFBF)
    override val fillLink16: Color = Color(0x29409CFF)
    override val fillLink32: Color = Color(0x52409CFF)
    override val fillLink8: Color = Color(0x14409CFF)
    override val fillNegative16: Color = Color(0x29FF6961)
    override val fillNegative32: Color = Color(0x52FF6961)
    override val fillNegative8: Color = Color(0x14FF6961)
    override val fillNeutral16: Color = Color(0x29FFFFFF)
    override val fillNeutral2: Color = Color(0x05FFFFFF)
    override val fillNeutral24: Color = Color(0x3DFFFFFF)
    override val fillNeutral8: Color = Color(0x14FFFFFF)
    override val fillPrimary: Color = Color(0xFF494949)
    override val fillSecondary: Color = Color(0xFF3F3F3F)
    override val iconDisabled: Color = Color(0x73FFFFFF)
    override val iconNegative: Color = Color(0xFFFF6961)
    override val iconPositive: Color = Color(0xFF4DCE6E)
    override val iconPrimary: Color = Color(0xCCFFFFFF)
    override val iconSecondary: Color = Color(0xFFFFFFFF)
    override val iconTertiary: Color = Color(0xFF3A3A3A)
    override val statusActivePositive: Color = Color(0xFF4DCE6E)
    override val statusActivePositive16: Color = Color(0x294DCE6E)
    override val statusActivePositive24: Color = Color(0x3D4DCE6E)
    override val statusActivePositive8: Color = Color(0x144DCE6E)
    override val statusActivePositiveOpacity: Color = Color(0x1F30DB5B)
    override val statusAttention: Color = Color(0xFFFFB704)
    override val statusAttention16: Color = Color(0x29FFB704)
    override val statusAttention24: Color = Color(0x3DFFB704)
    override val statusAttention8: Color = Color(0x14FFB704)
    override val statusAttentionOpacity: Color = Color(0x1FFFB340)
    override val statusErrorInactive: Color = Color(0xFFFF6961)
    override val statusErrorInactive16: Color = Color(0x29FF6961)
    override val statusErrorInactive24: Color = Color(0x3DFF6961)
    override val statusErrorInactive8: Color = Color(0x14FF6961)
    override val statusErrorInactiveOpacity: Color = Color(0x1FFF6961)
    override val statusIntermediate: Color = Color(0xFF6BB9CE)
    override val statusIntermediate16: Color = Color(0x296BB9CE)
    override val statusIntermediate24: Color = Color(0x3D6BB9CE)
    override val statusIntermediate8: Color = Color(0x146BB9CE)
    override val stroke100: Color = Color(0xFFFFFFFF)
    override val stroke16: Color = Color(0x29FFFFFF)
    override val stroke2: Color = Color(0x05FFFFFF)
    override val stroke32: Color = Color(0x52FFFFFF)
    override val stroke4: Color = Color(0x0AFFFFFF)
    override val stroke72: Color = Color(0xB8FFFFFF)
    override val stroke8: Color = Color(0x14FFFFFF)
    override val supportBlue: Color = Color(0xFF6BB9CE)
    override val supportGreen: Color = Color(0xFF4DCE6E)
    override val supportGrey: Color = Color(0xFFFFFFFF)
    override val supportOrange: Color = Color(0xFFFFB340)
    override val supportPink: Color = Color(0xFFFF6482)
    override val supportPurple: Color = Color(0xFFDA8FFF)
    override val textDisabled: Color = Color(0x73FFFFFF)
    override val textLabelInverse: Color = Color(0xFF242424)
    override val textLink: Color = Color(0xFF409CFF)
    override val textLinkHighlight: Color = Color(0xFFFFCE2E)
    override val textNegative: Color = Color(0xFFFF6961)
    override val textPositive: Color = Color(0xFF30DB5B)
    override val textParagraph: Color = Color(0x80ffffff)
    override val textSubtitle: Color = Color(0xB3FFFFFF)
    override val textTitle: Color = Color(0xFFFFFFFF)

    override fun colorScheme() = darkColorScheme().copy(
        primary = backgroundSurfaceDefault,
        primaryContainer = backgroundSurfaceDefault,
        onPrimary = textTitle,
        secondary = backgroundBrandPrimary,
        secondaryContainer = backgroundBrandPrimary,
        onSecondary = textTitle,
        onSurface = textParagraph,
        background = backgroundSurfaceDefault,
        onBackground = backgroundSurfaceDefault,
        error = textNegative,
        onError = textTitle
    )
}
