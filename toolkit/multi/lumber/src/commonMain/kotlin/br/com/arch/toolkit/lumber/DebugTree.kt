@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.lumber

/**
 * # DebugTree - Platform-specific Debug Logging Tree
 *
 * This is the common interface for the DebugTree implementation. It handles the log level and message formatting,
 * but the actual logging behavior will differ depending on the platform (JVM, Android, and others).
 *
 * ## Targets:
 * - **JVM**: Uses SLF4J or other JVM-based logging frameworks.
 * - **Android**: Uses Android's `Log` class.
 * - **Future Platforms**: The framework is designed to allow easy extension to additional platforms, such as iOS and JavaScript.
 *
 * ## Example (JVM Implementation):
 * ```kotlin
 * class MyDebugTree : DebugTree() {
 *     override fun log(level: Level, tag: String?, message: String, error: Throwable?) {
 *         // Platform-specific logging implementation (JVM using SLF4J)
 *     }
 * }
 * ```
 */
expect open class DebugTree() : Lumber.Oak {

    /**
     * Determines whether a log message at the specified level should be logged.
     * This function will be implemented differently for each platform.
     *
     * @param tag The tag to associate with the log message.
     * @param level The logging level.
     * @return Whether the message is loggable at the specified level.
     */
    override fun isLoggable(tag: String?, level: Lumber.Level): Boolean

    /**
     * Logs a message at the specified level, possibly including an error (Throwable).
     * The implementation of this function will be platform-specific, using different logging systems.
     *
     * @param level The logging level.
     * @param tag The tag associated with the log message.
     * @param message The log message.
     * @param error An optional throwable (error) to be logged.
     */
    override fun log(level: Lumber.Level, tag: String?, message: String, error: Throwable?)
}
