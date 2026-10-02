@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.shared

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.core.defaultStorage
import br.com.arch.toolkit.sample.data.local.LocalSourceModule
import br.com.arch.toolkit.sample.data.remote.RemoteSourceModule
import br.com.arch.toolkit.sample.feature.github.ui.detail.DetailViewModel
import br.com.arch.toolkit.sample.feature.github.ui.list.ListViewModel
import br.com.arch.toolkit.sample.feature.github.ui.list.RecentViewModel
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsViewModel
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitViewModel
import br.com.arch.toolkit.sample.repository.RepositoryModule
import br.com.arch.toolkit.sample.repository.ToolkitDemoRepository
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.core.logger.Logger as KoinLogger

@OptIn(coil3.annotation.ExperimentalCoilApi::class)
fun initKoin(configure: org.koin.dsl.KoinAppDeclaration = {}) {
    val application = startKoin {
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
    SingletonImageLoader.setSafe { context ->
        ImageLoader.Builder(context)
            .components {
                add(
                    KtorNetworkFetcherFactory(
                        application.koin.get<HttpClient>(named("image-client"))
                    )
                )
            }
            .build()
    }
}
