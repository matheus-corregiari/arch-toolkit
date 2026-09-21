package br.com.arch.toolkit.sample.feature.toolkit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.github.shared.structure.repository.ToolkitDemoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StorageDemoState(
    val value: String? = null,
    val message: AppText? = null,
    val busy: Boolean = false
)

class ToolkitViewModel(private val repository: ToolkitDemoRepository) : ViewModel() {
    val logs = repository.logs
    private val mutableStorage = MutableStateFlow(StorageDemoState())
    val storage = mutableStorage.asStateFlow()

    init {
        repository.start()
    }
    override fun onCleared() {
        repository.close()
    }
    fun writeLog() = repository.writeLog()
    fun clearLogs() = repository.clear()
    fun save(
        key: String,
        value: String
    ) = action(key, AppText.SAVED) {
        repository.save(key, value)
        repository.read(key)
    }
    fun read(key: String) = action(key, AppText.RESULT) { repository.read(key) }
    fun delete(key: String) = action(key, AppText.DELETED) {
        repository.delete(key)
        null
    }

    private fun action(key: String, message: AppText, operation: suspend () -> String?) {
        if (key.isBlank()) {
            mutableStorage.value = StorageDemoState(message = AppText.VALID_KEY)
            return
        }
        if (storage.value.busy) return
        mutableStorage.value = storage.value.copy(busy = true)
        viewModelScope.launch {
            try {
                mutableStorage.value = StorageDemoState(operation(), message)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: kotlinx.io.IOException) {
                mutableStorage.value = StorageDemoState(message = AppText.STORAGE_ERROR)
            }
        }
    }
}
