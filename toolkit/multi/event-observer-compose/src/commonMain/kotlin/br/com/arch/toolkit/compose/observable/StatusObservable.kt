package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.result.EventDataStatus

internal class StatusObservable<T>(
    private val status: EventDataStatus,
    private val func: @Composable (DataResultStatus) -> Unit,
) : ComposeObservable<T, Unit>() {
    @Composable
    override fun observe(result: DataResult<T>) = func(result.status)

    override fun hasVisibleContent(result: DataResult<T>) = status.considerEvent(result)
}
