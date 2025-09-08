package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level.Debug

class LumberDebugTest : LumberTest() {
    override val level: Lumber.Level = Debug

    override fun Lumber.Oak.runLog(message: String, vararg args: Any?) =
        debug(message = message, args = args)

    override fun Lumber.Oak.runLog(throwable: Throwable) = debug(error = throwable)

    override fun Lumber.Oak.runLog(throwable: Throwable, message: String, vararg args: Any?) =
        debug(error = throwable, message = message, args = args)
}
