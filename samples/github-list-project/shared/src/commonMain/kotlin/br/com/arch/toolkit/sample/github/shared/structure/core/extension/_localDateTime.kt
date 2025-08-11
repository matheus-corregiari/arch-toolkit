package br.com.arch.toolkit.sample.github.shared.structure.core.extension

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.byUnicodePattern

fun LocalDateTime.formatAsString() =
    LocalDateTime.Format { byUnicodePattern("dd/MM/yyyy - HH:mm") }.format(this)
