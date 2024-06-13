package br.com.arch.toolkit.storage

import android.content.Context
import br.com.arch.toolkit.storage.keyValue.KeyValueStorage
import br.com.arch.toolkit.storage.keyValue.MemoryStorage
import br.com.arch.toolkit.storage.keyValue.SharedPrefStorage
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

sealed class Storage {

    data object KeyValue : Storage() {
        private var _encrypted: SharedPrefStorage.Encrypted? = null
        private var _regular: SharedPrefStorage.Regular? = null

        val memory = MemoryStorage("default-memory")
        val encrypted: SharedPrefStorage.Encrypted
            get() = requireNotNull(_encrypted) { "Not initialized, Be aware to call init() before use" }
        val regular: SharedPrefStorage.Regular
            get() = requireNotNull(_regular) { "Not initialized, Be aware to call init() before use" }

        fun init(context: Context) {
            _encrypted = SharedPrefStorage.Encrypted(context, "default-encrypted")
            _regular = SharedPrefStorage.Regular(context, "default-regular")
        }
    }

    data object Settings : Storage() {
        var threshold: Duration = 300.milliseconds
            private set
        var keyValue: KeyValueStorage = KeyValue.memory
            private set

        var complexDataParser: ComplexDataParser? = null
            private set

        fun setDefaultThreshold(threshold: Duration) = apply {
            this.threshold = threshold
        }

        fun setDefaultStorage(storage: KeyValueStorage) = apply {
            keyValue = storage
        }

        fun setComplexDataParser(parser: ComplexDataParser) = apply {
            complexDataParser = parser
        }
    }
}
