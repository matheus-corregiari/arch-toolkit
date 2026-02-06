@file:Suppress("UNCHECKED_CAST")

package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus

internal class SingleObservable<T, R>(
    private val func: @Composable (R, DataResultStatus, Throwable?) -> Unit,
) : ComposeObservable<T, R>() {

    @Composable
    override fun observe(result: DataResult<T>) {
        val (data, error, status) = result
        val item = when (data) {
            is Collection<*> -> data.firstOrNull()
            is Map<*, *> -> data.toList().firstOrNull()
            is Sequence<*> -> data.firstOrNull()
            else -> null
        } as? R ?: return

        func(item, status, error)
    }

    override fun hasVisibleContent(result: DataResult<T>) = result.hasData && result.hasOneItem
}
