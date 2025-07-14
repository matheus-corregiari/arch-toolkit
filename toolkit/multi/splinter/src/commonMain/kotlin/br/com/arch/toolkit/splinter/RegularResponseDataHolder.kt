@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "VariableNaming")

package br.com.arch.toolkit.splinter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

expect abstract class RegularResponseDataHolder<T> internal constructor() {
    //region Observable Return Types
    protected val _flow: MutableStateFlow<T?>
    val flow: Flow<T?>
    //endregion

    //region Functions
    fun get(): T?

    protected suspend fun set(value: T?)

    protected fun trySet(value: T?)
    //endregion
}
