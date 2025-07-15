package br.com.arch.toolkit.splinter.util

import br.com.arch.toolkit.lumber.DebugTree
import br.com.arch.toolkit.lumber.Lumber.Level
import org.junit.Assert

class TestTree : DebugTree() {
    val history = mutableListOf<String>()

    override fun isLoggable(tag: String?, level: Level) = true

    override fun log(level: Level, tag: String?, message: String, error: Throwable?) {
        super.log(level, tag, message, error)
        history.add("$level - $tag - $message - $error")
    }

    fun assertAll(other: List<Any>) {
        Assert.assertEquals(other.size, history.size)
        other.forEachIndexed { index, data ->
            when (data) {
                is Regex -> Assert.assertTrue("Index: $$index", history[index].matches(data))
                else -> Assert.assertEquals(data, history[index])
            }
        }
    }
}
