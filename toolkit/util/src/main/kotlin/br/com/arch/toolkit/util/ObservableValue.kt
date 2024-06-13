package br.com.arch.toolkit.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

class ObservableValue<T>(
    initialValue: T,
    private val getter: () -> T?,
    private val setter: ((T?) -> Unit)? = null,
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)
    private val _flow = MutableStateFlow(initialValue)

    val flow: Flow<T>
        get() {
            updateWithGetter()
            return _flow.asStateFlow().shareIn(scope, SharingStarted.WhileSubscribed())
        }
    val liveData: LiveData<T>
        get() {
            updateWithGetter()
            return _flow.asStateFlow().asLiveData(scope.coroutineContext)
        }
    var value: T
        get() = updateWithGetter() ?: _flow.value
        set(value) {
            setter ?: return
            if (_flow.value != value) {
                runCatching { setter?.invoke(value) }
                _flow.update { value }
            }
        }

    private fun updateWithGetter() = runCatching { getter.invoke() }.getOrNull()
        ?.also { saved -> if (_flow.value != saved) _flow.update { saved } }
}
