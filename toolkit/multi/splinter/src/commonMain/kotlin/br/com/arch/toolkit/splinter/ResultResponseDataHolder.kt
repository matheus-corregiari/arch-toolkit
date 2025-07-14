@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "VariableNaming")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.flow.ColdResponseFlow
import br.com.arch.toolkit.flow.MutableResponseFlow
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlin.coroutines.coroutineContext

expect abstract class ResultResponseDataHolder<T> internal constructor() {
    //region Auxiliary Properties
    protected abstract val scope: () -> CoroutineScope
    protected abstract val running: () -> Boolean
    //endregion

    //region Observable Return Types
    protected val _flow: MutableResponseFlow<T>
    val coldFlow: ColdResponseFlow<T>
    val flow: ResponseFlow<T>
    //endregion

    //region Return Types
    val data: T?
    val error: Throwable?
    val status: DataResultStatus
    //endregion

    //region Functions
    fun get(): DataResult<T>

    protected suspend fun set(value: DataResult<T>)

    protected fun trySet(value: DataResult<T>)
    //endregion
}

suspend inline fun <T> Flow<T>.collectWhile(crossinline predicate: suspend (value: T) -> Boolean) {
    val collector = object : FlowCollector<T> {
        override suspend fun emit(value: T) {
            if (!predicate(value)) {
                throw CancellationException()
            }
        }
    }
    try {
        collect(collector)
    } finally {
        coroutineContext.ensureActive()
    }
}
