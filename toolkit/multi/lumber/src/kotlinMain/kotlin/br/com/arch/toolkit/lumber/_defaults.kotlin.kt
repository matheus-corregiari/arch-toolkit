@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalAtomicApi::class)

package br.com.arch.toolkit.lumber

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

actual fun defaultTag(exclude: Set<String>): String? = null

actual class ThreadSafe<T> actual constructor() {

    private val atomic = AtomicReference<T?>(null)

    actual fun get(): T? = atomic.load()

    actual fun set(data: T?) = atomic.store(data)

    actual fun remove() = set(null)
}

actual fun String.format(vararg args: Any?): String {
    val match = Regex("%[sd]").find(this) ?: return this
    if (args.isEmpty()) error("Wrong number of arguments")
    val argument = args.firstOrNull()
    val formatted = when (match.value) {
        "%s" -> argument.toString()
        "%d" -> (argument as? Number).toString()
        else -> match.value
    }
    return replaceRange(
        range = match.range,
        replacement = formatted
    ).format(
        args = args.drop(1).toTypedArray()
    )
}
