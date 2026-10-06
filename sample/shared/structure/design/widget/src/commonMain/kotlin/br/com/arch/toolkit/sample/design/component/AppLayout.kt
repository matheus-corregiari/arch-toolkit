package br.com.arch.toolkit.sample.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.core.model.WindowSize
import br.com.arch.toolkit.sample.design.AppTheme

/** Fill the usable height and center horizontally; children decide their own columns. */
@Composable
fun AppPage(
    title: String,
    maxWidth: Dp = AppTheme.dimen.contentMaxWidth,
    description: String? = null,
    scrollable: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val limit = maxWidth
    BoxWithConstraints(Modifier.fillMaxSize().background(AppTheme.color.backgroundSurfaceDefault)) {
        val gutter = when (AppTheme.screen.windowSize) {
            WindowSize.SMALL -> AppTheme.dimen.spacingM
            WindowSize.MEDIUM -> AppTheme.dimen.spacingXl
            WindowSize.LARGE -> AppTheme.dimen.spacingXxl
        }
        val pageDescription = description.takeIf { maxHeight >= CONTEXT_MIN_HEIGHT }
        Column(
            Modifier.align(Alignment.TopCenter).padding(horizontal = gutter)
                .widthIn(max = limit).fillMaxWidth().fillMaxHeight()
                .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(bottom = if (scrollable) AppTheme.dimen.spacingXl else 0.dp),
            verticalArrangement = if (scrollable) {
                Arrangement.spacedBy(
                    AppTheme.dimen.spacingXl
                )
            } else {
                Arrangement.Top
            }
        ) {
            ScreenTitle(
                Modifier.fillMaxWidth(),
                title,
                pageDescription
            )
            content()
        }
    }
}

private val CONTEXT_MIN_HEIGHT = 480.dp

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
            Modifier.padding(AppTheme.dimen.spacingXl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingS)
        ) {
            Text(
                title,
                modifier = Modifier.semantics { heading() },
                style = AppTheme.textStyle.sectionHeading,
                color = AppTheme.color.textTitle
            )
            description?.let { Text(it, style = AppTheme.textStyle.body) }
            content()
        }
    }
}
