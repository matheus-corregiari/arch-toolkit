package br.com.arch.toolkit.livedata

internal fun async(block: () -> Unit) = Thread(block).apply { start() }