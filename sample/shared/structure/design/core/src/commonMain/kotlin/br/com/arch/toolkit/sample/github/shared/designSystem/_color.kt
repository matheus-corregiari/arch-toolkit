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

    // Brand yellow always carries dark ink, including the light theme.
    val onBrand: Color = Color(0xFF242424)
    abstract val selectedSurface: Color
    abstract val selectedContent: Color
    abstract val controlOutline: Color
    abstract val surfaceOutline: Color

    fun colorScheme(): ColorScheme {
        val base = if (this is DarkColor) darkColorScheme() else lightColorScheme()
        return base.copy(
            primary = if (this is DarkColor) backgroundBrandPrimary else selectedContent,
            onPrimary = if (this is DarkColor) onBrand else componentsFixed,
            primaryContainer = backgroundBrandPrimary,
            onPrimaryContainer = onBrand,
            inversePrimary = backgroundBrandPrimary,
            secondary = textSubtitle,
            onSecondary = backgroundSurfaceDefault,
            secondaryContainer = selectedSurface,
            onSecondaryContainer = selectedContent,
            tertiary = textPositive,
            onTertiary = backgroundSurfaceDefault,
            tertiaryContainer = backgroundPositiveSecondary,
            onTertiaryContainer = textPositive,
            background = backgroundSurfaceDefault,
            onBackground = textTitle,
            surface = backgroundSurfaceSecondary,
            onSurface = textTitle,
            surfaceVariant = backgroundSurfaceTertiary,
            onSurfaceVariant = textParagraph,
            surfaceTint = Color.Transparent,
            surfaceDim = backgroundSurfaceDefault,
            surfaceBright = backgroundSurfaceSecondary,
            surfaceContainerLowest = backgroundSurfaceSecondary,
            surfaceContainerLow = backgroundSurfaceSecondary,
            surfaceContainer = backgroundSurfaceTertiary,
            surfaceContainerHigh = backgroundSurfaceTertiary,
            surfaceContainerHighest = fillSecondary,
            outline = controlOutline,
            outlineVariant = surfaceOutline,
            inverseSurface = backgroundSurfaceInverse,
            inverseOnSurface = textLabelInverse,
            error = textNegative,
            onError = backgroundSurfaceDefault,
            errorContainer = backgroundNegativeSecondary,
            onErrorContainer = textNegative,
            scrim = backgroundOverlay
        )
    }

    companion object {
        @Suppress("CyclomaticComplexMethod")
        operator fun invoke(
            theme: ThemeMode,
            contrast: ContrastMode,
            isSystemInDarkTheme: Boolean
        ): AppColor = when (theme) {
            ThemeMode.DARK -> when (contrast) {
                ContrastMode.STANDARD -> DarkColor.StandardContrast
                ContrastMode.MEDIUM -> DarkColor.MediumContrast
                ContrastMode.HIGH -> DarkColor.HighContrast
            }

            ThemeMode.SYSTEM if isSystemInDarkTheme -> when (contrast) {
                ContrastMode.STANDARD -> DarkColor.StandardContrast
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
        override val backgroundSurfaceDefault: Color = Color(0xFFF5F5F2)
        override val backgroundSurfaceSecondary: Color = Color(0xFFFFFFFF)
        override val backgroundSurfaceTertiary: Color = Color(0xFFEBEBE6)
        override val textTitle: Color = Color(0xFF242424)
        override val textParagraph: Color = Color(0xFF53534F)
        override val textSubtitle: Color = Color(0xFF454541)
        override val controlOutline: Color = Color(0xFF82827A)
        override val surfaceOutline: Color = Color(0xFFD6D6CE)
        override val selectedSurface: Color = Color(0xFFFFF0B3)
        override val selectedContent: Color = Color(0xFF695000)
        override val fillSecondary: Color = Color(0xFFDCDCD5)
    }

    @Immutable
    data object MediumContrast : LightColor() {
        override val backgroundSurfaceDefault: Color = Color(0xFFF1F1EC)
        override val backgroundSurfaceSecondary: Color = Color(0xFFFFFFFF)
        override val backgroundSurfaceTertiary: Color = Color(0xFFE2E2DA)
        override val textTitle: Color = Color(0xFF181816)
        override val textParagraph: Color = Color(0xFF3F3F3A)
        override val textSubtitle: Color = Color(0xFF34342F)
        override val controlOutline: Color = Color(0xFF64645C)
        override val surfaceOutline: Color = Color(0xFFB3B3A8)
        override val selectedSurface: Color = Color(0xFFFFE482)
        override val selectedContent: Color = Color(0xFF594200)
        override val fillSecondary: Color = Color(0xFFD0D0C5)
    }

    @Immutable
    data object HighContrast : LightColor() {
        override val backgroundSurfaceDefault: Color = Color(0xFFFFFFFF)
        override val backgroundSurfaceSecondary: Color = Color(0xFFFFFFFF)
        override val backgroundSurfaceTertiary: Color = Color(0xFFE7E7DE)
        override val textTitle: Color = Color(0xFF121210)
        override val textParagraph: Color = Color(0xFF242420)
        override val textSubtitle: Color = Color(0xFF1E1E1A)
        override val controlOutline: Color = Color(0xFF48483F)
        override val surfaceOutline: Color = Color(0xFF707066)
        override val selectedSurface: Color = Color(0xFFFFCE2E)
        override val selectedContent: Color = Color(0xFF242424)
        override val fillSecondary: Color = Color(0xFFD6D6CC)
    }

    override val backgroundBrand: Color = Color(0xFF242424)
    override val backgroundInformativePrimary: Color = Color(0xFF0080A8)
    override val backgroundInformativeSecondary: Color = Color(0xFFEDF5F9)
    override val backgroundNegativeSecondary: Color = Color(0xFFFFEBE9)
    override val backgroundOverlay: Color = Color(0x85121212)
    override val backgroundPositivePrimary: Color = Color(0xFF00796C)
    override val backgroundPositiveSecondary: Color = Color(0xFFE9F6F3)
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
    override val buttonSecondaryStrokeEnable: Color get() = controlOutline
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
    override val iconDisabled: Color = Color(0x73000000)
    override val iconNegative: Color = Color(0xFF9F2018)
    override val iconPositive: Color = Color(0xFF246044)
    override val iconPrimary: Color get() = textTitle
    override val iconSecondary: Color get() = textParagraph
    override val iconTertiary: Color = Color(0xFFFFFFFF)
    override val statusActivePositive: Color = Color(0xFF246044)
    override val statusActivePositive16: Color = Color(0x292B7551)
    override val statusActivePositive24: Color = Color(0x3D2B7551)
    override val statusActivePositive8: Color = Color(0x142B7551)
    override val statusActivePositiveOpacity: Color = Color(0x1F00A167)
    override val statusAttention: Color = Color(0xFF71500E)
    override val statusAttention16: Color = Color(0x29926C1D)
    override val statusAttention24: Color = Color(0x3D926C1D)
    override val statusAttention8: Color = Color(0x14926C1D)
    override val statusAttentionOpacity: Color = Color(0x1FFF8946)
    override val statusErrorInactive: Color = Color(0xFF9F2018)
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
    override val supportGreen: Color = Color(0xFF246044)
    override val supportGrey: Color = Color(0xFF242424)
    override val supportOrange: Color = Color(0xFFFF8946)
    override val supportPink: Color = Color(0xFFD30F45)
    override val supportPurple: Color = Color(0xFF884A97)

    override val textDisabled: Color = Color(0x73000000)
    override val textLabelInverse: Color = Color(0xFFFFFFFF)
    override val textLink: Color = Color(0xFF0055B0)
    override val textLinkHighlight: Color = Color(0xFFF4BF00)
    override val textNegative: Color = Color(0xFF9F2018)
    override val textPositive: Color = Color(0xFF246044)
}

private sealed class DarkColor : AppColor() {

    @Immutable
    data object StandardContrast : DarkColor() {
        override val backgroundSurfaceDefault: Color = Color(0xFF141413)
        override val backgroundSurfaceSecondary: Color = Color(0xFF20201D)
        override val backgroundSurfaceTertiary: Color = Color(0xFF2D2D28)
        override val textTitle: Color = Color(0xFFF7F7F0)
        override val textParagraph: Color = Color(0xFFBCBCB1)
        override val textSubtitle: Color = Color(0xFFD4D4C9)
        override val controlOutline: Color = Color(0xFF8C8C7E)
        override val surfaceOutline: Color = Color(0xFF49493F)
        override val selectedSurface: Color = Color(0xFF403616)
        override val selectedContent: Color = Color(0xFFFFDA66)
        override val fillSecondary: Color = Color(0xFF39392F)
    }

    @Immutable
    data object MediumContrast : DarkColor() {
        override val backgroundSurfaceDefault: Color = Color(0xFF10100F)
        override val backgroundSurfaceSecondary: Color = Color(0xFF22221D)
        override val backgroundSurfaceTertiary: Color = Color(0xFF31312A)
        override val textTitle: Color = Color(0xFFFAFAF3)
        override val textParagraph: Color = Color(0xFFD0D0C3)
        override val textSubtitle: Color = Color(0xFFE2E2D5)
        override val controlOutline: Color = Color(0xFFA4A493)
        override val surfaceOutline: Color = Color(0xFF71715E)
        override val selectedSurface: Color = Color(0xFF4A3B0D)
        override val selectedContent: Color = Color(0xFFFFE38B)
        override val fillSecondary: Color = Color(0xFF3D3D31)
    }

    @Immutable
    data object HighContrast : DarkColor() {
        override val backgroundSurfaceDefault: Color = Color(0xFF090908)
        override val backgroundSurfaceSecondary: Color = Color(0xFF1C1C17)
        override val backgroundSurfaceTertiary: Color = Color(0xFF303027)
        override val textTitle: Color = Color(0xFFFFFFFF)
        override val textParagraph: Color = Color(0xFFEEEEDE)
        override val textSubtitle: Color = Color(0xFFF4F4E5)
        override val controlOutline: Color = Color(0xFFC4C4AA)
        override val surfaceOutline: Color = Color(0xFFA5A58A)
        override val selectedSurface: Color = Color(0xFFFFCE2E)
        override val selectedContent: Color = Color(0xFF242424)
        override val fillSecondary: Color = Color(0xFF424234)
    }

    override val backgroundBrand: Color = Color(0xFFFBFBFB)
    override val backgroundInformativePrimary: Color = Color(0xFF006C8E)
    override val backgroundInformativeSecondary: Color = Color(0xFF003B4F)
    override val backgroundNegativeSecondary: Color = Color(0xFF5D0001)
    override val backgroundOverlay: Color = Color(0xE0000000)
    override val backgroundPositivePrimary: Color = Color(0xFF00A090)
    override val backgroundPositiveSecondary: Color = Color(0xFF20352F)
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
    override val buttonSecondaryStrokeEnable: Color get() = controlOutline
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
    override val iconDisabled: Color = Color(0x73FFFFFF)
    override val iconNegative: Color = Color(0xFFFF6961)
    override val iconPositive: Color = Color(0xFF4DCE6E)
    override val iconPrimary: Color get() = textTitle
    override val iconSecondary: Color get() = textParagraph
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
}
