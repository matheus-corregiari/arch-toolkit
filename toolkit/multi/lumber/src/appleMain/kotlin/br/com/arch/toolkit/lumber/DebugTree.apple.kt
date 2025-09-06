@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyle

/**
 * # DebugTree (Apple - Darwin)
 *
 * This tree prints log messages to the standard output (`println`) with
 * ANSI colors applied using the [Mordant](https://github.com/ajalt/mordant) library
 * for better readability in terminal environments.
 *
 * Each [Lumber.Level] is mapped to a distinct color style:
 * - [Lumber.Level.Error]   → Bright Red
 * - [Lumber.Level.Warn]    → Bright Yellow
 * - [Lumber.Level.Info]    → Bright Blue
 * - [Lumber.Level.Debug]   → Bright Green
 * - [Lumber.Level.Verbose] → Bright Magenta
 * - [Lumber.Level.Assert]  → Bright Cyan
 *
 * The [isLoggable] method always returns `true`, ensuring all logs
 * are emitted without filtering.
 *
 * ### Example usage:
 * ```kotlin
 * Lumber.plant(DebugTree())
 * Lumber.i("Startup", "Application initialized")
 * ```
 *
 * Console output (with colors applied):
 * ```
 * [Info]-[Startup] -> Application initialized
 * ```
 *
 * @constructor Creates a debug tree that logs with colored output
 * for Apple/Darwin targets.
 * @see Lumber
 * @see Lumber.Oak
 */
actual open class DebugTree : Lumber.Oak() {

    /** Maps a [Lumber.Level] to a terminal [TextStyle] using Mordant colors. */
    private val Lumber.Level.toStyle: TextStyle
        get() = when (this) {
            Lumber.Level.Error -> TextColors.brightRed
            Lumber.Level.Warn -> TextColors.brightYellow
            Lumber.Level.Info -> TextColors.brightBlue
            Lumber.Level.Debug -> TextColors.brightGreen
            Lumber.Level.Verbose -> TextColors.brightMagenta
            Lumber.Level.Assert -> TextColors.brightCyan
        }

    /** Always returns `true`, allowing all logs to be emitted. */
    actual override fun isLoggable(tag: String?, level: Lumber.Level) = true

    /**
     * Prints the formatted log message to the standard output with colors.
     *
     * @param level The log level as defined in [Lumber.Level].
     * @param tag An optional tag identifying the log source.
     * @param message The log message content.
     * @param error An optional [Throwable] attached to the log (currently ignored).
     */
    actual override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?) {
        val formattedMessage = if (tag == null) {
            "[%s] -> %s".format(level.name, message)
        } else {
            "[%s]-[%s] -> %s".format(level.name, tag, message)
        }
        println(formattedMessage.lineSequence().map(level.toStyle::invoke).joinToString("\n"))
    }
}
