package br.com.arch.toolkit.compose

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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
val <T> DataResult<T>.composable: ComposableDataResult<T> get() = ComposableDataResult(flowOf(this))

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
val <T> Flow<DataResult<T>>.composable: ComposableDataResult<T> get() = ComposableDataResult(this)

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
fun <T> Flow<DataResult<T>>.collectAsComposableState(): State<ComposableDataResult<T>> {
    return derivedStateOf { composable }
}
