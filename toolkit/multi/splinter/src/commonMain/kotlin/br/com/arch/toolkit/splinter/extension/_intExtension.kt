@file:OptIn(ExperimentalAtomicApi::class)

package br.com.arch.toolkit.splinter.extension

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

private const val ID_MIN_WIDTH = 3

internal fun AtomicInt.incrementAsId() = incrementAndFetch().asId()

internal fun AtomicInt.asId() = load().asId()

private fun Int.asId() = "#" + "$this".padStart(ID_MIN_WIDTH, '0')
