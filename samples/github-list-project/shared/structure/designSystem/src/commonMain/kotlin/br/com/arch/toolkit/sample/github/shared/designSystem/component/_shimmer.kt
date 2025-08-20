package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode

private const val MAX_WIDTH = 1000f
private const val INITIAL = MAX_WIDTH * -1f
private const val FINAL = MAX_WIDTH * 2f
private const val RATIO = MAX_WIDTH * 0.75f
private const val DURATION = 1500

@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "ShimmerInfiniteTransition")
    val translateAnim by transition.animateFloat(
        label = "ShimmerFloatAnimation",
        initialValue = INITIAL,
        targetValue = FINAL,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = DURATION,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val (surface, fill) = when (AppTheme.screen.contrast) {
        ContrastMode.STANDARD -> AppTheme.dimen.opacityLevel2 to AppTheme.dimen.opacityLevel3
        ContrastMode.MEDIUM -> AppTheme.dimen.opacityLevel3 to AppTheme.dimen.opacityLevel3
        ContrastMode.HIGH -> AppTheme.dimen.opacityLevel4 to AppTheme.dimen.opacityLevel4
    }
    return Brush.linearGradient(
        colors = listOf(
            AppTheme.color.backgroundSurfaceSecondary.copy(alpha = surface),
            AppTheme.color.fillSecondary.copy(alpha = fill),
            AppTheme.color.backgroundSurfaceSecondary.copy(alpha = surface),
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + RATIO, 0f)
    )
}

@Composable
fun ShimmerRoundedM(modifier: Modifier) = Box(
    modifier = modifier.background(
        brush = shimmerBrush(),
        shape = RoundedCornerShape(AppTheme.dimen.radiusM)
    )
)
