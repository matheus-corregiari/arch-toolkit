package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level.Warn

class LumberWarnTest : LumberTest() {
    override val level: Lumber.Level = Warn

    override fun Lumber.Oak.runLog(message: String, vararg args: Any?) =
        warn(message = message, args = args)

    override fun Lumber.Oak.runLog(throwable: Throwable) = warn(error = throwable)

    override fun Lumber.Oak.runLog(throwable: Throwable, message: String, vararg args: Any?) =
        warn(error = throwable, message = message, args = args)
}
