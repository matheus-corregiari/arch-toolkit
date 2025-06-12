package br.com.arch.toolkit.compose

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult

/**
 * Wraps this [DataResult] in a [ComposableDataResult] for use in Jetpack Compose.
 *
 * Example:
 * ```kotlin
 * val comp = myDataResult.composable
 * comp.Unwrap { OnData { data -> Text(data.toString()) } }
 * ```
 *
 * @receiver the source [DataResult] to observe in Compose
 * @return a [ComposableDataResult] that you can chain callbacks on
 * @see ComposableDataResult
 */
val <T> DataResult<T>.composable: ComposableDataResult<T>
    get() = ComposableDataResult(ResponseFlow(this))

/**
 * Converts this [ResponseFlow] into a [ComposableDataResult], enabling Compose-based observation.
 *
 * Example:
 * ```kotlin
 * val compFlow = myFlow.composable
 * compFlow.OnError { t -> Text("Error: ${t.message}") }.Unwrap()
 * ```
 *
 * @see ComposableDataResult
 */
val <T> ResponseFlow<T>.composable: ComposableDataResult<T>
    get() = ComposableDataResult(this)

/**
 * Collects this [ResponseFlow] as a Compose [State] of [ComposableDataResult], updating on each emission.
 *
 * Example:
 * ```kotlin
 * val state by myFlow.collectAsComposableState()
 * state.OnShowLoading { CircularProgressIndicator() }.Unwrap()
 * ```
 *
 * @receiver the [ResponseFlow] to collect into Compose
 * @return a [State] of the latest [ComposableDataResult]
 * @see ComposableDataResult
 */
fun <T> ResponseFlow<T>.collectAsComposableState() = derivedStateOf { composable }
