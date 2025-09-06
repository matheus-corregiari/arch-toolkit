package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level.Info

class LumberLogTest : LumberTest() {
    override val level: Lumber.Level = Info

    override fun Lumber.Oak.runLog(message: String, vararg args: Any?) =
        log(level = Info, message = message, args = args)

    override fun Lumber.Oak.runLog(throwable: Throwable) = log(level = Info, error = throwable)

    override fun Lumber.Oak.runLog(throwable: Throwable, message: String, vararg args: Any?) =
        log(level = Info, error = throwable, message = message, args = args)
}
