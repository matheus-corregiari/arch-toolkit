@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.flow.ColdResponseFlow
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import kotlinx.coroutines.flow.Flow

expect interface TargetResultHolder<T> : ResultHolder<T>

expect interface TargetRegularHolder<T> : RegularHolder<T>

interface ResultHolder<T> : DataHolder<DataResult<T>> {
    val flow: ResponseFlow<T>
    val coldFlow: ColdResponseFlow<T>

    //region Return Types
    val data: T? get() = get().data
    val error: Throwable? get() = get().error
    val status: DataResultStatus get() = get().status
    //endregion
}

interface RegularHolder<T> : DataHolder<T?> {
    val flow: Flow<T?>
}

interface DataHolder<T> {
    fun get(): T
}

internal interface DataSetter<T> {
    suspend fun set(value: T)

    fun trySet(value: T): Boolean
}
