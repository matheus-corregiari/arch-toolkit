@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalAtomicApi::class)

package br.com.arch.toolkit.lumber

import kotlin.concurrent.atomics.ExperimentalAtomicApi

actual fun defaultTag(exclude: Set<String>): String? = null
