package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Base sealed class for wiring a [DataResult] stream into Compose UI updates.
 *
 * Each subclass holds an internal [MutableStateFlow] of type [R], which represents
 * the transformed or raw content to be displayed in Compose. Subclasses must
 * emit into [flow] (e.g. `flow.value = newValue`) when their conditions are met.
 *
 * @param T the type of the data carried by the source [DataResult]
 * @param R the type of the content emitted to Compose views
 *
 * @see MutableStateFlow
 * @see br.com.arch.toolkit.compose.ComposableDataResult
 *
 */
internal sealed class ComposeObservable<T, R> {
    /**
     * Determines whether this observable currently has content to display.
     *
     * @param result the [DataResult] being evaluated
     */
    abstract fun hasVisibleContent(result: DataResult<T>): Boolean

    /**
     * Invoked inside a @Composable context to render UI based on [result].
     *
     * Subclasses should collect from [result] and display non-null values,
     * for example:
     * ```kotlin
     * flow.collectAsState().value?.let { content -> /* render content */ }
     * ```
     */
    @Composable
    @Suppress("ComposableNaming")
    abstract fun observe(result: DataResult<T>)
}
