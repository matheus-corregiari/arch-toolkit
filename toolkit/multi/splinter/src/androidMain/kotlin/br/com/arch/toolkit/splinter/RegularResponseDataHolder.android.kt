@file:OptIn(Experimental::class)
@file:Suppress(
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
    "VariableNaming",
    "MatchingDeclarationName"
)

package br.com.arch.toolkit.splinter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.util.safePostValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

actual abstract class RegularResponseDataHolder<T> internal actual constructor() {

    //region Observable Return Types
    protected actual val _flow = MutableStateFlow<T?>(null)
    actual val flow: Flow<T?> get() = _flow.asSharedFlow()
    private val _liveData = MutableLiveData<T?>(null)
    val liveData: LiveData<T?> get() = _liveData
    //endregion

    //region Functions
    actual fun get(): T? = _flow.value
    protected actual suspend fun set(value: T?) {
        _flow.emit(value)
        _liveData.postValue(value)
    }

    protected actual fun trySet(value: T?) {
        _flow.tryEmit(value)
        _liveData.safePostValue(value)
    }
    //endregion
}
