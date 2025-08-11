@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.github.shared

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureRegistry
import br.com.arch.toolkit.sample.github.shared.structure.core.enableSavedStateHandleCompat
import br.com.arch.toolkit.sample.github.shared.structure.core.featureRegistry
import br.com.arch.toolkit.sample.github.shared.structure.core.savedStateHandleCompat
import br.com.arch.toolkit.sample.github.shared.structure.data.local.defaultKeyValueDataStore
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.TrustManager
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.api.createGithubApi
import br.com.arch.toolkit.sample.github.shared.structure.repository.FeatureRepository
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubRepository
import br.com.arch.toolkit.sample.github.shared.structure.repository.SettingsRepository
import br.com.arch.toolkit.sample.github.shared.ui.home.HomeViewModel
import br.com.arch.toolkit.sample.github.shared.ui.list.ListViewModel
import br.com.arch.toolkit.sample.github.shared.ui.settings.SettingsViewModel
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import io.ktor.client.plugins.logging.Logger as KtorLogger
import org.koin.core.logger.Logger as KoinLogger

private val koinLogger = object : KoinLogger() {
    override fun display(level: Level, msg: String) = Lumber.tag("Koin").info(msg)
}

private val ktorLogger = object : KtorLogger {
    override fun log(message: String) = Lumber.tag("Ktor").info(message)
}

private object LocalSourceModule {
    val module = module {
        single(named("default-keyValue")) { defaultKeyValueDataStore() }
    }
}

private object RemoteSourceModule {
    private val Scope.ktorfit
        get() = get<Ktorfit>(named("ktorfit"))
    private val Scope.ktor
        get() = get<HttpClient>(named("ktor"))

    val module = module {
        // Json Parser
        single {
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                isLenient = true
                allowSpecialFloatingPointValues = true
                allowStructuredMapKeys = true
                prettyPrint = false
                useArrayPolymorphism = false
            }
        }

        single(named("image-client")) {
            HttpClient(CIO) {
                expectSuccess = true
                engine { https { trustManager = TrustManager() } }
                defaultRequest {
                    contentType(ContentType.Image.Any)
                    accept(ContentType.Image.Any)
                }
            }
        }
        single(named("ktor")) {
            HttpClient(CIO) {
                expectSuccess = true
                engine { https { trustManager = TrustManager() } }
                install(ContentNegotiation) { json(get()) }
                install(Logging) {
                    format = LoggingFormat.OkHttp
                    level = LogLevel.BODY
                    logger = ktorLogger
                }
            }
        }

        single(named("ktorfit")) {
            Ktorfit.Builder().build {
                baseUrl("https://api.github.com/")
                httpClient(client = ktor)
            }
        }

        // Api Interfaces
        single { ktorfit.createGithubApi() }
    }
}

private object RepositoryModule {
    private val Scope.allFeatureMap: Map<String, List<FeatureRegistry>>
        get() = getAll<Pair<String, List<FeatureRegistry>>>().toMap()

    val module = module {
        single { FeatureRepository(allFeatureMap) }
        singleOf(::GithubRepository)
        single { SettingsRepository(get(named("default-keyValue"))) }
    }
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
