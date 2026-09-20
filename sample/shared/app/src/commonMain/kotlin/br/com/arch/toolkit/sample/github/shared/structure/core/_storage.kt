package br.com.arch.toolkit.sample.github.shared.structure.core

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.storage.core.StorageProvider
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

const val DEFAULT_KEY_VALUE = "default-keyValue"
const val DEFAULT_DATA_STORE = "default-storeProvider"

@Composable
fun rememberDefaultStorage(): StorageProvider = koinInject(named(DEFAULT_DATA_STORE))
val KoinComponent.defaultStorage: StorageProvider get() = get(named(DEFAULT_DATA_STORE))
val Scope.defaultStorage: StorageProvider get() = get(named(DEFAULT_DATA_STORE))
