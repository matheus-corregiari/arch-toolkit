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

@Composable
fun Modifier.fillAdjustableSize() = when (AppTheme.screen.size) {
    WindowSize.SMALL -> fillMaxSize()
    WindowSize.MEDIUM -> fillMaxHeight().fillMaxWidth(0.85f)
    WindowSize.LARGE -> fillMaxHeight().fillMaxWidth(0.75f)
}
