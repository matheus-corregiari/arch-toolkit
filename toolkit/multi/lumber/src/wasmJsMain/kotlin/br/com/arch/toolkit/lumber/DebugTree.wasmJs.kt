@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

/**
 * # DebugTree (Wasm/JS)
 *
 * JavaScript/Wasm-specific implementation of [Lumber.Oak].
 * Logs are written directly to the browser (or Node.js) console
 * using the appropriate `console.*` function.
 *
 * Each [Lumber.Level] is mapped as follows:
 * - [Lumber.Level.Verbose] → `console.log`
 * - [Lumber.Level.Debug]   → `console.log`
 * - [Lumber.Level.Info]    → `console.info`
 * - [Lumber.Level.Warn]    → `console.warn`
 * - [Lumber.Level.Error]   → `console.error`
 * - [Lumber.Level.Assert]  → `console.error`
 *
 * ## Example
 * ```kotlin
 * Lumber.plant(DebugTree())
 *
 * Lumber.info("Hello from Wasm")
 * Lumber.error(Exception("oops"), "Something went wrong")
 * ```
 *
 * In the browser dev tools you will see:
 * ```
 * INFO - [null] : Hello from Wasm
 * ERROR - [null] : Something went wrong
 * ```
 *
 * @see jsLog
 * @see jsLogInfo
 * @see jsLogWarn
 * @see jsLogError
 */
actual open class DebugTree actual constructor() : Lumber.Oak() {

    /**
     * Always returns `true` for Wasm/JS.
     *
     * There is no log filtering available here.
     */
    actual override fun isLoggable(tag: String?, level: Lumber.Level): Boolean = true

    /**
     * Logs a message to the JS console, mapping [Lumber.Level] to
     * the appropriate `console.*` function.
     *
     * @param level The log level.
     * @param tag An optional tag, can be `null`.
     * @param message The log message.
     * @param error Optional exception. Currently **not** printed,
     *              but can be appended to the message or extended if needed.
     */
    actual override fun log(
        level: Lumber.Level,
        tag: String?,
        message: String,
        error: Throwable?
    ) = when (level) {
        Lumber.Level.Verbose -> jsLog("VERBOSE - [$tag] : $message")
        Lumber.Level.Debug -> jsLog("DEBUG - [$tag] : $message")
        Lumber.Level.Info -> jsLogInfo("[$tag] : $message")
        Lumber.Level.Warn -> jsLogWarn("[$tag] : $message")
        Lumber.Level.Error -> jsLogError("[$tag] : $message")
        Lumber.Level.Assert -> jsLogError("[$tag] : $message")
    }
}
