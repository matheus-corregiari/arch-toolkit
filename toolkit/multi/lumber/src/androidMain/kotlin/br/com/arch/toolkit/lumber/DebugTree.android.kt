@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import android.util.Log

/**
 * A custom implementation of the `Lumber.Oak` class, specifically for logging
 * within a Kotlin Multiplatform (KMP) project. This implementation is designed
 * for the Android target and utilizes the Android `Log` class for logging at
 * different levels.
 *
 * The class overrides the `isLoggable` and `log` methods from the parent class
 * to integrate with Android's native logging system (`android.util.Log`), supporting
 * multiple logging levels and messages.
 *
 * @constructor Creates a new instance of the `DebugTree` class for Android.
 */
actual open class DebugTree : Lumber.Oak() {

    /**
     * Checks if a log message at the specified level should be logged using
     * Android's `Log.isLoggable` method.
     *
     * This method overrides `isLoggable` in the parent class. It maps the
     * `Lumber.Level` to the corresponding Android log level and checks if
     * logging is enabled for that level.
     *
     * @param tag The tag associated with the log message, can be `null`.
     * @param level The log level for this log message.
     * @return `true` if logging is enabled for the specified level; `false` otherwise.
     */
    actual override fun isLoggable(tag: String?, level: Lumber.Level) = Log.isLoggable(
        tag, when (level) {
            Lumber.Level.Verbose -> Log.VERBOSE
            Lumber.Level.Debug -> Log.DEBUG
            Lumber.Level.Info -> Log.INFO
            Lumber.Level.Warn -> Log.WARN
            Lumber.Level.Error -> Log.ERROR
            Lumber.Level.Assert -> Log.ASSERT
        }
    )

    /**
     * Logs a message at the specified level using Android's `Log` class.
     *
     * This method overrides `log` from the parent class. It maps the
     * `Lumber.Level` to the appropriate Android log method (`Log.v`, `Log.d`,
     * `Log.i`, etc.) and logs the message, along with an optional error cause.
     *
     * @param level The log level to be used for this log message.
     * @param tag The tag associated with the log message, can be `null`.
     * @param message The log message to be logged.
     * @param error An optional `Throwable` error that can be logged as the cause.
     */
    actual override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?) {
        when (level) {
            Lumber.Level.Verbose -> Log.v(tag, message, error)
            Lumber.Level.Debug -> Log.d(tag, message, error)
            Lumber.Level.Info -> Log.i(tag, message, error)
            Lumber.Level.Warn -> Log.w(tag, message, error)
            Lumber.Level.Error -> Log.e(tag, message, error)
            Lumber.Level.Assert -> Log.wtf(tag, message, error)
        }
    }
}
