package br.com.arch.toolkit.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult

/**
 * Creates a [ComposableDataResult] from this [DataResult], enabling declarative observation
 * of its loading, success, and error states in Jetpack Compose.
 *
 * Example:
 * ```kotlin
 * val comp = myDataResult.composable
 * comp.Unwrap {
 *     OnData { data -> Text(data.toString()) }
 * }
 * ```
 *
 * @receiver The synchronous [DataResult] to wrap.
 * @return A [ComposableDataResult] configured with the underlying flow.
 * @see ComposableDataResult
 */
val <T> DataResult<T>.composable: ComposableDataResult<T>
    get() = ComposableDataResult(ResponseFlow(this))

/**
 * Creates a [ComposableDataResult] from this [ResponseFlow], providing Compose-aware
 * callbacks for loading, data, and error handling.
 *
 * Example:
 * ```kotlin
 * val compFlow = myFlow.composable3333333
 * compFlow
 *   .OnShowLoading { CircularProgressIndicator() }
 *   .OnError { t -> Text("Error: ${t.message}") }
 *   .Unwrap()
 * ```
 *
 * @receiver The reactive [ResponseFlow] emitting [DataResult].
 * @return A [ComposableDataResult] to chain Compose callbacks.
 * @see ComposableDataResult
 */
val <T> ResponseFlow<T>.composable: ComposableDataResult<T>
    get() = ComposableDataResult(this)

/**
 * Converts this [ResponseFlow] into a Compose [State] that holds a [ComposableDataResult].
 * Useful when you need to obtain the wrapper as state for reuse in multiple composables.
 *
 * Example:
 * ```kotlin
 * val compState by myFlow.collectAsComposableState()
 * compState
 *   .OnData { ... }
 *   .OnError { ... }
 *   .Unwrap()
 * ```
 *
 * @receiver The [ResponseFlow] to collect.
 * @return A [State] whose value is always the latest [ComposableDataResult] instance.
 */
fun <T> ResponseFlow<T>.collectAsComposableState(): State<ComposableDataResult<T>> =
    derivedStateOf(neverEqualPolicy()) { composable }

/**
 * Produces a [State] of [ComposableDataResult] without explicit lifecycle handling.
 * Simplified version that updates the state on each emission.
 *
 * Example:
 * ```kotlin
 * @Composable
 * fun MyScreen(flow: ResponseFlow<MyData>) {
 *     val compState by flow.produceComposableState()
 *     compState
 *         .OnData { data -> /* ... */ }
 *         .Unwrap()
 * }
 * ```
 *
 * @receiver The [ResponseFlow] to observe.
 * @return A [State] that updates its value on every flow emission.
 */
@Composable
fun <T> ResponseFlow<T>.produceComposableState(): State<ComposableDataResult<T>> {
    // Initialize with the current wrapper
    val initial = remember { composable }
    val initialValue = remember { value }
    return produceState(initial, this, initialValue) {
        this@produceState.value = composable
    }
}
