package br.com.arch.toolkit.sample.github.shared.structure.data.remote

import br.com.arch.toolkit.lumber.Lumber
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.ChallengeHandler
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.scope.Scope
import platform.Foundation.NSURLCredential
import platform.Foundation.create
import platform.Foundation.serverTrust

private val ktorLogger = object : Logger {
    override fun log(message: String) = Lumber.tag("Ktor").info(message)
}

internal actual fun Scope.imageClient() = HttpClient(Darwin) {
    expectSuccess = true
    engine {
        configureRequest {
            setAllowsCellularAccess(true)
        }
    }
    defaultRequest {
        contentType(ContentType.Image.Any)
        accept(ContentType.Image.Any)
    }
}

@BetaInteropApi
@OptIn(ExperimentalForeignApi::class)
internal actual fun Scope.requestClient() = HttpClient(Darwin) {
    expectSuccess = true
    engine {
        configureRequest { setAllowsCellularAccess(true) }
        val challenge: ChallengeHandler = { _, _, challenge, completionHandler ->
            val credential = NSURLCredential.create(trust = challenge.protectionSpace.serverTrust)
            completionHandler(0, credential)
        }
        handleChallenge(challenge)
    }
    install(ContentNegotiation) { json(get()) }
    install(Logging) {
        format = LoggingFormat.OkHttp
        level = LogLevel.BODY
        logger = ktorLogger
    }
}
