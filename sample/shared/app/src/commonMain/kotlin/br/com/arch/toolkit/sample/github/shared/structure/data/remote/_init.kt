package br.com.arch.toolkit.sample.github.shared.structure.data.remote

import br.com.arch.toolkit.sample.github.shared.structure.data.remote.api.createGithubApi
import br.com.arch.toolkit.sample.http.createImageClient
import br.com.arch.toolkit.sample.http.createRequestClient
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

object RemoteSourceModule {
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

        single(named("image-client")) { createImageClient() }
        single(named("ktor")) { createRequestClient(get()) }

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
