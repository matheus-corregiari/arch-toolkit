@file:Suppress(
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
    "VariableNaming",
    "MatchingDeclarationName",
)

package br.com.arch.toolkit.splinter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

actual abstract class RegularResponseDataHolder<T> internal actual constructor() {
    //region Observable Return Types
    protected actual val _flow = MutableStateFlow<T?>(null)
    actual val flow: Flow<T?> get() = _flow.asSharedFlow()
    //endregion

    //region Functions
    actual fun get(): T? = _flow.value

    protected actual suspend fun set(value: T?) = _flow.emit(value)

    protected actual fun trySet(value: T?) {
        _flow.tryEmit(value)
    }
    //endregion
}
