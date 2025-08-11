@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

expect interface TargetDataHolder<T> : ResponseDataHolder<T>

internal expect class ResultHolder<T>() : TargetDataHolder<T> {
    fun init(splinter: Splinter<T>)
}
