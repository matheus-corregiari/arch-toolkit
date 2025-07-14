@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.flow.ColdResponseFlow
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import kotlinx.coroutines.flow.Flow

actual interface DataHolder<T> {
    actual fun get(): T

    actual suspend fun set(value: T)

    actual fun trySet(value: T): Boolean
}

actual interface RegularHolder<T> : DataHolder<T?> {
    actual val flow: Flow<T?>
}

actual interface ResultHolder<T> : DataHolder<DataResult<T>> {
    actual val flow: ResponseFlow<T>
    actual val coldFlow: ColdResponseFlow<T>
}
