package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level.Assert

class LumberAssertTest : LumberTest() {
    override val level: Lumber.Level = Assert

    override fun Lumber.Oak.runLog(message: String, vararg args: Any?) =
        wtf(message = message, args = args)

    override fun Lumber.Oak.runLog(throwable: Throwable) = wtf(error = throwable)

    override fun Lumber.Oak.runLog(throwable: Throwable, message: String, vararg args: Any?) =
        wtf(error = throwable, message = message, args = args)
}
