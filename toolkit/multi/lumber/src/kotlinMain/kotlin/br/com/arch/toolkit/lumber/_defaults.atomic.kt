@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalAtomicApi::class)

package br.com.arch.toolkit.lumber

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

actual class ThreadSafe<T> actual constructor() {

    private val atomic = AtomicReference<T?>(null)

    actual fun get(): T? = atomic.load()

    actual fun set(data: T?) = atomic.store(data)

    actual fun remove() = set(null)
}

actual fun String.format(vararg args: Any?): String {
    var index = 0
    return replace(Regex("%[sd]")) {
        val arg = args.getOrNull(index++)
        when (it.value) {
            "%s" -> arg?.toString() ?: "null"
            "%d" -> (arg as? Number)?.toString() ?: "0"
            else -> it.value
        }
    }
}
