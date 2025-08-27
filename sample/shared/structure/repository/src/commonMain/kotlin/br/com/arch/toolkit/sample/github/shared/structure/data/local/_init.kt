@file:Suppress("MatchingDeclarationName", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.sample.github.shared.structure.data.local

import org.koin.core.module.Module

/**
 * TODO
 *  Ver como fica pra criptografar os dados no Android e na JVM
 *  Ver local seguro pra guardar o db na JVM
 */
expect object LocalSourceModule {
    val module: Module
}
