package br.com.arch.toolkit.sample.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import br.com.arch.toolkit.sample.core.model.DeviceType
import br.com.arch.toolkit.sample.core.model.WindowSize
import br.com.arch.toolkit.sample.design.AppTheme
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurDefaults
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

@Composable
fun Modifier.haze(state: HazeState): Modifier {
    val enableBlur = HazeBlurDefaults.isBlurEnabledByDefault() &&
        AppTheme.screen.windowSize == WindowSize.SMALL &&
        AppTheme.screen.type == DeviceType.MOBILE
    val background = AppTheme.color.backgroundSurfaceDefault
    val radius = AppTheme.dimen.spacingS
    val fallback = HazeColorEffect.tint(
        Brush.verticalGradient(
            listOf(background, background.copy(alpha = AppTheme.dimen.opacityLevel6))
        )
    )
    val tint = HazeColorEffect.tint(background.copy(alpha = AppTheme.dimen.opacityLevel4))
    return hazeBlur(
        input = HazeInput.Sources(state),
        style = HazeBlurStyle {
            blurEnabled(enableBlur)
            blurRadius(radius)
            backgroundColor(background)
            fallbackColorEffect(fallback)
            colorEffects(listOf(tint))
            progressive(
                HazeProgressive.verticalGradient(
                    startIntensity = if (enableBlur) 1f else 0.98f,
                    endIntensity = if (enableBlur) 0f else 0.98f
                )
            )
        }
    )
}
