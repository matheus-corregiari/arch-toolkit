@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

actual open class DebugTree actual constructor() : Lumber.Oak() {

    actual override fun isLoggable(tag: String?, level: Lumber.Level) = true

    actual override fun log(
        level: Lumber.Level,
        tag: String?,
        message: String,
        error: Throwable?
    ) {
        jsConsole(levelMethod(level), message)
    }

    private fun levelMethod(level: Lumber.Level): String = when (level) {
        Lumber.Level.Verbose -> "debug"
        Lumber.Level.Debug -> "debug"
        Lumber.Level.Info -> "info"
        Lumber.Level.Warn -> "warn"
        Lumber.Level.Error -> "error"
        Lumber.Level.Assert -> "error"
    }
}

