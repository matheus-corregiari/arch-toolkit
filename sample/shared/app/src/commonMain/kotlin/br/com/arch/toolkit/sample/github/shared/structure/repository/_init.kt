package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.core.defaultStorage
import org.koin.dsl.module

object RepositoryModule {
    val module = module {
        single<GithubRepository> { RemoteGithubRepository(get()) }
        single { SettingsRepository(defaultStorage) }
    }
}
