@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "VariableNaming")

package br.com.arch.toolkit.splinter

import timber.log.Timber

actual open class Logger internal actual constructor(
    internal actual val id: String,
    private val quiet: Boolean
) {

    private val tag = if (id.isBlank()) "Splinter" else "Splinter[$id]"
    private val timber = Timber.tag(tag).takeIf { quiet.not() }

    internal actual fun logInfo(message: String, error: Throwable?) =
        timber?.i(error, message) ?: Unit

    internal actual fun logError(message: String, error: Throwable?) =
        timber?.e(error, message) ?: Unit

    internal actual fun logWarning(message: String, error: Throwable?) =
        timber?.w(error, message) ?: Unit
}
