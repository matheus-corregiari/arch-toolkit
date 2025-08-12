@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

actual class ThreadSafe<T> actual constructor() : ThreadLocal<T>()

@Suppress("ThrowingExceptionsWithoutMessageOrCause")
actual fun defaultTag(exclude: Set<String>): String? = Throwable()
    .stackTrace
    .first { it.className !in exclude }
    .let(::createStackElementTag)

actual fun String.format(vararg args: Any?): String = String.format(this, *args)

/**
 * Extract the tag which should be used for the message from the `element`. By default
 * this will use the class name without any anonymous class suffixes (e.g., `Foo$1`
 * becomes `Foo`).
 *
 * Note: This will not be called if a [manual tag][.tag] was specified.
 */
private fun createStackElementTag(element: StackTraceElement): String {
    var tag = element.className.substringAfterLast('.')
    val matcher = "(\\$\\d+)+$".toPattern().matcher(tag)
    if (matcher.find()) tag = matcher.replaceAll("")
    tag = tag.chunked(MAX_TAG_LENGTH).first()
    return "$tag:${element.methodName}"
}

