package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.EventDataStatus

internal class ErrorWithThrowableObservable<T>(
    private val status: EventDataStatus,
    private val func: @Composable (Throwable) -> Unit,
) : ComposeObservable<T, Throwable>() {
    @Composable
    override fun observe(result: DataResult<T>) {
        val (_, error, _) = result
        error ?: return
        func(error)
    }

    override fun hasVisibleContent(result: DataResult<T>) =
        result.isError && result.hasError && status.considerEvent(result)
}
