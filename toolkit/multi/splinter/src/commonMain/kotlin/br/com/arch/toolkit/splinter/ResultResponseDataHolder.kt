@file:OptIn(Experimental::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "VariableNaming")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.flow.MutableResponseFlow
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import kotlinx.coroutines.CoroutineScope

expect abstract class ResultResponseDataHolder<T> internal constructor() {

    //region Auxiliary Properties
    protected abstract val scope: () -> CoroutineScope
    //endregion

    //region Observable Return Types
    protected val _flow: MutableResponseFlow<T>
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
