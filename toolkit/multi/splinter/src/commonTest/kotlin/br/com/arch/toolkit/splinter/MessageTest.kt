package br.com.arch.toolkit.splinter

import kotlin.test.Test
import kotlin.test.assertEquals

class MessageTest {

    @Test
    fun logTagsKeepTheirIndentationOnEveryPlatform() {
        val tags = mapOf(
            "[Splinter]" to "",
            "[Apprentice #001]" to "-- ",
            "[OneShot]" to "-- -- ",
            "[Mirror]" to "-- -- ",
            "[Polling]" to "-- -- ",
            "[Cache]" to "-- -- -- ",
            "[Custom]" to "-- -- -- -- "
        )
        tags.forEach { (tag, indent) ->
            val message = "$tag example"
            assertEquals(indent + message, Splinter.Message.info(message).indentedMessage)
        }
        assertEquals("untagged", Splinter.Message.info("untagged").indentedMessage)
    }
}
