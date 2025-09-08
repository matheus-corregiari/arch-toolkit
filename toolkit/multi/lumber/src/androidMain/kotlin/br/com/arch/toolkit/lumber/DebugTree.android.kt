@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import android.util.Log

/**
 * # DebugTree (Android)
 *
 * Android-specific implementation of [Lumber.Oak], delegating logs
 * to the Android [Log] framework.
 *
 * This is conceptually similar to Timber's `DebugTree`:
 * it prints all logs to Logcat, mapping [Lumber.Level] to
 * the corresponding Android priority.
 *
 * ## Example
 * ```kotlin
 * // Plant the DebugTree for Android
 * Lumber.plant(DebugTree())
 *
 * Lumber.debug("Debug message with id=%d", 42)
 * Lumber.error(Exception("Oops"), "Something went wrong")
 * ```
 *
 * @see android.util.Log
 * @see Lumber.Oak
 */
actual open class DebugTree : Lumber.Oak() {

    /**
     * Determines whether a log at the given [level] should be printed.
     *
     * Delegates to [Log.isLoggable], mapping [Lumber.Level] to the
     * corresponding Android priority:
     * - [Lumber.Level.Verbose] → [Log.VERBOSE]
     * - [Lumber.Level.Debug]   → [Log.DEBUG]
     * - [Lumber.Level.Info]    → [Log.INFO]
     * - [Lumber.Level.Warn]    → [Log.WARN]
     * - [Lumber.Level.Error]   → [Log.ERROR]
     * - [Lumber.Level.Assert]  → [Log.ASSERT]
     *
     * @param tag Optional tag, can be `null` (Android will use `"null"`).
     * @param level The logging level.
     * @return `true` if Android allows logging at this level, `false` otherwise.
     */
    actual override fun isLoggable(tag: String?, level: Lumber.Level) = Log.isLoggable(
        /* tag = */
        tag,
        /* level = */
        when (level) {
            Lumber.Level.Verbose -> Log.VERBOSE
            Lumber.Level.Debug -> Log.DEBUG
            Lumber.Level.Info -> Log.INFO
            Lumber.Level.Warn -> Log.WARN
            Lumber.Level.Error -> Log.ERROR
            Lumber.Level.Assert -> Log.ASSERT
        }
    )

    /**
     * Prints a log message at the given [level] using Android's [Log].
     *
     * Maps [Lumber.Level] to the corresponding Android method:
     * - [Lumber.Level.Verbose] → [Log.v]
     * - [Lumber.Level.Debug]   → [Log.d]
     * - [Lumber.Level.Info]    → [Log.i]
     * - [Lumber.Level.Warn]    → [Log.w]
     * - [Lumber.Level.Error]   → [Log.e]
     * - [Lumber.Level.Assert]  → [Log.wtf]
     *
     * @param level The logging level.
     * @param tag Optional tag (may be `null`).
     * @param message The message to log.
     * @param error Optional throwable to log.
     */
    actual override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?) {
        when (level) {
            Lumber.Level.Verbose -> Log.v(tag, message)
            Lumber.Level.Debug -> Log.d(tag, message)
            Lumber.Level.Info -> Log.i(tag, message)
            Lumber.Level.Warn -> Log.w(tag, message)
            Lumber.Level.Error -> Log.e(tag, message)
            Lumber.Level.Assert -> Log.wtf(tag, message)
        }
    }
}
