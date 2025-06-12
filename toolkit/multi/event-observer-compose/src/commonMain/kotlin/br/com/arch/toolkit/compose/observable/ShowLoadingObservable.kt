package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.EventDataStatus
import br.com.arch.toolkit.result.ObserveWrapper

internal class ShowLoadingObservable<T>(
    private val status: EventDataStatus,
    private val func: @Composable () -> Unit,
) : ComposeObservable<T, Unit>() {
    @Composable
    override fun observe() {
        val state: Unit? by flow.collectAsState(null)
        state?.let { func() }
    }

    override fun hasVisibleContent(result: DataResult<T>) =
        result.isLoading && status.considerEvent(result)

    override fun ObserveWrapper<T>.attachToWrapper(result: DataResult<T>) {
        showLoading(dataStatus = status) { flow.emit(Unit) }
    }
}
