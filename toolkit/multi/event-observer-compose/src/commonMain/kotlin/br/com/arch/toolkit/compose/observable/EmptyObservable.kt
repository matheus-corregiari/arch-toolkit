package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.ObserveWrapper

internal class EmptyObservable<T>(
    private val func: @Composable () -> Unit,
) : ComposeObservable<T, Unit>() {
    @Composable
    override fun observe() {
        val state: Unit? by flow.collectAsState(null)
        state?.let { func() }
    }

    override fun hasVisibleContent(result: DataResult<T>) = result.hasData && result.isEmpty

    override fun ObserveWrapper<T>.attachToWrapper(result: DataResult<T>) {
        empty { flow.emit(Unit) }
    }
}
