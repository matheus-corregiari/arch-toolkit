package br.com.arch.toolkit.sample.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import br.com.arch.toolkit.sample.design.AppTheme
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
        val gutters = AppTheme.dimen.spacingXl
        val twoColumnsFit = maxWidth >= minimumWidth * WIDE_COLUMN_COUNT + gutters
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (twoColumnsFit) WIDE_COLUMN_COUNT else 1),
            modifier = Modifier.fillMaxWidth(),
            state = state,
            contentPadding = PaddingValues(bottom = AppTheme.dimen.spacingXl),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXl),
            content = content
        )
    }
}

/** Search belongs above results; its controls remain scrollable in a short window. */
@Composable
fun AppSearchLayout(
    controls: @Composable ColumnScope.() -> Unit,
    results: @Composable ColumnScope.() -> Unit
) {
    val controlScroll = rememberScrollState()
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val controlMaxHeight = maxHeight * CONTROL_HEIGHT_FRACTION
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxWidth().heightIn(max = controlMaxHeight)
                    .verticalScroll(controlScroll),
                content = controls
            )
            Column(Modifier.weight(1f).fillMaxWidth(), content = results)
        }
    }
}

private const val CONTROL_HEIGHT_FRACTION = 0.5f
private const val WIDE_COLUMN_COUNT = 2
