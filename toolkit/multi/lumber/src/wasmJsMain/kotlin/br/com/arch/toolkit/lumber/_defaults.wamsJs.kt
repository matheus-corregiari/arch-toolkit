@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalAtomicApi::class)

package br.com.arch.toolkit.lumber

import kotlin.concurrent.atomics.ExperimentalAtomicApi

private fun parseLine(line: String): String? {
    val trimmed = line.trim()
    if (!trimmed.startsWith("at ")) return null
    val symbol = trimmed.removePrefix("at ").trim().substringBefore(" (")
    val classOrFunc = symbol.substringBeforeLast('.')
    val candidate = classOrFunc.substringAfterLast('.')
    val clean = candidate.substringAfterLast('/').substringAfterLast('\\')
        .substringAfterLast('.').substringBefore('@').substringBefore('$')
    return clean.chunked(MAX_TAG_LENGTH).first()
        .takeIf { it.isNotEmpty() && clean != "Object" }
}

actual fun defaultTag(exclude: Set<String>): String? {
    val stack = runCatching { jsStack() }.getOrNull()
    if (stack == null || stack.isBlank()) return null
    return stack.lineSequence().drop(1)
        .mapNotNull(::parseLine)
        .firstOrNull { it !in exclude }
}
