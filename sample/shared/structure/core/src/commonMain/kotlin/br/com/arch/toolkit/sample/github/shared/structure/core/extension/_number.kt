package br.com.arch.toolkit.sample.github.shared.structure.core.extension

import kotlin.math.pow
import kotlin.math.roundToLong

private val prefixes = arrayOf("", "K", "M", "B", "T")
private const val groupSize = 1000

fun Number.abbreviate(): String {
    var current = this.toDouble()
    var index = 0

    while (current >= groupSize && index < prefixes.size - 1) {
        current /= groupSize
        index++
    }

    return "${current.formatNumber(1)}${prefixes[index]}"
}

fun Number.formatNumber(decimals: Int): String {
    val rounded = formatWithDecimals(2)
    val parts = rounded.split('.')

    // Format the integer part
    val intPart = parts[0].reversed()
        .chunked("$groupSize".count { it == '0' })
        .joinToString(",").reversed()

    // Format the decimal part
    val decimal = if (parts.size > 1) parts[1] else ""
    val decimalPart = if (decimals > 0 && decimal.toInt() > 0) {
        ".${decimal.padEnd(decimals, '0').substring(0, decimals)}"
    } else {
        ""
    }

    return "$intPart$decimalPart"
}

private fun Number.formatWithDecimals(decimals: Int): String {
    val multiplier = 10.0.pow(decimals)
    val numberAsString = (toDouble() * multiplier)
        .roundToLong().toString().padStart(decimals + 1, '0')
    val decimalIndex = numberAsString.length - decimals - 1
    val mainRes = numberAsString.substring(0..decimalIndex)
    val fractionRes = numberAsString.substring(decimalIndex + 1)
    return if (fractionRes.isEmpty()) mainRes else "$mainRes.$fractionRes"
}
