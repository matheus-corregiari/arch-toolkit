package br.com.arch.toolkit.sample.github.shared.structure.data.remote

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.api.createGithubApi
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
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import io.ktor.client.plugins.logging.Logger as KtorLogger

private val ktorLogger = object : KtorLogger {
    override fun log(message: String) = Lumber.tag("Ktor").info(message)
}

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

        single(named("image-client")) {
            HttpClient(CIO) {
                expectSuccess = true
                //engine { https { trustManager = TrustManager() } }
                defaultRequest {
                    contentType(ContentType.Image.Any)
                    accept(ContentType.Image.Any)
                }
            }
        }
        single(named("ktor")) {
            HttpClient(CIO) {
                expectSuccess = true
                //engine { https { trustManager = TrustManager() } }
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

//import java.security.cert.X509Certificate
//import javax.net.ssl.X509TrustManager
//
//internal class TrustManager : X509TrustManager {
//    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit
//    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit
//    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
//}
