package br.com.arch.toolkit.storage.core

actual fun <T> KeyValue<T>.instant(): T = lastValue
