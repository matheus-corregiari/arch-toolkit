package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.EventDataStatus

internal class SuccessObservable<T>(
    private val status: EventDataStatus,
    private val func: @Composable () -> Unit,
) : ComposeObservable<T, Unit>() {
    @Composable
    override fun observe(result: DataResult<T>) = func()

    override fun hasVisibleContent(result: DataResult<T>) =
        result.isSuccess && status.considerEvent(result)
}
