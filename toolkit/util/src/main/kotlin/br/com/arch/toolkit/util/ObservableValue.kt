package br.com.arch.toolkit.util

class ObservableValue<T>(
    initialValue: T,
    private val getter: () -> T?,
    private val setter: ((T?) -> Unit)? = null,
)
