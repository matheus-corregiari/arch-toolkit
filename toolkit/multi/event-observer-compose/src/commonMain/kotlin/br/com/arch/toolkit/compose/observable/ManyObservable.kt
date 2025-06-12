package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.ObserveWrapper

internal class ManyObservable<T>(
    private val func: @Composable (T) -> Unit,
) : ComposeObservable<T, T>() {
    @Composable
    override fun observe() {
        val state: T? by flow.collectAsState(null)
        state?.let { func(it) }
    }

    override fun hasVisibleContent(result: DataResult<T>) = result.hasData && result.hasManyItems

    override fun ObserveWrapper<T>.attachToWrapper(result: DataResult<T>) {
        manyItems { flow.emit(result.data) }
    }
}
