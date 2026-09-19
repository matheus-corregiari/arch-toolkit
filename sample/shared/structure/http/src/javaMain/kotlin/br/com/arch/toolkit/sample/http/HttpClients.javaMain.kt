package br.com.arch.toolkit.sample.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.json.Json

actual fun createImageClient() = HttpClient(OkHttp) { configureTimeouts() }

actual fun createRequestClient(json: Json) = HttpClient(OkHttp) { configureRequests(json) }
