@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.flow.ColdResponseFlow
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import kotlinx.coroutines.flow.Flow

expect interface ResultHolder<T> : DataHolder<DataResult<T>> {
    val flow: ResponseFlow<T>
    val coldFlow: ColdResponseFlow<T>
}

expect interface RegularHolder<T> : DataHolder<T?> {
    val flow: Flow<T?>
}

expect interface DataHolder<T> {
    fun get(): T

    suspend fun set(value: T)

    fun trySet(value: T): Boolean
}
