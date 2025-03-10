@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import android.util.Log

actual open class DebugTree : Lumber.Oak() {

    override fun isLoggable(tag: String?, level: Lumber.Level) = Log.isLoggable(
        tag, when (level) {
            Lumber.Level.Verbose -> Log.VERBOSE
            Lumber.Level.Debug -> Log.DEBUG
            Lumber.Level.Info -> Log.INFO
            Lumber.Level.Warn -> Log.WARN
            Lumber.Level.Error -> Log.ERROR
            Lumber.Level.Assert -> Log.ASSERT
        }
    )

    override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?) {
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
