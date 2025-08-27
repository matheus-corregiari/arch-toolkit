package br.com.arch.toolkit.sample.github.shared.structure.data.remote

import br.com.arch.toolkit.lumber.Lumber
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
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
import org.koin.core.scope.Scope
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

private val ktorLogger = object : Logger {
    override fun log(message: String) = Lumber.tag("Ktor").info(message)
}

internal actual fun Scope.imageClient() = HttpClient(OkHttp) {
    expectSuccess = true
    engine {
        config {
            val trustManager = TrustManager()
            val sslContextFactory = SSLContext.getInstance("SSL")
            sslContextFactory.init(null, arrayOf(trustManager), null)
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContextFactory.socketFactory)
            sslSocketFactory(sslContextFactory.socketFactory, trustManager)
        }
    }
    defaultRequest {
        contentType(ContentType.Image.Any)
        accept(ContentType.Image.Any)
    }
}

internal actual fun Scope.requestClient() = HttpClient(OkHttp) {
    expectSuccess = true
    engine {
        config {
            val trustManager = TrustManager()
            val sslContextFactory = SSLContext.getInstance("SSL")
            sslContextFactory.init(null, arrayOf(trustManager), null)
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContextFactory.socketFactory)
            sslSocketFactory(sslContextFactory.socketFactory, trustManager)
        }
    }
    install(ContentNegotiation) { json(get()) }
    install(Logging) {
        format = LoggingFormat.OkHttp
        level = LogLevel.BODY
        logger = ktorLogger
    }
}

@Suppress("CustomX509TrustManager")
private class TrustManager : X509TrustManager {
    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit
    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit
    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
}
