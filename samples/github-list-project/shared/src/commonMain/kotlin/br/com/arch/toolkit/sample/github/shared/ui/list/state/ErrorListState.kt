package br.com.arch.toolkit.sample.github.shared.ui.list.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.ErrorState

internal class ErrorListState(private val error: Throwable, private val retry: () -> Unit) :
    ListState() {
    @Composable
    override fun Draw(modifier: Modifier) = Box(
        modifier = modifier.padding(horizontal = AppTheme.dimen.spacingM).verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        ErrorState(
            modifier = Modifier.padding(
                vertical = AppTheme.dimen.spacingG
            ),
            title = "Something went wrong!",
            error = error,
            retry = retry
        )
    }
}
