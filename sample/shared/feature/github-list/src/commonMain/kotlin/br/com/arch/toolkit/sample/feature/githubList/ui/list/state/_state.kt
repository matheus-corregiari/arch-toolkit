package br.com.arch.toolkit.sample.feature.githubList.ui.list.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Suppress("FunctionNaming")
internal sealed class ListState {
    @Composable
    abstract fun Draw(modifier: Modifier)
}
