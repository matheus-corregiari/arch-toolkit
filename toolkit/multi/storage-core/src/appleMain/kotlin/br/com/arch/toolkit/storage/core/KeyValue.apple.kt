package br.com.arch.toolkit.storage.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

actual fun <T> KeyValue<T>.instant(): T = runBlocking { current() } ?: lastValue
internal actual fun <T> KeyValue<T>.defaultScope() = CoroutineScope(Dispatchers.Default)
