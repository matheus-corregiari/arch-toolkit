@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "VariableNaming")

package br.com.arch.toolkit.splinter

expect open class Logger internal constructor(id: String, quiet: Boolean) {
    internal val id: String
    internal fun logInfo(message: String, error: Throwable? = null)
    internal fun logError(message: String, error: Throwable? = null)
    internal fun logWarning(message: String, error: Throwable? = null)
}
