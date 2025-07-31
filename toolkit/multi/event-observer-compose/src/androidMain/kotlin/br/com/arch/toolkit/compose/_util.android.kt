package br.com.arch.toolkit.compose

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResult

/**
 * Wraps this [ResponseLiveData] as a [ComposableDataResult], for Compose-driven UI updates.
 *
 * Example:
 * ```kotlin
 * val comp = liveData.composable
 * comp.OnData { d -> Text(d.toString()) }.Unwrap()
 * ```
 *
 * @see ComposableDataResult
 */
val <T> LiveData<DataResult<T>>.composable: ComposableDataResult<T>
    get() = ComposableDataResult(asFlow())

/**
 * Collects this [ResponseLiveData] as a Compose [State] of [ComposableDataResult], updating on each change.
 *
 * Example:
 * ```kotlin
 * val state by liveData.collectAsComposableState()
 * state.OnError { Text("Oops") }.Unwrap()
 * ```
 *
 * @return a [State] holding the latest [ComposableDataResult]
 * @see ComposableDataResult
 */
fun <T> LiveData<DataResult<T>>.collectAsComposableState() = derivedStateOf { composable }
