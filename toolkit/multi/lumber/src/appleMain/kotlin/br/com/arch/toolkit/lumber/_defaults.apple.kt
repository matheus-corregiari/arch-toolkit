@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

internal actual const val MAX_LOG_LENGTH: Int = 8000
internal actual const val MAX_TAG_LENGTH: Int = 30
private val METHOD_REGEX =
    "(?<full>(?:[a-zA-Z]+\\.)+(?<className>[a-zA-Z]+))#(?<method>[a-zA-Z ]+)\\(".toRegex()

internal actual fun defaultTag(): String? {
    val ignore = fqcnIgnore.map { it.qualifiedName }
    return METHOD_REGEX
        .findAll(Throwable("Default Log Exception").stackTraceToString())
        .mapNotNull(::extractData)
        .filter { (full, _, _) -> full !in ignore }
        .map { (_, className, method) -> "$className:$method" }
        .firstOrNull()
        ?.chunked(MAX_TAG_LENGTH)?.first()
}

@OptIn(ExperimentalAtomicApi::class)
actual class ThreadSafe<T> {
    private val atomic = AtomicReference<T?>(null)
    actual fun get(): T? = atomic.load()
    actual fun set(data: T?) = atomic.store(data)
    actual fun remove() = set(null)
}

private fun extractData(match: MatchResult): Triple<String, String, String>? {
    val group = (match.groups as MatchNamedGroupCollection)
    val full = group["full"]
    val className = group["className"]
    val method = group["method"]
    if (full == null || className == null || method == null) return null
    return Triple(
        full.value,
        className.value,
        method.value.camelcase()
    )
}

