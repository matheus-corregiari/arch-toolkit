package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level.Info

class LumberInfoTest : LumberTest() {
    override val level: Lumber.Level = Info

    override fun Lumber.Oak.runLog(message: String, vararg args: Any?) =
        info(message = message, args = args)

    override fun Lumber.Oak.runLog(throwable: Throwable) = info(error = throwable)

    override fun Lumber.Oak.runLog(throwable: Throwable, message: String, vararg args: Any?) =
        info(error = throwable, message = message, args = args)
}
