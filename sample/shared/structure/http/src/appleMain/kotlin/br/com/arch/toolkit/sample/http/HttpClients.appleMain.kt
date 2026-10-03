package br.com.arch.toolkit.sample.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import kotlinx.serialization.json.Json

actual fun createImageClient() = HttpClient(Darwin) { configureTimeouts() }

actual fun createRequestClient(json: Json) = HttpClient(Darwin) { configureRequests(json) }
