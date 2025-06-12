package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.EventDataStatus
import br.com.arch.toolkit.result.ObserveWrapper

internal class ErrorWithThrowableObservable<T>(
    private val status: EventDataStatus,
    private val func: @Composable (Throwable) -> Unit,
) : ComposeObservable<T, Throwable>() {
    @Composable
    override fun observe() {
        val state: Throwable? by flow.collectAsState(null)
        state?.let { func(it) }
    }

    override fun hasVisibleContent(result: DataResult<T>) =
        result.isError && result.hasError && status.considerEvent(result)

    override fun ObserveWrapper<T>.attachToWrapper(result: DataResult<T>) {
        error(dataStatus = status) { error -> flow.emit(error) }
    }
}
