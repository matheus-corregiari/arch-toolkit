@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyle

actual open class DebugTree : Lumber.Oak() {
    private val Lumber.Level.toStyle: TextStyle
        get() = when (this) {
            Lumber.Level.Error -> TextColors.brightRed
            Lumber.Level.Warn -> TextColors.brightYellow
            Lumber.Level.Info -> TextColors.brightBlue
            Lumber.Level.Debug -> TextColors.brightGreen
            Lumber.Level.Verbose -> TextColors.brightMagenta
            Lumber.Level.Assert -> TextColors.brightCyan
        }

    actual override fun isLoggable(tag: String?, level: Lumber.Level) = true

    actual override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?) {
        if (tag == null) {
            println(level.toStyle("[$level] -> $message"))
        } else {
            println(level.toStyle("[$level]-[$tag] -> $message"))
        }
    }
}
