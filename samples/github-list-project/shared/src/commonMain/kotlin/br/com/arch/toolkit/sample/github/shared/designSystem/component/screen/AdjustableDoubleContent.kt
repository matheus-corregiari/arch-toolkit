package br.com.arch.toolkit.sample.github.shared.designSystem.component.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun AdjustableDoubleContent(
    defaultSecondaryContentVisibility: Boolean = false
): Pair<Boolean, (Boolean) -> Unit> {
    val (isSecondaryContentVisible: Boolean, setSecondaryContentVisible: (Boolean) -> Unit) =
        remember { mutableStateOf(defaultSecondaryContentVisibility) }

    return Pair(isSecondaryContentVisible, setSecondaryContentVisible)
}
