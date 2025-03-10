@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

import org.slf4j.LoggerFactory
import org.slf4j.event.Level as PlatLevel

actual open class DebugTree : Lumber.Oak() {

    private val Lumber.Level.toPlatLvl: PlatLevel
        get() = when (this) {
            Lumber.Level.Error -> PlatLevel.ERROR
            Lumber.Level.Warn -> PlatLevel.WARN
            Lumber.Level.Info -> PlatLevel.INFO
            Lumber.Level.Debug -> PlatLevel.DEBUG
            Lumber.Level.Verbose -> PlatLevel.TRACE
            Lumber.Level.Assert -> PlatLevel.TRACE
        }

    override fun isLoggable(tag: String?, level: Lumber.Level) =
        get(tag).isEnabledForLevel(level.toPlatLvl)

    override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?) =
        get(tag).atLevel(level.toPlatLvl)
            .setMessage(message)
            .run { error?.let { setCause(it) } ?: this }
            .log()

    private fun get(tag: String?) = LoggerFactory.getLogger(tag ?: javaClass.simpleName)
}
