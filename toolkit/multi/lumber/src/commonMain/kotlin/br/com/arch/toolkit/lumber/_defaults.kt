@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

internal expect val MAX_LOG_LENGTH: Int
internal expect val MAX_TAG_LENGTH: Int

internal expect fun defaultTag(): String?

expect class ThreadSafe<T>() {
    fun get(): T?
    fun set(data: T?)
    fun remove()
}
