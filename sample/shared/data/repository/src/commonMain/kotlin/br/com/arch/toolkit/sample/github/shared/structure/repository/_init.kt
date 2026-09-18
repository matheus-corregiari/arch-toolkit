package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureRegistry
import br.com.arch.toolkit.sample.github.shared.structure.core.defaultStorage
import org.koin.core.module.dsl.singleOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

object RepositoryModule {
    private val Scope.allFeatureMap: Map<String, List<FeatureRegistry>>
        get() = getAll<Pair<String, List<FeatureRegistry>>>().toMap()

    val module = module {
        single { FeatureRepository(allFeatureMap) }
        singleOf(::GithubRepository)
        single { SettingsRepository(defaultStorage) }
    }
}
