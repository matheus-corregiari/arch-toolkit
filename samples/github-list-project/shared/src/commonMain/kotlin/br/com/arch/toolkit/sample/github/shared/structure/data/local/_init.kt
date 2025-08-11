@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.github.shared.structure.data.local

import org.koin.core.qualifier.named
import org.koin.dsl.module

const val DEFAULT_DATA_STORE = "default-keyValue"

/**
 * TODO
 *  Ver como fica pra criptografar os dados no Android e na JVM
 *  Ver local seguro pra guardar o db na JVM
 */
object StorageModule {
    val module = module {
        single(named("default-keyValue")) { defaultKeyValueDataStore() }
    }
}
