@file:Suppress(
    "TooManyFunctions",
    "LongParameterList",
    "LongMethod",
    "CyclomaticComplexMethod",
    "DestructuringDeclarationWithTooManyEntries"
)

package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.core.model.WindowSize

private const val MEDIUM_WIDTH_FRACTION = 0.85f
private const val LARGE_WIDTH_FRACTION = 0.75f

@Composable
fun Modifier.fillAdjustableSize() = when (AppTheme.screen.windowSize) {
    WindowSize.SMALL -> fillMaxSize()
    WindowSize.MEDIUM -> fillMaxHeight().fillMaxWidth(MEDIUM_WIDTH_FRACTION)
    WindowSize.LARGE -> fillMaxHeight().fillMaxWidth(LARGE_WIDTH_FRACTION)
}
