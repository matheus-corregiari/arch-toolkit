package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.core.model.WindowSize

/** Centered adaptive content shared by every destination, with a stable title hierarchy. */
@Composable
fun AppPage(
    title: String,
    maxWidth: Dp = AppTheme.dimen.contentMaxWidth,
    content: @Composable ColumnScope.() -> Unit
) {
    val maximumPageWidth = maxWidth
    val fontScale = LocalDensity.current.fontScale.coerceAtLeast(1f)
    BoxWithConstraints(Modifier.fillMaxSize().background(AppTheme.color.backgroundSurfaceDefault)) {
        val roomy = AppTheme.screen.windowSize != WindowSize.SMALL || AppTheme.screen.isLandscape
        val minimumGridWidth = AppTheme.dimen.sectionMinWidth * fontScale * GRID_COLUMNS +
            AppTheme.dimen.spacingM * (GRID_COLUMNS + 1)
        val pageWidth = if (roomy) {
            (this.maxWidth * CONTENT_WIDTH_FRACTION)
                .coerceAtLeast(minimumGridWidth.coerceAtMost(this.maxWidth))
                .coerceAtMost(maximumPageWidth)
        } else {
            this.maxWidth.coerceAtMost(maximumPageWidth)
        }
        val pageHeight = if (roomy) {
            (maxHeight - AppTheme.dimen.spacingM).coerceAtLeast(0.dp)
                .coerceAtMost(AppTheme.dimen.contentMaxHeight)
        } else {
            maxHeight
        }
        Column(
            Modifier.align(if (roomy) Alignment.Center else Alignment.TopCenter)
                .width(pageWidth).heightIn(max = pageHeight)
        ) {
            ScreenTitle(Modifier.fillMaxWidth(), title)
            content()
        }
    }
}

private const val CONTENT_WIDTH_FRACTION = 0.92f
private const val GRID_COLUMNS = 2

/** A single surface treatment for settings, demos and repository information. */
@Composable
fun AppSection(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = AppTheme.dimen.shapes().large,
        color = AppTheme.color.backgroundSurfaceSecondary,
        contentColor = AppTheme.color.textParagraph,
        border = BorderStroke(AppTheme.dimen.borderWidthS, AppTheme.color.surfaceOutline)
    ) {
        Column(
            Modifier.padding(AppTheme.dimen.spacingM),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingS)
        ) {
            Text(
                title,
                modifier = Modifier.semantics { heading() },
                style = AppTheme.textStyle.subtitleXBold,
                color = AppTheme.color.textTitle
            )
            description?.let { Text(it, style = AppTheme.textStyle.paragraphM) }
            content()
        }
    }
}
