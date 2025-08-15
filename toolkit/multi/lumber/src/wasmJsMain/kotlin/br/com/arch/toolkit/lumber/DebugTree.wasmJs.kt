@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

actual open class DebugTree actual constructor() : Lumber.Oak() {

    actual override fun isLoggable(tag: String?, level: Lumber.Level) = true

    actual override fun log(
        level: Lumber.Level,
        tag: String?,
        message: String,
        error: Throwable?
    ) = when (level) {
        Lumber.Level.Verbose -> jsLog("VERBOSE - [$tag] : $message")
        Lumber.Level.Debug -> jsLog("DEBUG - [$tag] : $message")
        Lumber.Level.Info -> jsLogInfo("[$tag] : $message")
        Lumber.Level.Warn -> jsLogWarn("[$tag] : $message")
        Lumber.Level.Error -> jsLogError("[$tag] : $message")
        Lumber.Level.Assert -> jsLogError("[$tag] : $message")
    }
}
