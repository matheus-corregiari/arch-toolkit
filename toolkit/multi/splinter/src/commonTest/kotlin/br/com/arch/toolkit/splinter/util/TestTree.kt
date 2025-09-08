package br.com.arch.toolkit.splinter.util

import br.com.arch.toolkit.lumber.DebugTree
import br.com.arch.toolkit.lumber.Lumber.Level
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestTree : DebugTree() {
    val history = mutableListOf<String>()

    override fun isLoggable(tag: String?, level: Level) = true

    override fun log(level: Level, tag: String?, message: String, error: Throwable?) {
        super.log(level, tag, message, error)
        history.add("$level - $tag - $message - $error")
    }

    fun assertAll(other: List<Any>) {
        assertEquals(other.size, history.size)
        other.forEachIndexed { index, data ->
            when (data) {
                is Regex -> assertTrue("Index: $$index", { history[index].matches(data) })
                else -> assertEquals(data, history[index])
            }
        }
    }
}
