package br.com.arch.toolkit.splinter.extension

fun <R> (() -> R).invokeCatching() = runCatching { invoke() }

fun <T, R> ((T) -> R).invokeCatching(data: T) = runCatching { invoke(data) }

suspend fun <R> (suspend () -> R).invokeCatching() = runCatching { invoke() }

suspend fun <T, R> (suspend (T) -> R).invokeCatching(data: T) = runCatching { invoke(data) }