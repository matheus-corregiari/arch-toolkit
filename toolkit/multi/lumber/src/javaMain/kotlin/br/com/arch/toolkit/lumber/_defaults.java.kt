@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

internal actual const val MAX_LOG_LENGTH: Int = 4000
internal actual const val MAX_TAG_LENGTH: Int = 25

actual class ThreadSafe<T> : ThreadLocal<T>()

@Suppress("ThrowingExceptionsWithoutMessageOrCause")
internal actual fun defaultTag(): String? {
    val ignore = fqcnIgnore.map { it.qualifiedName }
    return Throwable().stackTrace
        .firstOrNull { it.className.replace("$", ".") !in ignore }
        ?.let(::createStackElementTag)
        ?.chunked(MAX_TAG_LENGTH)?.first()
}

internal fun createStackElementTag(element: StackTraceElement): String {
    var tag = element.className.substringAfterLast('.')
    val matcher = "(\\$\\d+)+$".toPattern().matcher(tag)
    if (matcher.find()) tag = matcher.replaceAll("")
    return "$tag:${element.methodName.camelcase()}"
}
