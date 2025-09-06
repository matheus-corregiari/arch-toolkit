package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level.Verbose

class LumberVerboseTest : LumberTest() {
    override val level: Lumber.Level = Verbose

    override fun Lumber.Oak.runLog(message: String, vararg args: Any?) =
        verbose(message = message, args = args)

    override fun Lumber.Oak.runLog(throwable: Throwable) = verbose(error = throwable)

    override fun Lumber.Oak.runLog(throwable: Throwable, message: String, vararg args: Any?) =
        verbose(error = throwable, message = message, args = args)
}
