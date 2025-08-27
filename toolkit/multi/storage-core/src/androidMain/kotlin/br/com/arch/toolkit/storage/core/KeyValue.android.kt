package br.com.arch.toolkit.storage.core

import kotlinx.coroutines.runBlocking

actual fun <T> KeyValue<T>.instant(): T = runBlocking { current() } ?: lastValue
