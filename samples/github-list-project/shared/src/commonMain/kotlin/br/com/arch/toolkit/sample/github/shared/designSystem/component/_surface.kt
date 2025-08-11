@file:Suppress(
    "TooManyFunctions",
    "LongParameterList",
    "LongMethod",
    "CyclomaticComplexMethod",
    "DestructuringDeclarationWithTooManyEntries"
)

package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme

enum class StrokeGravity {
    ALL,
    START, END, TOP, BOTTOM,
    TOP_START, TOP_END,
    BOTTOM_START, BOTTOM_END,
    TOP_BOTTOM,
    NONE;
}

private fun DrawScope.layerSize(strokeGravity: StrokeGravity, radiusPx: Float) =
    when (strokeGravity) {
        StrokeGravity.ALL, StrokeGravity.TOP_BOTTOM -> size
        StrokeGravity.TOP, StrokeGravity.BOTTOM -> Size(width = size.width, height = radiusPx)
        StrokeGravity.START, StrokeGravity.END -> Size(width = radiusPx, height = size.height)
        StrokeGravity.NONE -> Size(width = 0f, height = 0f)
        else -> Size(width = radiusPx, height = radiusPx)
    }

@Composable
fun Modifier.containerRadiusM() = layeredBackground(
    radius = AppTheme.dimen.radiusM,
    accentColor = AppTheme.color.stroke8,
    layeredColors = listOf(AppTheme.color.backgroundSurfaceSecondary),
    strokeGravity = StrokeGravity.ALL
)

@Composable
fun Modifier.containerRadiusXs() = background(
    color = AppTheme.color.backgroundSurfaceSecondary,
    shape = RoundedCornerShape(AppTheme.dimen.radiusXs)
).border(
    border = BorderStroke(
        width = AppTheme.dimen.borderWidthS,
        color = AppTheme.color.stroke8
    ),
    shape = RoundedCornerShape(AppTheme.dimen.radiusXs)
)

@Composable
fun Modifier.containerNegativeRadiusM() = layeredBackground(
    radius = AppTheme.dimen.radiusM,
    accentColor = AppTheme.color.textNegative,
    layeredColors = listOf(AppTheme.color.fillNegative8),
    strokeGravity = StrokeGravity.ALL
)

@Composable
fun Modifier.layeredBackground(
    topStartRadius: Dp = 0.dp,
    topEndRadius: Dp = 0.dp,
    bottomStartRadius: Dp = 0.dp,
    bottomEndRadius: Dp = 0.dp,
    strokeGravity: StrokeGravity,
    accentColor: Brush,
    layeredColors: List<Brush>,
) = surfaceWithRadius(
    color = Color.Transparent,
    topStartRadius = topStartRadius,
    topEndRadius = topEndRadius,
    bottomStartRadius = bottomStartRadius,
    bottomEndRadius = bottomEndRadius
).run {
    val (topStartRadiusPx, topEndRadiusPx, bottomStartRadiusPx, bottomEndRadiusPx) =
        LocalDensity.current.run {
            listOf(
                topStartRadius.toPx(),
                topEndRadius.toPx(),
                bottomStartRadius.toPx(),
                bottomEndRadius.toPx()
            )
        }

    val radiusPx = when (strokeGravity) {
        StrokeGravity.TOP, StrokeGravity.TOP_START, StrokeGravity.START -> topStartRadiusPx
        StrokeGravity.TOP_END, StrokeGravity.END -> topEndRadiusPx
        StrokeGravity.BOTTOM, StrokeGravity.BOTTOM_START -> bottomStartRadiusPx
        StrokeGravity.BOTTOM_END -> bottomEndRadiusPx
        else -> 0f
    }

    var result = drawBehind {
        if (strokeGravity == StrokeGravity.ALL) return@drawBehind

        val layerSize = layerSize(strokeGravity, radiusPx)
        val offset = when (strokeGravity) {
            StrokeGravity.END, StrokeGravity.TOP_END -> {
                Offset(x = size.width - layerSize.width, y = 0f)
            }

            StrokeGravity.BOTTOM, StrokeGravity.BOTTOM_START -> {
                Offset(x = 0f, y = size.height - layerSize.height)
            }

            StrokeGravity.BOTTOM_END -> {
                Offset(x = size.width - layerSize.width, y = size.height - layerSize.height)
            }

            else -> Offset(x = 0f, y = 0f)
        }
        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = offset,
                        size = layerSize,
                    ),
                    topLeft = CornerRadius(x = topStartRadiusPx),
                    topRight = CornerRadius(x = topEndRadiusPx),
                    bottomLeft = CornerRadius(x = bottomStartRadiusPx),
                    bottomRight = CornerRadius(x = bottomEndRadiusPx),
                )
            )
        }
        drawPath(path = path, brush = accentColor)
    }

    layeredColors.forEach { secondaryColor ->
        result = result.drawBehind {
            val (layerSize, offset) = when (strokeGravity) {
                // Start
                StrokeGravity.START -> null to Offset(x = 1.dp.toPx(), y = 0f)
                StrokeGravity.TOP_START -> null to Offset(x = 1.dp.toPx(), y = 1.dp.toPx())
                StrokeGravity.BOTTOM_START -> null to Offset(x = 1.dp.toPx(), y = 0f)

                // End
                StrokeGravity.END -> {
                    size.copy(width = size.width - 1.dp.toPx()) to Offset(x = 0f, y = 0f)
                }

                StrokeGravity.TOP_END -> null to Offset(x = 0f, y = 1.dp.toPx())
                StrokeGravity.BOTTOM_END -> {
                    size.copy(
                        height = size.height - 1.dp.toPx(),
                        width = size.width - 1.dp.toPx()
                    ) to Offset(x = 0f, y = 0f)
                }

                // Vertical
                StrokeGravity.TOP -> null to Offset(x = 0f, y = 1.dp.toPx())
                StrokeGravity.BOTTOM -> {
                    size.copy(height = size.height - 1.dp.toPx()) to Offset(x = 0f, y = 0f)
                }

                StrokeGravity.TOP_BOTTOM -> {
                    size.copy(height = size.height - 2.dp.toPx()) to Offset(x = 0f, y = 1.dp.toPx())
                }

                else -> null to Offset(x = 0f, y = 0f)
            }
            val path = Path().apply {
                fillType = PathFillType.EvenOdd
                addRoundRect(
                    RoundRect(
                        rect = Rect(
                            offset = offset,
                            size = layerSize ?: Size(
                                height = size.height - offset.y,
                                width = size.width - offset.x,
                            ),
                        ),
                        topLeft = CornerRadius(x = topStartRadiusPx),
                        topRight = CornerRadius(x = topEndRadiusPx),
                        bottomLeft = CornerRadius(x = bottomStartRadiusPx),
                        bottomRight = CornerRadius(x = bottomEndRadiusPx),
                    )
                )
            }
            drawPath(path = path, brush = secondaryColor)
        }
    }

    if (strokeGravity == StrokeGravity.ALL) {
        result = result
            .border(
                width = AppTheme.dimen.borderWidthS,
                brush = accentColor,
                shape = RoundedCornerShape(
                    topStart = topStartRadius,
                    topEnd = topEndRadius,
                    bottomEnd = bottomStartRadius,
                    bottomStart = bottomEndRadius
                )
            )
    }

    return@run result
}

@Composable
fun Modifier.layeredBackground(
    topRadius: Dp,
    bottomRadius: Dp,
    strokeGravity: StrokeGravity,
    accentColor: Brush,
    layeredColors: List<Brush>,
) = layeredBackground(
    topStartRadius = topRadius,
    topEndRadius = topRadius,
    bottomStartRadius = bottomRadius,
    bottomEndRadius = bottomRadius,
    strokeGravity = strokeGravity,
    accentColor = accentColor,
    layeredColors = layeredColors
)

@Composable
fun Modifier.layeredBackground(
    radius: Dp,
    strokeGravity: StrokeGravity,
    accentColor: Brush,
    layeredColors: List<Brush>,
) = layeredBackground(
    topRadius = radius,
    bottomRadius = radius,
    strokeGravity = strokeGravity,
    accentColor = accentColor,
    layeredColors = layeredColors
)

@Composable
fun Modifier.layeredBackground(
    topStartRadius: Dp = 0.dp,
    topEndRadius: Dp = 0.dp,
    bottomStartRadius: Dp = 0.dp,
    bottomEndRadius: Dp = 0.dp,
    strokeGravity: StrokeGravity,
    accentColor: Color,
    layeredColors: List<Color>,
) = layeredBackground(
    topStartRadius = topStartRadius,
    topEndRadius = topEndRadius,
    bottomStartRadius = bottomStartRadius,
    bottomEndRadius = bottomEndRadius,
    strokeGravity = strokeGravity,
    accentColor = SolidColor(accentColor),
    layeredColors = layeredColors.map(::SolidColor)
)

@Composable
fun Modifier.layeredBackground(
    topRadius: Dp,
    bottomRadius: Dp,
    strokeGravity: StrokeGravity,
    accentColor: Color,
    layeredColors: List<Color>,
) = layeredBackground(
    topStartRadius = topRadius,
    topEndRadius = topRadius,
    bottomStartRadius = bottomRadius,
    bottomEndRadius = bottomRadius,
    strokeGravity = strokeGravity,
    accentColor = accentColor,
    layeredColors = layeredColors
)

@Composable
fun Modifier.layeredBackground(
    radius: Dp,
    strokeGravity: StrokeGravity,
    accentColor: Color,
    layeredColors: List<Color>,
) = layeredBackground(
    radius = radius,
    strokeGravity = strokeGravity,
    accentColor = SolidColor(accentColor),
    layeredColors = layeredColors.map(::SolidColor)
)

@Composable
fun Modifier.surfaceWithRadius(
    color: Brush,
    topStartRadius: Dp = 0.dp,
    topEndRadius: Dp = 0.dp,
    bottomStartRadius: Dp = 0.dp,
    bottomEndRadius: Dp = 0.dp,
): Modifier = clip(
    shape = RoundedCornerShape(
        topStart = topStartRadius,
        topEnd = topEndRadius,
        bottomStart = bottomStartRadius,
        bottomEnd = bottomEndRadius
    )
).background(brush = color)

@Composable
fun Modifier.surfaceWithRadius(
    color: Brush,
    topRadius: Dp,
    bottomRadius: Dp
): Modifier = surfaceWithRadius(
    color = color,
    topStartRadius = topRadius,
    topEndRadius = topRadius,
    bottomStartRadius = bottomRadius,
    bottomEndRadius = bottomRadius
)

@Composable
fun Modifier.surfaceWithRadius(
    color: Color,
    topStartRadius: Dp,
    topEndRadius: Dp,
    bottomStartRadius: Dp,
    bottomEndRadius: Dp,
): Modifier = surfaceWithRadius(
    color = SolidColor(color),
    topStartRadius = topStartRadius,
    topEndRadius = topEndRadius,
    bottomStartRadius = bottomStartRadius,
    bottomEndRadius = bottomEndRadius
)

@Composable
fun Modifier.surfaceWithRadius(color: Color, topRadius: Dp, bottomRadius: Dp): Modifier =
    surfaceWithRadius(color = SolidColor(color), topRadius = topRadius, bottomRadius = bottomRadius)

@Composable
fun Modifier.surfaceWithRadius(color: Brush, radius: Dp): Modifier =
    surfaceWithRadius(color = color, topRadius = radius, bottomRadius = radius)

@Composable
fun Modifier.surfaceWithRadius(color: Color, radius: Dp): Modifier =
    surfaceWithRadius(color = SolidColor(color), radius = radius)
