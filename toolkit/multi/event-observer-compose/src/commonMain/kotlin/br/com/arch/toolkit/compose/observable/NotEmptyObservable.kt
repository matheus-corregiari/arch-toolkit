package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult

internal class NotEmptyObservable<T>(
    private val func: @Composable (T) -> Unit,
) : ComposeObservable<T, T>() {
    @Composable
    override fun observe(result: DataResult<T>) {
        val (data, _, _) = result
        data ?: return
        func(data)
    }

    override fun hasVisibleContent(result: DataResult<T>) = result.hasData && result.isNotEmpty

}
