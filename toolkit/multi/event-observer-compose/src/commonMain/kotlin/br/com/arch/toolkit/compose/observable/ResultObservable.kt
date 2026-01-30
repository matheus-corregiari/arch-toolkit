package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus

internal class ResultObservable<T>(
    private val func: @Composable (T?, DataResultStatus, Throwable?) -> Unit,
) : ComposeObservable<T, DataResult<T>>() {
    @Composable
    override fun observe(result: DataResult<T>) {
        val (data, error, status) = result
        func(data, status, error)
    }

    override fun hasVisibleContent(result: DataResult<T>) = true
}
