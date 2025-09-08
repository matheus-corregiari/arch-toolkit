package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult

internal class EmptyObservable<T>(
    private val func: @Composable () -> Unit,
) : ComposeObservable<T, Unit>() {
    @Composable
    override fun observe(result: DataResult<T>) = func()

    override fun hasVisibleContent(result: DataResult<T>) = result.hasData && result.isEmpty
}
