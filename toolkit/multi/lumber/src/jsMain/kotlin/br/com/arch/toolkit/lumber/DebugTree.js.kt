@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

actual open class DebugTree actual constructor() : Lumber.Oak() {

    actual override fun isLoggable(tag: String?, level: Lumber.Level) = true

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
