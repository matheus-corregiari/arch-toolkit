package br.com.arch.toolkit.sample.repository

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.storage.core.StorageProvider
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ToolkitDemoRepository(private val storage: StorageProvider) {
    private val mutableLogs = MutableStateFlow<List<String>>(emptyList())
    val logs = mutableLogs.asStateFlow()
    private val tree = object : Lumber.Oak() {
        override fun isLoggable(tag: String?, level: Lumber.Level): Boolean = tag == "Showcase"
        override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?) {
            if (tag ==
                "Showcase"
            ) {
                mutableLogs.update { (it + "${level.name}: $message").takeLast(LOG_LIMIT) }
            }
        }
    }

    fun start() {
        Lumber.plant(tree)
    }
    fun close() {
        Lumber.uproot(tree)
    }
    fun clear() {
        mutableLogs.value = emptyList()
    }

    // snippet:lumber:start
    fun writeLog() {
        Lumber.tag("Showcase").info("Hello from Arch Toolkit")
    }
    // snippet:lumber:end

    // snippet:storage:start
    suspend fun save(key: String, value: String) = coroutineScope {
        storage.string(demoKey(key)).set(value, this)
    }

    suspend fun read(key: String): String? = storage.string(demoKey(key)).current()

    suspend fun delete(key: String) = coroutineScope {
        storage.string(demoKey(key)).set(null, this)
    }
    // snippet:storage:end

    private fun demoKey(key: String): String {
        require(key.isNotBlank())
        return "showcase.demo.${key.trim()}"
    }

    private companion object {
        const val LOG_LIMIT = 100
    }
}
