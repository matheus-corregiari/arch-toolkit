package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import kotlin.math.max

/** Cards use the available content width, keeping readable columns at enlarged font sizes. */
@Composable
fun AppSectionGrid(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    content: LazyGridScope.() -> Unit
) {
    val minimumWidth = AppTheme.dimen.sectionMinWidth * max(1f, LocalDensity.current.fontScale)
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val gutters = AppTheme.dimen.spacingM * (WIDE_COLUMN_COUNT + 1)
        val twoColumnsFit = maxWidth >= minimumWidth * WIDE_COLUMN_COUNT + gutters
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (twoColumnsFit) WIDE_COLUMN_COUNT else 1),
            modifier = Modifier.fillMaxWidth(),
            state = state,
            contentPadding = PaddingValues(AppTheme.dimen.spacingM),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM),
            content = content
        )
    }
}

/** Search stays beside results when both panes fit; short windows can scroll the controls. */
@Composable
fun AppSearchLayout(
    controls: @Composable ColumnScope.() -> Unit,
    results: @Composable ColumnScope.(sideBySide: Boolean) -> Unit
) {
    val fontScale = max(1f, LocalDensity.current.fontScale)
    val controlScroll = rememberScrollState()
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val controlMaxHeight = maxHeight * CONTROL_HEIGHT_FRACTION
        val paneMaxHeight = maxHeight
        if (maxWidth >= AppTheme.dimen.splitPaneMinWidth * fontScale) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(
                    Modifier.width(AppTheme.dimen.searchPaneWidth * fontScale)
                        .heightIn(max = paneMaxHeight).verticalScroll(controlScroll),
                    content = controls
                )
                Column(Modifier.weight(1f)) { results(true) }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                Column(
                    Modifier.fillMaxWidth().heightIn(max = controlMaxHeight)
                        .verticalScroll(controlScroll),
                    content = controls
                )
                Column(Modifier.weight(1f).fillMaxWidth()) { results(false) }
            }
        }
    }
}

private const val CONTROL_HEIGHT_FRACTION = 0.5f
private const val WIDE_COLUMN_COUNT = 2
