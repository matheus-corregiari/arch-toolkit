package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.result.ObserveWrapper
import kotlinx.coroutines.flow.update

internal class DataObservable<T>(
    private val func: @Composable (T, DataResultStatus, Throwable?) -> Unit,
) : ComposeObservable<T, DataResult<T>>() {
    @Composable
    override fun observe() {
        val state: DataResult<T>? by flow.collectAsState(null)
        val (data, error, status) = state ?: return
        data ?: return
        func(data, status, error)
    }

    override fun hasVisibleContent(result: DataResult<T>) = result.hasData

    override fun ObserveWrapper<T>.attachToWrapper(result: DataResult<T>) {
        data { flow.update { result } }
    }
}
