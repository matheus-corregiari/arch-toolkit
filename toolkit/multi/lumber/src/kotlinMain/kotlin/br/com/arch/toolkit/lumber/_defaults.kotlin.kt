@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

internal actual const val MAX_LOG_LENGTH: Int = 8000
internal actual const val MAX_TAG_LENGTH: Int = 25

actual fun defaultTag(): String? = null

@OptIn(ExperimentalAtomicApi::class)
actual class ThreadSafe<T> {

    private val atomic = AtomicReference<T?>(null)

    actual fun get(): T? = atomic.load()

    actual fun set(data: T?) = atomic.store(data)

    actual fun remove() = set(null)
}
