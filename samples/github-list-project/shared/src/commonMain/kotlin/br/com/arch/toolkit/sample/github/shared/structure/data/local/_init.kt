@file:Suppress("MatchingDeclarationName", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.sample.github.shared.structure.data.local

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.storage.core.StorageProvider
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

internal const val DEFAULT_KEY_VALUE = "default-keyValue"
internal const val DEFAULT_DATA_STORE = "default-storeProvider"

/**
 * TODO
 *  Ver como fica pra criptografar os dados no Android e na JVM
 *  Ver local seguro pra guardar o db na JVM
 */
expect object LocalSourceModule {
    val module: Module
}

@Composable
fun rememberDefaultStorage(): StorageProvider = koinInject(named(DEFAULT_DATA_STORE))
val KoinComponent.defaultStorage: StorageProvider get() = get(named(DEFAULT_DATA_STORE))
val Scope.defaultStorage: StorageProvider get() = get(named(DEFAULT_DATA_STORE))
