@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

internal actual class TargetRegularHolderImpl<T> actual constructor() :
    RegularHolderImpl<T>(),
    TargetRegularHolder<T>

internal actual class TargetResultHolderImpl<T : Any> actual constructor() :
    ResultHolderImpl<T>(),
    TargetResultHolder<T>
