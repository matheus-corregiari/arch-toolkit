@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import kotlinx.coroutines.CoroutineScope

internal expect class ResultHolderImpl<T>(
    scope: () -> CoroutineScope,
    running: () -> Boolean,
) : ResultHolder<T>

internal expect class RegularHolderImpl<T>() : RegularHolder<T>
