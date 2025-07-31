@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.TextStyles
import org.slf4j.LoggerFactory
import org.slf4j.event.Level as PlatLevel

/**
 * A custom implementation of the `Lumber.Oak` class, specifically for logging
 * within a Kotlin Multiplatform (KMP) project. This implementation is designed
 * for the JVM target and utilizes the SLF4J logging framework.
 *
 * The class provides a custom mapping between `Lumber.Level` and `PlatLevel`
 * for logging at different levels. It overrides the `isLoggable` and `log`
 * methods from the parent class to integrate with the SLF4J logging system,
 * supporting multiple logging levels and messages.
 *
 * @constructor Creates a new instance of the `DebugTree` class.
 */
actual open class DebugTree : Lumber.Oak() {
    /**
     * Extension property to convert `Lumber.Level` to corresponding `PlatLevel`.
     * This is used to map the Lumber log levels to PlatLevel values. The levels
     * are mapped according to the SLF4J logging framework.
     *
     * @param `Lumber.Level` value representing the log level.
     * @return The corresponding `PlatLevel` value.
     */
    private val Lumber.Level.toPlatLvl: PlatLevel
        get() = when (this) {
            Lumber.Level.Error -> PlatLevel.ERROR
            Lumber.Level.Warn -> PlatLevel.WARN
            Lumber.Level.Info -> PlatLevel.INFO
            Lumber.Level.Debug -> PlatLevel.DEBUG
            Lumber.Level.Verbose -> PlatLevel.TRACE
            Lumber.Level.Assert -> PlatLevel.TRACE
        }

    /**
     * Extension property to convert `Lumber.Level` to corresponding `TextStyle`.
     * This is used to map the Lumber log levels to corresponding text styles.
     * The styles are used for formatting the log message.
     *
     * @param `Lumber.Level` value representing the log level.
     * @return The corresponding `TextStyle` value.
     */
    private val Lumber.Level.toStyle: TextStyle
        get() = when (this) {
            Lumber.Level.Error -> TextColors.brightRed
            Lumber.Level.Warn -> TextColors.brightYellow
            Lumber.Level.Info -> TextColors.brightBlue
            Lumber.Level.Debug -> TextColors.brightGreen
            Lumber.Level.Verbose -> TextColors.brightMagenta
            Lumber.Level.Assert -> TextColors.brightCyan
        }

    /**
     * Checks if a log message at the specified level should be logged.
     *
     * This method overrides `isLoggable` in the parent class. It uses the `get` method
     * to obtain a logger (using SLF4J) and checks if the log level is enabled for
     * the given logger.
     *
     * @param tag The tag associated with the log message, can be `null`.
     * @param level The log level for this log message.
     * @return `true` if logging is enabled for the specified level; `false` otherwise.
     */
    actual override fun isLoggable(tag: String?, level: Lumber.Level) =
        get(tag).isEnabledForLevel(level.toPlatLvl)

    /**
     * Logs a message at the specified level using the logger.
     *
     * This method overrides `log` from the parent class. It gets a logger (via SLF4J)
     * based on the provided `tag`, sets the message, and optionally sets an error cause.
     * The message is logged at the mapped `PlatLevel` level.
     *
     * @param level The log level to be used for this log message.
     * @param tag The tag associated with the log message, can be `null`.
     * @param message The log message to be logged.
     * @param error An optional `Throwable` error that can be logged as the cause.
     */
    actual override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?) =
        get(tag?.let { (TextStyles.bold + TextColors.gray)(it) })
            .atLevel(level.toPlatLvl)
            .setMessage(level.toStyle(message))
            .run { error?.let { setCause(it) } ?: this }
            .log()

    /**
     * Retrieves a logger for the specified tag.
     *
     * This helper method returns an SLF4J logger instance, using the `tag` if provided,
     * or the class name as the default if `tag` is `null`.
     *
     * @param tag The tag associated with the logger, can be `null`.
     * @return An SLF4J logger instance for the specified tag.
     */
    private fun get(tag: String?) =
        LoggerFactory.getLogger(
            tag?.let { (TextStyles.bold + TextColors.gray)(it) }
                ?: (TextStyles.dim + TextColors.gray)(javaClass.simpleName),
        )
}
