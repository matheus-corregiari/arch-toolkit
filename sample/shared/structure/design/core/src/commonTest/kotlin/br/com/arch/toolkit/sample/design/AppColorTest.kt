package br.com.arch.toolkit.sample.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import br.com.arch.toolkit.sample.core.model.ContrastMode
import br.com.arch.toolkit.sample.core.model.ThemeMode
import br.com.arch.toolkit.sample.design.AppColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppColorTest {
    @Test
    fun readableTextAndControlsAcrossAllSixPalettes() {
        for (theme in listOf(ThemeMode.LIGHT, ThemeMode.DARK)) {
            for (mode in ContrastMode.entries) {
                val palette = AppColor(theme, mode, false)
                val surfaces = listOf(
                    palette.backgroundSurfaceDefault,
                    palette.backgroundSurfaceSecondary,
                    palette.backgroundSurfaceTertiary
                )
                for (surface in surfaces) {
                    for (text in listOf(
                        palette.textTitle,
                        palette.textParagraph,
                        palette.textSubtitle,
                        palette.textLink,
                        palette.textPositive,
                        palette.textNegative
                    )) {
                        assertContrast(text, surface, MIN_TEXT_CONTRAST, "$theme/$mode text")
                    }
                    assertContrast(
                        palette.controlOutline,
                        surface,
                        MIN_CONTROL_CONTRAST,
                        "$theme/$mode outline"
                    )
                }
                assertActions(palette)
                if (mode != ContrastMode.STANDARD) {
                    surfaces.forEach {
                        assertContrast(
                            palette.textParagraph,
                            it,
                            ENHANCED_TEXT_CONTRAST,
                            "$theme/$mode body"
                        )
                    }
                }
            }
        }
    }

    @Test
    fun systemThemeResolvesToTheCorrespondingPalette() {
        for (mode in ContrastMode.entries) {
            assertEquals(
                AppColor(ThemeMode.DARK, mode, false),
                AppColor(ThemeMode.SYSTEM, mode, true)
            )
            assertEquals(
                AppColor(ThemeMode.LIGHT, mode, false),
                AppColor(ThemeMode.SYSTEM, mode, false)
            )
        }
    }

    private fun assertActions(palette: AppColor) {
        assertContrast(
            palette.onBrand,
            palette.backgroundBrandPrimary,
            ENHANCED_TEXT_CONTRAST,
            "brand ink"
        )
        assertContrast(
            palette.selectedContent,
            palette.selectedSurface,
            MIN_TEXT_CONTRAST,
            "selection"
        )
        assertEquals(palette.textTitle, palette.colorScheme().onSurface)
        val material = palette.colorScheme()
        assertContrast(
            material.onPrimary,
            material.primary,
            MIN_TEXT_CONTRAST,
            "Material primary"
        )
        assertContrast(
            material.primary,
            material.surface,
            MIN_TEXT_CONTRAST,
            "Material action"
        )
        val fixedRoles = listOf(
            material.onPrimaryFixed to material.primaryFixedDim,
            material.onPrimaryFixedVariant to material.primaryFixedDim,
            material.onSecondaryFixed to material.secondaryFixedDim,
            material.onSecondaryFixedVariant to material.secondaryFixedDim,
            material.onTertiaryFixed to material.tertiaryFixedDim,
            material.onTertiaryFixedVariant to material.tertiaryFixedDim
        )
        fixedRoles.forEach { (text, surface) ->
            assertContrast(text, surface, MIN_TEXT_CONTRAST, "Material fixed role")
        }
    }

    private fun assertContrast(
        foreground: Color,
        background: Color,
        minimum: Double,
        role: String
    ) {
        val light = maxOf(foreground.luminance(), background.luminance())
        val dark = minOf(foreground.luminance(), background.luminance())
        val ratio = (light + LUMINANCE_OFFSET) / (dark + LUMINANCE_OFFSET)
        assertTrue(ratio >= minimum, "$role: $ratio < $minimum")
    }

    private companion object {
        const val MIN_TEXT_CONTRAST = 4.5
        const val ENHANCED_TEXT_CONTRAST = 7.0
        const val MIN_CONTROL_CONTRAST = 3.0
        const val LUMINANCE_OFFSET = 0.05
    }
}
