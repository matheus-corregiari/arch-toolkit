package br.com.arch.toolkit.compose.observable

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.ObserveWrapper
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
     * Backing flow that holds the latest value to be rendered.
     * Subclasses emit into this flow when new content is ready.
     */
    protected val flow = MutableStateFlow<R?>(null)

    /**
     * Attaches this observable to an [ObserveWrapper] so it can react
     * to each emitted [DataResult].
     *
     * Implementations should call wrapper APIs (e.g. `OnData`, `OnError`)
     * and update [flow] accordingly.
     *
     * @receiver the [ObserveWrapper] managing callbacks for [DataResult]
     * @param result the incoming [DataResult] to observe
     */
    abstract fun ObserveWrapper<T>.attachToWrapper(result: DataResult<T>)

    /**
     * Determines whether this observable currently has content to display.
     *
     * @param result the [DataResult] being evaluated
     * @return `true` if [flow] contains a non-null value ready for Compose
     */
    abstract fun hasVisibleContent(result: DataResult<T>): Boolean

    /**
     * Invoked inside a @Composable context to render UI based on [flow].
     *
     * Subclasses should collect from [flow] and display non-null values,
     * for example:
     * ```kotlin
     * flow.collectAsState().value?.let { content -> /* render content */ }
     * ```
     */
    @Composable
    @Suppress("ComposableNaming")
    abstract fun observe()
}
