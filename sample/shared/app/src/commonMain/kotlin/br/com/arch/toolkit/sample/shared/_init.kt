@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.shared

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.feature.githubSample.ui.detail.DetailViewModel
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.ListViewModel
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.RecentViewModel
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsViewModel
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitViewModel
import br.com.arch.toolkit.sample.github.shared.structure.core.defaultStorage
import br.com.arch.toolkit.sample.github.shared.structure.data.local.LocalSourceModule
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.RemoteSourceModule
import br.com.arch.toolkit.sample.github.shared.structure.repository.RepositoryModule
import br.com.arch.toolkit.sample.github.shared.structure.repository.ToolkitDemoRepository
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.core.logger.Logger as KoinLogger

fun initKoin(configure: org.koin.dsl.KoinAppDeclaration = {}) {
    startKoin {
        configure()
        logger(object : KoinLogger() {
            override fun display(level: Level, msg: String) = Lumber.tag("Koin").info(msg)
        })

        modules(
            // Structure - DesignSystem
            // Nothing to set!

            // Structure - Repository
            LocalSourceModule.module,
            RemoteSourceModule.module,
            RepositoryModule.module,

            // Features

            // Main Module
            module {
                viewModel { ListViewModel(get(), get()) }
                viewModel { RecentViewModel(get()) }
                viewModel { parameters ->
                    DetailViewModel(
                        get(),
                        parameters.get(),
                        parameters.get(),
                        get()
                    )
                }
                viewModel { SettingsViewModel(get()) }
                factory { ToolkitDemoRepository(defaultStorage) }
                viewModel { ToolkitViewModel(get()) }
            }
        )
    }
}
