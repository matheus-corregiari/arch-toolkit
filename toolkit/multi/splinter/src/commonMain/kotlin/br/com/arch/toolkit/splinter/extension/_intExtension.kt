@file:OptIn(ExperimentalAtomicApi::class)

package br.com.arch.toolkit.splinter.extension

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

internal fun AtomicInt.incrementAsId() = incrementAndFetch().asId()
internal fun AtomicInt.asId() = load().asId()
private fun Int.asId() = "#" + "$this".padStart(3, '0')
