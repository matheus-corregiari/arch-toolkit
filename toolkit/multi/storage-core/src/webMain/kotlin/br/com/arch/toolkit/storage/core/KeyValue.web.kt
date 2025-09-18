package br.com.arch.toolkit.storage.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

actual fun <T> KeyValue<T>.instant(): T = lastValue
internal actual fun <T> KeyValue<T>.defaultScope() = CoroutineScope(Dispatchers.Default)
