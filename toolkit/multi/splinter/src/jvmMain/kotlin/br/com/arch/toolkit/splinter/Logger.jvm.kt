@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "VariableNaming")

package br.com.arch.toolkit.splinter

actual open class Logger internal actual constructor(
    internal actual val id: String,
    private val quiet: Boolean
) {
    internal actual fun logInfo(message: String, error: Throwable?) = log(message, error)
    internal actual fun logError(message: String, error: Throwable?) = log(message, error)
    internal actual fun logWarning(message: String, error: Throwable?) = log(message, error)

    private fun log(message: String, error: Throwable?) {
        val tag = if (id.isBlank()) "Splinter" else "Splinter[$id]"
        val logMessage = "$tag - $message - ${error?.message}"
        if (quiet.not()) println(logMessage)
    }
}
