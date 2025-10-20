@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.shared

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.feature.GithubListModule
import br.com.arch.toolkit.sample.feature.SettingsModule
import br.com.arch.toolkit.sample.github.shared.structure.core.featureRegistry
import br.com.arch.toolkit.sample.github.shared.structure.data.local.LocalSourceModule
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.RemoteSourceModule
import br.com.arch.toolkit.sample.github.shared.structure.repository.RepositoryModule
import br.com.arch.toolkit.sample.shared.ui.home.HomeViewModel
import br.com.arch.toolkit.stateHandle.enableSavedStateHandleCompat
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.logger.Logger as KoinLogger

fun initKoin() {
    startKoin {
        logger(object : KoinLogger() {
            override fun display(level: Level, msg: String) = Lumber.tag("Koin").info(msg)
        })

        modules(
            // Structure - Core
            module { enableSavedStateHandleCompat() },

            // Structure - DesignSystem
            // Nothing to set!

            // Structure - Repository
            LocalSourceModule.module,
            RemoteSourceModule.module,
            RepositoryModule.module,

            // Features
            SettingsModule.module,
            GithubListModule.module,

            // Main Module
            module {
                // ViewModels
                viewModelOf(::HomeViewModel)

                // Features
                featureRegistry("main") { mainRegistry() }
            }
        )
    }
}
