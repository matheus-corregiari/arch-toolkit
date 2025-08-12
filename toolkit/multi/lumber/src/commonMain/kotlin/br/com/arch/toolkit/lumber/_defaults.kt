@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import kotlinx.coroutines.sync.Mutex

internal const val MAX_LOG_LENGTH = 4000
internal const val MAX_TAG_LENGTH = 25

internal fun <T> Mutex.synchronized(key: Any, block: () -> T) = try {
    holdsLock(owner = key)
    block()
} finally {
    unlock(owner = key)
}

expect fun defaultTag(exclude: Set<String>): String?
expect fun String.format(vararg args: Any?): String

expect class ThreadSafe<T>() {
    fun get(): T?
    fun set(data: T?)
    fun remove()
}
