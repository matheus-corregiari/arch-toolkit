package br.com.arch.toolkit.sample.http

import br.com.arch.toolkit.lumber.Lumber
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun createImageClient(): HttpClient
expect fun createRequestClient(json: Json): HttpClient

internal fun HttpClientConfig<*>.configureTimeouts() {
    expectSuccess = true
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 30_000
    }
}

internal fun HttpClientConfig<*>.configureRequests(parser: Json) {
    configureTimeouts()
    install(ContentNegotiation) { json(parser) }
    install(Logging) {
        level = LogLevel.INFO
        logger = object : Logger {
            override fun log(message: String) = Lumber.tag("HTTP").info(message)
        }
    }
}
