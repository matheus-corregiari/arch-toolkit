@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.github.shared

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.shared.structure.core.enableSavedStateHandleCompat
import br.com.arch.toolkit.sample.github.shared.structure.core.featureRegistry
import br.com.arch.toolkit.sample.github.shared.structure.core.savedStateHandleCompat
import br.com.arch.toolkit.sample.github.shared.structure.data.local.LocalSourceModule
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.RemoteSourceModule
import br.com.arch.toolkit.sample.github.shared.structure.repository.RepositoryModule
import br.com.arch.toolkit.sample.github.shared.ui.home.HomeViewModel
import br.com.arch.toolkit.sample.github.shared.ui.list.ListViewModel
import br.com.arch.toolkit.sample.github.shared.ui.settings.SettingsViewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.logger.Logger as KoinLogger

private val koinLogger = object : KoinLogger() {
    override fun display(level: Level, msg: String) = Lumber.tag("Koin").info(msg)
}

private object FeatureModule {
    val module = module {
        // Features
        featureRegistry("repository-list-home") { repositoryList() }
        featureRegistry("settings-home") { settings() }
    }
}

private object AppModule {
    val module = module {
        // ViewModels
        viewModelOf(::HomeViewModel)
        viewModel { ListViewModel(get(), savedStateHandleCompat("list-view-model")) }
        viewModelOf(::SettingsViewModel)
    }
}

fun initKoin() {
    startKoin {
        logger(koinLogger)
        module { enableSavedStateHandleCompat() }
        modules(
            LocalSourceModule.module,
            RemoteSourceModule.module,
            RepositoryModule.module,
            FeatureModule.module,
            AppModule.module,
        )
    }
}
