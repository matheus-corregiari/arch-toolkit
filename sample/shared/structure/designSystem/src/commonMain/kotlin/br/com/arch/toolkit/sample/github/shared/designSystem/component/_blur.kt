@file:OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class)

package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.core.model.DeviceType
import br.com.arch.toolkit.sample.github.shared.structure.core.model.WindowSize
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@Composable
private fun hazeStyle() = HazeStyle(
    blurRadius = AppTheme.dimen.spacingS,
    backgroundColor = AppTheme.color.backgroundSurfaceDefault,
    fallbackTint = HazeTint(
        brush = Brush.verticalGradient(
            listOf(
                AppTheme.color.backgroundSurfaceDefault,
                AppTheme.color.backgroundSurfaceDefault.copy(
                    alpha = AppTheme.dimen.opacityLevel6
                )
            )
        )
    ),
    tint = HazeTint(
        color = AppTheme.color.backgroundSurfaceDefault.copy(
            alpha = AppTheme.dimen.opacityLevel4
        )
    ),
)

@Composable
fun Modifier.haze(state: HazeState): Modifier {
    val enableBlur = state.blurEnabled
            && AppTheme.screen.size == WindowSize.SMALL
            && AppTheme.screen.type == DeviceType.MOBILE
    return hazeEffect(state = state, style = hazeStyle()) {
        blurEnabled = enableBlur
        progressive = HazeProgressive.verticalGradient(
            startIntensity = if (enableBlur) 1f else 0.98f,
            endIntensity = if (enableBlur) 0f else 0.98f,
        )
    }
}
