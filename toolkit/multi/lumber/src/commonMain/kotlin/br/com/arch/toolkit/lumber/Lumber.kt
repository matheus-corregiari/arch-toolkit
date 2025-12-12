@file:Suppress("unused", "TooManyFunctions")

package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.Level.Assert
import br.com.arch.toolkit.lumber.Lumber.Level.Debug
import br.com.arch.toolkit.lumber.Lumber.Level.Error
import br.com.arch.toolkit.lumber.Lumber.Level.Info
import br.com.arch.toolkit.lumber.Lumber.Level.Verbose
import br.com.arch.toolkit.lumber.Lumber.Level.Warn
import br.com.arch.toolkit.lumber.Lumber.OakWood.forest
import br.com.arch.toolkit.lumber.Lumber.OakWood.maxLogLength
import br.com.arch.toolkit.lumber.Lumber.OakWood.maxTagLength
import br.com.arch.toolkit.lumber.Lumber.OakWood.plant
import br.com.arch.toolkit.lumber.Lumber.OakWood.quiet
import br.com.arch.toolkit.lumber.Lumber.OakWood.tag
import br.com.arch.toolkit.lumber.Lumber.OakWood.uproot
import br.com.arch.toolkit.lumber.Lumber.OakWood.uprootAll
import kotlinx.coroutines.sync.Mutex

/**
 * # Lumber - A Lightweight Logging Library for Kotlin Multiplatform
 *
 * Lumber is a modern, Kotlin Multiplatform (KMP) logging library inspired by
 * [Timber by Jake Wharton](https://github.com/JakeWharton/timber).
 *
 * Unlike Timber (JVM/Android only), Lumber works across multiple targets:
 * - **JVM** (desktop, backend)
 * - **Android**
 * - **Kotlin/Native** (iOS, Linux, Windows)
 * - **Kotlin/JS & Wasm**
 *
 * ## Key Features
 * - Idiomatic Kotlin API with extension-friendly design.
 * - Multiple log levels ([Level.Verbose], [Level.Debug], [Level.Info], [Level.Warn], [Level.Error], [Level.Assert]).
 * - Delegation to multiple logging trees ([Oak]) via the [OakWood] singleton.
 * - One-time [tag] and [quiet] controls for flexible output.
 * - Automatic message splitting if longer than [MAX_LOG_LENGTH].
 * - Simple string formatting (`%s`, `%d`), independent of platform.
 *
 * ## Example Usage
 * ```kotlin
 * // Plant a custom Oak
 * Lumber.plant(ConsoleOak())
 *
 * // Simple log
 * Lumber.info("User loaded")
 *
 * // Formatted log
 * Lumber.debug("User %s took %dms to load", username, duration)
 *
 * // Logging with exception
 * try {
 *     doFail()
 * } catch (ex: Exception) {
 *     Lumber.error(ex, "Something went wrong with user %s", username)
 * }
 *
 * // One-time tag
 * Lumber.tag("Auth").warn("User session expired")
 *
 * // Suppress one log
 * Lumber.quiet(true).debug("This will not be logged")
 * ```
 *
 * ## Best Practices
 * - Plant at least one [Oak] before logging, otherwise logs are ignored.
 * - Use different [Oak] implementations for different outputs (console, file, remote).
 * - Use `tag()` sparingly to annotate context; don’t overuse as it resets after one call.
 * - Avoid expensive string concatenation; rely on the formatting mechanism.
 *
 * @author Matheus Corregiari
 */
class Lumber private constructor() {
    init {
        throw AssertionError("No instances allowed.")
    }

    /**
     * Defines the severity level of a log message.
     *
     * Each level indicates intent and should be used consistently across the app:
     *
     * - [Verbose] → **lowest priority**. Use for extremely detailed logs
     *   (e.g., internal variable dumps, step-by-step flows).
     *
     *   ```kotlin
     *   Lumber.verbose("Loop iteration=%d, value=%s", i, value)
     *   ```
     *
     * - [Debug] → Developer-facing messages useful during development
     *   but usually turned off in production.
     *
     *   ```kotlin
     *   Lumber.debug("Loaded user profile for %s", userId)
     *   ```
     *
     * - [Info] → High-level events that describe the normal flow of the app.
     *   Safe to keep enabled in production.
     *
     *   ```kotlin
     *   Lumber.info("App started in %d ms", startupTime)
     *   ```
     *
     * - [Warn] → Something unexpected happened or a potential issue was detected,
     *   but the app is still working correctly.
     *
     *   ```kotlin
     *   Lumber.warn("Cache miss for key=%s, falling back to network", key)
     *   ```
     *
     * - [Error] → A failure occurred that needs attention, usually accompanied by an exception.
     *
     *   ```kotlin
     *   try {
     *       fetchData()
     *   } catch (ex: IOException) {
     *       Lumber.error(ex, "Network request failed")
     *   }
     *   ```
     *
     * - [Assert] → Highest priority. Use for conditions that should **never** happen
     *   (fatal errors, invariant violations).
     *
     *   ```kotlin
     *   checkNotNull(user) ?: Lumber.wtf("User must not be null here!")
     *   ```
     */
    enum class Level {
        /** Extremely detailed logging, usually disabled in production. */
        Verbose,

        /** Debug information for developers, disabled in release builds. */
        Debug,

        /** General information about app state and high-level events. */
        Info,

        /** Something unexpected but not fatal. */
        Warn,

        /** Recoverable or unrecoverable errors, often with exceptions. */
        Error,

        /** Critical failures that should never happen (What a Terrible Failure) */
        Assert
    }

    /**
     * # Oak - Abstract Logging Tree
     *
     * Base class for logging destinations ("trees").
     * Extend [Oak] to implement your own logging strategy:
     * - console printing,
     * - file logging,
     * - network / crash reporting,
     * - etc.
     *
     * ## Example
     * ```kotlin
     * class ConsoleOak : Lumber.Oak() {
     *     override fun isLoggable(tag: String?, level: Level) = true
     *     override fun log(level: Level, tag: String?, message: String, error: Throwable?) {
     *         println("$level [$tag]: $message")
     *         error?.printStackTrace()
     *     }
     * }
     *
     * Lumber.plant(ConsoleOak())
     * Lumber.debug("Debugging with %s", "Lumber")
     * ```
     *
     * @see OakWood for the singleton dispatcher that manages multiple [Oak] instances.
     */
    abstract class Oak {

        private val explicitTag = ThreadSafe<String?>()
        private val explicitQuiet = ThreadSafe<Boolean?>()
        private val explicitMaxLogLength = ThreadSafe<Int?>()
        private val explicitMaxTagLength = ThreadSafe<Int?>()

        /**
         * Optional one-time tag. Set via [tag], consumed after one use.
         */
        protected open val tag: String?
            get() = explicitTag
                .get()
                .takeIf { it.isNullOrBlank().not() }
                ?.also { explicitTag.remove() }

        /**
         * Optional one-time quiet flag. Set via [quiet], consumed after one use.
         */
        protected open val quiet: Boolean
            get() = explicitQuiet.get()?.also { explicitQuiet.remove() } == true

        /**
         * Optional one-time max tag length. Set via [maxTagLength], consumed after one use.
         */
        protected open val maxTagLength: Int?
            get() = explicitMaxTagLength.get()?.also { explicitMaxTagLength.remove() }

        /**
         * Optional one-time max log length. Set via [maxLogLength], consumed after one use.
         */
        protected open val maxLogLength: Int?
            get() = explicitMaxLogLength.get()?.also { explicitMaxLogLength.remove() }

        /**
         * Sets a one-time tag to be used for the next logging call on this specific [Oak].
         * This tag helps identify the source of the log, making it easier to trace.
         * The tag is temporary and only affects the immediate next log message.
         *
         * The tag is stored using a [ThreadSafe] to ensure it is only applied for the current thread
         * and is cleared automatically after the log call.
         *
         * @param tag The tag to attach to the next log message.
         * @return The [Oak] instance for method chaining.
         *
         * ## Example:
         * ```kotlin
         * Lumber.tag("MyActivity").debug("Debug message")
         * // Expected output: Debug: [MyActivity] Debug message
         * ```
         */
        open fun tag(tag: String): Oak {
            explicitTag.set(tag.trimEnd().trimStart())
            return this
        }

        /**
         * Sets a one-time quiet flag to be used for the next logging call on this specific [Oak].
         * When enabled, some loggers might skip logging the message based on their implementation.
         * The quiet flag is temporary and only affects the immediate next log message.
         *
         * The flag is stored using a [ThreadSafe] to ensure it is only applied for the current thread
         * and is cleared automatically after the log call.
         *
         * @param quiet True to enable quiet mode for the next log call; false otherwise.
         * @return The [Oak] instance for method chaining.
         *
         * ## Example:
         * ```kotlin
         * Lumber.quiet(true).error("This error might be ignored by some Oaks.");
         * // Expected output: <no output>
         * ```
         */
        open fun quiet(quiet: Boolean): Oak {
            explicitQuiet.set(quiet)
            return this
        }

        /**
         * Sets a one-time max log length to be used for the next logging call on this specific [Oak].
         * When enabled, some loggers might skip logging the message based on their implementation.
         * The quiet flag is temporary and only affects the immediate next log message.
         *
         * The flag is stored using a [ThreadSafe] to ensure it is only applied for the current thread
         * and is cleared automatically after the log call.
         *
         * @param maxLogLength The maximum length of the log message.
         * @return The [Oak] instance for method chaining.
         *
         * ## Example:
         * ```kotlin
         * Lumber.maxLogLength(10).tag("Tag").debug("Debug message");
         * // Expected output:
         * Debug: [Tag #0] Debug mess
         * Debug: [Tag #1] age
         * ```
         */
        open fun maxLogLength(maxLogLength: Int): Oak {
            require(maxLogLength > 0) { "maxLogLength must be positive" }
            explicitMaxLogLength.set(maxLogLength)
            return this
        }

        /**
         * Sets a one-time max log length to be used for the next logging call on this specific [Oak].
         * When enabled, some loggers might skip logging the message based on their implementation.
         * The quiet flag is temporary and only affects the immediate next log message.
         *
         * The flag is stored using a [ThreadSafe] to ensure it is only applied for the current thread
         * and is cleared automatically after the log call.
         *
         * @param quiet True to enable quiet mode for the next log call; false otherwise.
         * @return The [Oak] instance for method chaining.
         *
         * ## Example:
         * ```kotlin
         * Lumber.maxTagLength(3).tag("LongTag").debug("Debug message");
         * // Expected output:
         * Debug: [Lon] Debug message
         * ```
         */
        open fun maxTagLength(maxTagLength: Int): Oak {
            require(maxTagLength > 0) { "maxTagLength must be positive" }
            explicitMaxTagLength.set(maxTagLength)
            return this
        }

        //region Verbose
        /** Log a [Level.Verbose] message. */
        open fun verbose(message: String, vararg args: Any?) =
            log(level = Verbose, message = message, args = args)

        /** Log a [Level.Verbose] exception only. */
        open fun verbose(error: Throwable) =
            log(level = Verbose, error = error)

        /** Log a [Level.Verbose] exception with message. */
        open fun verbose(error: Throwable, message: String, vararg args: Any?) =
            log(level = Verbose, error = error, message = message, args = args)
        //endregion

        //region Debug
        /** Log a [Level.Debug] message. */
        open fun debug(message: String, vararg args: Any?) =
            log(level = Debug, message = message, args = args)

        /** Log a [Level.Debug] exception only. */
        open fun debug(error: Throwable) =
            log(level = Debug, error = error)

        /** Log a [Level.Debug] exception with message. */
        open fun debug(error: Throwable, message: String, vararg args: Any?) =
            log(level = Debug, error = error, message = message, args = args)
        //endregion

        //region Info
        /** Log a [Level.Info] message. */
        open fun info(message: String, vararg args: Any?) =
            log(level = Info, message = message, args = args)

        /** Log a [Level.Info] exception only. */
        open fun info(error: Throwable) =
            log(level = Info, error = error)

        /** Log a [Level.Info] exception with message. */
        open fun info(error: Throwable, message: String, vararg args: Any?) =
            log(level = Info, error = error, message = message, args = args)
        //endregion

        //region Warn
        /** Log a [Level.Warn] message. */
        open fun warn(message: String, vararg args: Any?) =
            log(level = Warn, message = message, args = args)

        /** Log a [Level.Warn] exception only. */
        open fun warn(error: Throwable) =
            log(level = Warn, error = error)

        /** Log a [Level.Warn] exception with message. */
        open fun warn(error: Throwable, message: String, vararg args: Any?) =
            log(level = Warn, error = error, message = message, args = args)
        //endregion

        //region Error
        /** Log a [Level.Error] message. */
        open fun error(message: String, vararg args: Any?) =
            log(level = Error, message = message, args = args)

        /** Log a [Level.Error] exception only. */
        open fun error(error: Throwable) =
            log(level = Error, error = error)

        /** Log a [Level.Error] exception with message. */
        open fun error(error: Throwable, message: String, vararg args: Any?) =
            log(level = Error, error = error, message = message, args = args)
        //endregion

        //region Assert
        /** Log a [Level.Assert] message. */
        open fun wtf(message: String, vararg args: Any?) =
            log(level = Assert, message = message, args = args)

        /** Log a [Level.Assert] exception only. */
        open fun wtf(error: Throwable) =
            log(level = Assert, error = error)

        /** Log a [Level.Assert] exception with message. */
        open fun wtf(error: Throwable, message: String, vararg args: Any?) =
            log(level = Assert, error = error, message = message, args = args)
        //endregion

        //region Raw Log

        /** Log a message at [level] with optional formatting args. */
        open fun log(level: Level, message: String, vararg args: Any?) =
            log(level = level, error = null, message = message, args = args)

        /** Log an exception only. */
        open fun log(level: Level, error: Throwable) =
            log(level = level, error = error, message = null, args = emptyArray())

        /** Log exception + optional message with args. */
        open fun log(level: Level, error: Throwable?, message: String?, vararg args: Any?) =
            prepareLog(level = level, error = error, message = message, args = args)
        //endregion

        /**
         * Determines whether a log at [level] should be written.
         * Override to filter logs (e.g. only >= Warn).
         */
        protected abstract fun isLoggable(tag: String?, level: Level): Boolean

        /**
         * Performs the actual logging.
         *
         * @param level The log level.
         * @param tag The optional tag.
         * @param message The formatted message (never null here).
         * @param error Optional exception.
         */
        protected abstract fun log(level: Level, tag: String?, message: String, error: Throwable?)

        private fun prepareLog(
            level: Level,
            error: Throwable?,
            message: String?,
            vararg args: Any?,
        ) {
            // Consume tag even when message is not loggable so that next message is correctly tagged.
            val tag = (tag ?: defaultTag())?.chunked(maxTagLength ?: MAX_TAG_LENGTH)?.first()
            if (!isLoggable(tag, level) || quiet) return

            var formattedMessage = message
            if (formattedMessage.isNullOrEmpty()) {
                // Swallow message if it's null and there's no throwable.
                if (error == null) return
                formattedMessage = error.stackTraceToString()
            } else {
                formattedMessage = formattedMessage.format(*args)
                if (error != null) formattedMessage += "\n\n" + error.stackTraceToString()
            }
            val logLength = maxLogLength ?: MAX_LOG_LENGTH
            if (formattedMessage.length <= logLength) {
                log(level = level, tag = tag, message = formattedMessage, error = error)
            } else {
                formattedMessage.chunked(logLength).forEachIndexed { index, part ->
                    val newTag = tag?.let { "$it #$index" } ?: "#$index"
                    log(level = level, tag = newTag, message = part.trimStart('\n'), error = error)
                }
            }
        }
    }

    /**
     * # OakWood - Forest Manager
     *
     * Singleton dispatcher that holds and manages multiple [Oak] trees.
     * All calls to [Lumber] are delegated here.
     *
     * - Call [plant] to add one or more [Oak] instances.
     * - Call [uproot] or [uprootAll] to remove trees.
     * - Call [forest] to inspect planted trees.
     *
     * ## Example
     * ```kotlin
     * val console = ConsoleOak()
     * val remote = RemoteOak()
     * Lumber.plant(console, remote)
     *
     * Lumber.warn("Something happened") // logged to both trees
     * ```
     */
    companion object OakWood : Oak() {
        // Holds all the planted Oak trees.
        private val trees = mutableSetOf<Oak>()
        private val mutex = Mutex()

        /**
         * The number of currently planted Oak trees.
         * @return the count of Oak trees in the forest.
         */
        val treeCount: Int get() = trees.size

        override fun log(level: Level, tag: String?, message: String, error: Throwable?): Unit =
            error(message = "OakWood does not implement direct logging; use its dispatcher instead.")

        /**
         * Dispatches the log message to all planted Oaks.
         *
         * @param level The logging level for the message.
         * @param message The log message.
         * @param error An optional throwable to be logged.
         *
         * ## Example:
         * ```kotlin
         * // Assume two Oaks: consoleOak and fileOak are planted
         * Lumber.debug(message = "Debug message", error = Exception("Example Exception"))
         * // ConsoleOak: Debug message
         * // FileOak: Debug message
         * ```
         */
        override fun log(level: Level, error: Throwable?, message: String?, vararg args: Any?) {
            trees.forEach {
                it.log(level = level, error = error, message = message, args = args)
            }
        }

        /**
         * Returns true for all levels, allowing logging for all cases.
         * Override to implement any filtering logic if necessary.
         *
         * @param tag The tag associated with the log message.
         * @param level The logging level.
         * @return Always returns true for logging.
         *
         * ## Example:
         * ```kotlin
         * Lumber.isLoggable("MyTag", Level.Debug) // returns true
         * ```
         */
        override fun isLoggable(tag: String?, level: Level): Boolean = true

        /**
         * Sets a one-time tag to be used for the next logging call on all planted Oaks.
         * This method propagates the tag to every individual Oak managed by OakWood.
         * The tag is stored in each Oak using a thread-local to ensure thread safety and is cleared after the log call.
         *
         * @param tag The tag to attach to the next log message for all Oaks.
         * @return The OakWood instance for method chaining.
         *
         * ## Example:
         * ```kotlin
         * Lumber.tag("MyActivity").debug("Debug message")
         * // Expected output for ConsoleOak: Debug: [MyActivity] Debug message
         * // Expected output for FileOak: Debug: [MyActivity] Debug message
         * ```
         */
        override fun tag(tag: String): Oak {
            // Propagate the tag to all Oaks.
            trees.forEach { it.tag(tag) }
            return this
        }

        /**
         * Sets a one-time quiet flag to be used for the next logging call on all planted Oaks.
         * This method propagates the quiet flag to every individual Oak managed by OakWood.
         * The flag is stored using a thread-local to ensure thread safety and is cleared after the log call.
         *
         * @param quiet True to enable quiet mode for the next log call in all Oaks; false otherwise.
         * @return The OakWood instance for method chaining.
         *
         * ## Example:
         * ```kotlin
         * Lumber.quiet(true).error("This error might be ignored by some Oaks.")
         * // No output in quiet mode if the Oaks implement this feature.
         * ```
         */
        override fun quiet(quiet: Boolean): Oak {
            // Propagate the quiet flag to all Oaks.
            trees.forEach { it.quiet(quiet) }
            return this
        }

        /**
         * Sets a one-time max log length to be used for the next logging call on all planted Oaks.
         * This method propagates the max log length to every individual Oak managed by OakWood.
         * The flag is stored using a thread-local to ensure thread safety and is cleared after the log call.
         *
         * @param maxLogLength The maximum length of the log message for all Oaks.
         * @return The OakWood instance for method chaining.
         *
         * ## Example:
         * ```kotlin
         * Lumber.maxLogLength(10).tag("Tag").debug("Debug message")
         * // Expected output for ConsoleOak:
         * // Debug: [Tag #0] Debug mess
         * // Debug: [Tag #1] age
         *
         * // Expected output for FileOak:
         * // Debug: [Tag #0] Debug mess
         * // Debug: [Tag #1] age
         * ```
         */
        override fun maxLogLength(maxLogLength: Int): Oak {
            // Propagate the maxTagLength to all Oaks.
            trees.forEach { it.maxLogLength(maxLogLength) }
            return this
        }

        /**
         * Sets a one-time max tag length to be used for the next logging call on all planted Oaks.
         * This method propagates the max tag length to every individual Oak managed by OakWood.
         * The flag is stored using a thread-local to ensure thread safety and is cleared after the log call.
         *
         * @param maxTagLength The maximum length of the tag for all Oaks.
         * @return The OakWood instance for method chaining.
         *
         * ## Example:
         * ```kotlin
         * Lumber.maxTagLength(3).tag("LongTag").debug("Debug message")
         * // Expected output for ConsoleOak:
         * // Debug: [Lon] Debug message
         *
         * // Expected output for FileOak:
         * // Debug: [Lon] Debug message
         * ```
         */
        override fun maxTagLength(maxTagLength: Int): Oak {
            // Propagate the maxTagLength to all Oaks.
            trees.forEach { it.maxTagLength(maxTagLength) }
            return this
        }


        /**
         * Plants new logging trees into the forest.
         * Accepts one or more Oak instances and adds them to the logging system.
         *
         * @param trees An array of Oak instances to be added to the forest.
         * @throws IllegalArgumentException if trying to plant the same OakWood instance.
         *
         * ## Example:
         * ```kotlin
         * val consoleOak = ConsoleOak() // A custom Oak implementation
         * val fileOak = FileOak() // Another custom Oak implementation
         * Lumber.plant(consoleOak, fileOak) // Adds both Oaks to the forest
         * Lumber.debug("Message to consoleOak and fileOak")
         * ```
         */
        fun plant(tree: Oak, vararg trees: Oak) = apply {
            val allTrees = listOf(tree, *trees)
            allTrees.forEach { require(it !== this) { "Cannot plant Lumber itself." } }
            mutex.synchronized(trees) { this.trees.addAll(allTrees) }
        }

        /**
         * Uproots a specific logging tree.
         *
         * @param tree The tree to be removed from the forest.
         *
         * ## Example:
         * ```kotlin
         * val consoleOak = ConsoleOak()
         * Lumber.plant(consoleOak) // Adds consoleOak to the forest
         * Lumber.uproot(consoleOak) // Removes consoleOak from the forest
         * ```
         */
        fun uproot(tree: Oak) = apply {
            mutex.synchronized(trees) { trees.remove(tree) }
        }

        /**
         * Clears all planted trees from the forest.
         *
         * ## Example:
         * ```kotlin
         * Lumber.uprootAll() // Removes all trees (Oaks) from the forest
         * Lumber.debug("Message will not be logged anymore")
         * ```
         */
        fun uprootAll() = apply {
            mutex.synchronized(trees) { trees.clear() }
        }

        /**
         * Returns a copy of all planted trees (Oaks).
         * This can be useful for iterating or debugging.
         *
         * @return A list of all planted Oaks.
         *
         * ## Example:
         * ```kotlin
         * val forest = Lumber.forest() // Get all planted Oaks
         * forest.forEach { oak -> oak.debug("Inspecting Oak: ${oak.javaClass.simpleName}") }
         * ```
         */
        fun forest(): List<Oak> = trees.toList()
    }
}
