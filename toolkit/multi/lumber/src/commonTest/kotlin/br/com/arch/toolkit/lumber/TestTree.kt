package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level
import kotlin.test.assertEquals

class TestTree(private val falseForLevel: Level? = null) : DebugTree() {
    val history = mutableListOf<Data>()

    override fun isLoggable(tag: String?, level: Level): Boolean {
        super.isLoggable(tag, level)
        return (level == falseForLevel).not()
    }

    override fun log(level: Level, tag: String?, message: String, error: Throwable?) {
        super.log(level, tag, message, error)
        history.add(Data(level, tag, message, error))
    }

    fun assertAll(vararg other: Data) {
        assertEquals(other.size, history.size)
        other.forEachIndexed { index, data -> assertEquals(data, history[index]) }
    }

    data class Data(
        private val level: Level,
        private val tag: String?,
        private val message: String,
        private val error: Throwable?
    )
}
