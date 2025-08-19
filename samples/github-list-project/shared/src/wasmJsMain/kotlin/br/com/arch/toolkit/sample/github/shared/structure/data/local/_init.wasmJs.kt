@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.sample.github.shared.structure.data.local

import br.com.arch.toolkit.storage.core.StorageProvider
import br.com.arch.toolkit.storage.memory.MemoryStoreProvider
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual object LocalSourceModule {
    actual val module: Module = module {
        single(named(DEFAULT_KEY_VALUE)) { mutableMapOf<String, MutableStateFlow<*>>() }
        single<StorageProvider>(named(DEFAULT_DATA_STORE)) {
            MemoryStoreProvider(database = get(named(DEFAULT_KEY_VALUE)))
        }
    }
}
