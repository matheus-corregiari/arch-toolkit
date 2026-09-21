package br.com.arch.toolkit.sample.github.shared.structure.core.extension

import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import kotlinx.datetime.LocalDateTime

fun Long.localized(language: AppLanguage): String {
    val separator = if (language == AppLanguage.PORTUGUESE_BRAZIL) "." else ","
    val digits = toString().removePrefix(
        "-"
    ).reversed().chunked(DIGITS_PER_GROUP).joinToString(separator).reversed()
    return if (this < 0) "-$digits" else digits
}

fun LocalDateTime.localized(language: AppLanguage): String {
    val day = day.toString().padStart(2, '0')
    val month = (month.ordinal + 1).toString().padStart(2, '0')
    val date = if (language ==
        AppLanguage.PORTUGUESE_BRAZIL
    ) {
        "$day/$month/$year"
    } else {
        "$month/$day/$year"
    }
    return "$date ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

private const val DIGITS_PER_GROUP = 3
