@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.sample.github.shared.structure.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual sealed class FeatureContent
class WindowContent(val open: () -> Unit) : FeatureContent()
class AdjustableDoubleContent(
    val mainContent: @Composable (Modifier) -> Unit,
    val secondaryContent: @Composable (Modifier) -> Unit,
)
