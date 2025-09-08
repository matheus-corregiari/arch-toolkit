package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level.Error

class LumberErrorTest : LumberTest() {
    override val level: Lumber.Level = Error

    override fun Lumber.Oak.runLog(message: String, vararg args: Any?) =
        error(message = message, args = args)

    override fun Lumber.Oak.runLog(throwable: Throwable) = error(error = throwable)

    override fun Lumber.Oak.runLog(throwable: Throwable, message: String, vararg args: Any?) =
        error(error = throwable, message = message, args = args)
}
