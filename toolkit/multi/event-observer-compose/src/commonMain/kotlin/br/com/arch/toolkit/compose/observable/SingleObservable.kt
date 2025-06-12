package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.ObserveWrapper

internal class SingleObservable<T, R>(
    private val func: @Composable (R) -> Unit,
) : ComposeObservable<T, R>() {
    @Composable
    override fun observe() {
        val state: R? by flow.collectAsState(null)
        state?.let { func(it) }
    }

    override fun hasVisibleContent(result: DataResult<T>) = result.hasData && result.hasOneItem

    override fun ObserveWrapper<T>.attachToWrapper(result: DataResult<T>) {
        oneItem {
            val item = when (val data = result.data) {
                is Collection<*> -> data.firstOrNull()
                is Map<*, *> -> data.toList().firstOrNull()
                is Sequence<*> -> data.firstOrNull()
                else -> null
            }
            flow.emit(item as? R)
        }
    }
}
