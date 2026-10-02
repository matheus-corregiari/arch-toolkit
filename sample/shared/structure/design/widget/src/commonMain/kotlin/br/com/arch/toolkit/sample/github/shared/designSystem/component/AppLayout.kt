package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme

/** Centered adaptive content shared by every destination, with a stable title hierarchy. */
@Composable
fun AppPage(
    title: String,
    maxWidth: Dp = AppTheme.dimen.contentMaxWidth,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        Modifier.fillMaxSize().background(AppTheme.color.backgroundSurfaceDefault),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            Modifier.fillMaxHeight().widthIn(max = maxWidth).fillMaxWidth()
        ) {
            ScreenTitle(Modifier.fillMaxWidth(), title)
            content()
        }
    }
}

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
