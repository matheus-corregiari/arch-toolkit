package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus

internal class EmptyObservable<T>(
    private val func: @Composable (DataResultStatus, Throwable?) -> Unit,
) : ComposeObservable<T, Unit>() {
    @Composable
    override fun observe(result: DataResult<T>) {
        val (_, error, status) = result
        func(status, error)
    }

    override fun hasVisibleContent(result: DataResult<T>) = result.hasData && result.isEmpty
}
