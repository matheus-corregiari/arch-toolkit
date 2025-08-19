@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level
import br.com.arch.toolkit.lumber.Lumber.Oak
import kotlinx.coroutines.sync.Mutex

internal const val MAX_LOG_LENGTH = 4000
internal const val MAX_TAG_LENGTH = 25

internal fun <T> Mutex.synchronized(key: Any, block: () -> T) = try {
    runCatching { tryLock(owner = key) }
    block()
} finally {
    runCatching { if(isLocked) unlock(owner = key) }
}

internal val fqcnIgnore = setOfNotNull(
    Lumber::class.qualifiedName,
    Level::class.qualifiedName,
    Lumber.OakWood::class.qualifiedName,
    Oak::class.qualifiedName,
    DebugTree::class.qualifiedName,
)

expect fun defaultTag(): String?
expect fun String.format(vararg args: Any?): String

expect class ThreadSafe<T>() {
    fun get(): T?
    fun set(data: T?)
    fun remove()
}
