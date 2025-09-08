@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

/**
 * # DebugTree (Kotlin/JS)
 *
 * This tree routes log messages to the native JavaScript `console`,
 * using different methods depending on the provided log [Lumber.Level].
 *
 * - [Lumber.Level.Verbose] → `console.log`
 * - [Lumber.Level.Debug]   → `console.log`
 * - [Lumber.Level.Info]    → `console.info`
 * - [Lumber.Level.Warn]    → `console.warn`
 * - [Lumber.Level.Error]   → `console.error`
 * - [Lumber.Level.Assert]  → `console.error`
 *
 * The [isLoggable] method always returns `true`, ensuring that all logs
 * are emitted without filtering.
 *
 * ### Example usage:
 * ```kotlin
 * Lumber.plant(DebugTree())
 * Lumber.d("Init", "Application successfully loaded")
 * ```
 *
 * Console output:
 * ```
 * DEBUG Init : Application successfully loaded
 * ```
 *
 * @constructor Creates a debug tree that logs into the JavaScript console.
 * @see Lumber
 * @see Lumber.Oak
 */
actual open class DebugTree actual constructor() : Lumber.Oak() {

    /** Always returns `true`, allowing all logs to be emitted. */
    actual override fun isLoggable(tag: String?, level: Lumber.Level) = true

    /**
     * Routes the log message to the native JavaScript `console`.
     *
     * @param level The log level as defined in [Lumber.Level].
     * @param tag An optional tag to identify the log source.
     * @param message The log message content.
     * @param error An optional [Throwable] attached to the log (currently ignored).
     */
    actual override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?) =
        when (level) {
            Lumber.Level.Verbose -> console.log("VERBOSE $tag : $message")
            Lumber.Level.Debug -> console.log("DEBUG $tag : $message")
            Lumber.Level.Info -> console.info("INFO $tag : $message")
            Lumber.Level.Warn -> console.warn("WARNING $tag : $message")
            Lumber.Level.Error -> console.error("ERROR $tag : $message")
            Lumber.Level.Assert -> console.error("ASSERT $tag : $message")
        }
}
