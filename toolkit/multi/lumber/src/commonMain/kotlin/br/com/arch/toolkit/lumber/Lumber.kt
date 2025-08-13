@file:Suppress("unused", "TooManyFunctions")

package br.com.arch.toolkit.lumber

import br.com.arch.toolkit.lumber.Lumber.OakWood.quiet
import br.com.arch.toolkit.lumber.Lumber.OakWood.tag
import kotlinx.coroutines.sync.Mutex

/**
 * # Lumber - A Lightweight Logging Library for Android and Kotlin Multiplatform (KMP)
 *
 * Lumber is a flexible and straightforward logging library designed for Android and Kotlin Multiplatform (KMP),
 * inspired by the excellent [Timber](https://github.com/JakeWharton/timber) library by Jake Wharton.
 * This library allows you to log messages with various levels of priority, such as `Verbose`,
 * `Debug`, `Info`, `Warn`, `Error`, and `Assert`.
 *
 * ## Example Usage:
 * ```
 * // Plant a custom Oak (logging tree)
 * Lumber.plant(MyCustomOak())
 *
 * // Log messages with different levels
 * Lumber.debug("Debug message")
 * Lumber.error(Throwable("Exception"), "An error occurred!")
 * ```
 *
 * ## Honorable Mention:
 * Special thanks to Jake Wharton for the Timber library, which served as a great inspiration for Lumber.
 *
 * @author ***
 * @since 1.0
 */
class Lumber private constructor() {
    init {
        throw AssertionError("No instances allowed.")
    }

    /** Logging levels supported by Lumber. */
    enum class Level { Verbose, Debug, Info, Warn, Error, Assert }

    /**
     * # Oak - Abstract Logging Tree
     *
     * Represents a logging tree where logs are processed and dispatched. To use Lumber,
     * extend this class and implement the desired logging behavior, such as writing to
     * the console, files, or external services.
     *
     * ## Example:
     * ```
     * class ConsoleOak : Lumber.Oak() {
     *     override fun log(level: Level, tag: String?, message: String, error: Throwable?) {
     *         println("$level: [$tag] $message")
     *     }
     * }
     * ```
     */
    abstract class Oak {

        private val explicitTag = ThreadSafe<String?>()
        private val explicitQuiet = ThreadSafe<Boolean?>()

        private val fqcnIgnore = setOfNotNull(
            Lumber::class.qualifiedName,
            Level::class.qualifiedName,
            OakWood::class.qualifiedName,
            Oak::class.qualifiedName,
            DebugTree::class.qualifiedName,
        )

        /**
         * The tag to be used for the log message.
         * By default, it is derived from the stack trace element, but it can be overridden
         * with a custom tag using the `tag()` method.
         *
         * @return The tag to be used for the log message.
         */
        protected open val tag: String?
            get() = explicitTag
                .get()
                .takeIf { it.isNullOrBlank().not() }
                ?.also { explicitTag.remove() }
                ?: defaultTag(fqcnIgnore)

        /**
         * A flag to determine if the log message should be suppressed.
         * This flag can be set temporarily using the `quiet()` method.
         *
         * @return `true` if the log is in quiet mode, otherwise `false`.
         */
        protected open val quiet: Boolean
            get() = explicitQuiet.get()?.also { explicitQuiet.remove() } == true

        /**
         * Sets a one-time tag to be used for the next logging call on this specific {@link Oak}.
         * This tag helps identify the source of the log, making it easier to trace.
         * The tag is temporary and only affects the immediate next log message.
         * <p>
         * The tag is stored using a {@link ThreadLocal} to ensure it is only applied for the current thread
         * and is cleared automatically after the log call.
         * </p>
         *
         * @param tag The tag to attach to the next log message.
         * @return The {@link Oak} instance for method chaining.
         *
         * ## Example:
         * ```kotlin
         * Lumber.tag("MyActivity").debug("Debug message")
         * // Expected output: Debug: [MyActivity] Debug message
         * ```
         */
        open fun tag(tag: String): Oak {
            explicitTag.set(tag)
            return this
        }

        /**
         * Sets a one-time quiet flag to be used for the next logging call on this specific {@link Oak}.
         * When enabled, some loggers might skip logging the message based on their implementation.
         * The quiet flag is temporary and only affects the immediate next log message.
         * <p>
         * The flag is stored using a {@link ThreadLocal} to ensure it is only applied for the current thread
         * and is cleared automatically after the log call.
         * </p>
         *
         * @param quiet True to enable quiet mode for the next log call; false otherwise.
         * @return The {@link Oak} instance for method chaining.
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

        //region Verbose

        /** Log a verbose message with optional format args. */
        open fun verbose(message: String, vararg args: Any?) =
            log(level = Level.Verbose, message = message, args = args)

        /** Log a verbose exception. */
        open fun verbose(error: Throwable) =
            log(level = Level.Verbose, error = error)

        /** Log a verbose exception and a message with optional format args. */
        open fun verbose(error: Throwable, message: String, vararg args: Any?) =
            log(level = Level.Verbose, error = error, message = message, args = args)
        //endregion

        //region Debug

        /** Log a debug message with optional format args. */
        open fun debug(message: String, vararg args: Any?) =
            log(level = Level.Debug, message = message, args = args)

        /** Log a debug exception and a message with optional format args. */
        open fun debug(error: Throwable) =
            log(level = Level.Debug, error = error)

        /** Log a debug exception. */
        open fun debug(error: Throwable, message: String, vararg args: Any?) =
            log(level = Level.Debug, error = error, message = message, args = args)
        //endregion

        //region Info

        /** Log an info message with optional format args. */
        open fun info(message: String, vararg args: Any?) =
            log(level = Level.Info, message = message, args = args)

        /** Log an info exception and a message with optional format args. */
        open fun info(error: Throwable) =
            log(level = Level.Info, error = null, message = null, args = emptyArray())

        /** Log an info exception. */
        open fun info(error: Throwable, message: String, vararg args: Any?) =
            log(level = Level.Info, error = error, message = message, args = args)
        //endregion

        //region Warn

        /** Log a warning message with optional format args. */
        open fun warn(message: String, vararg args: Any?) =
            log(level = Level.Warn, message = message, args = args)

        /** Log a warning exception and a message with optional format args. */
        open fun warn(error: Throwable) =
            log(level = Level.Warn, error = error)

        /** Log a warning exception. */
        open fun warn(error: Throwable, message: String, vararg args: Any?) =
            log(level = Level.Warn, error = error, message = message, args = args)
        //endregion

        //region Error

        /** Log an error message with optional format args. */
        open fun error(message: String, vararg args: Any?) =
            log(level = Level.Error, message = message, args = args)

        /** Log an error exception and a message with optional format args. */
        open fun error(error: Throwable) =
            log(level = Level.Error, error = error)

        /** Log an error exception. */
        open fun error(error: Throwable, message: String, vararg args: Any?) =
            log(level = Level.Error, error = error, message = message, args = args)
        //endregion

        //region Assert

        /** Log an assert message with optional format args. */
        open fun wtf(message: String, vararg args: Any?) =
            log(level = Level.Assert, message = message, args = args)

        /** Log an assert exception and a message with optional format args. */
        open fun wtf(error: Throwable) =
            log(level = Level.Assert, error = error)

        /** Log an assert exception. */
        open fun wtf(error: Throwable, message: String, vararg args: Any?) =
            log(level = Level.Assert, error = error, message = message, args = args)
        //endregion

        //region Raw Log

        /** Log at `priority` a message with optional format args. */
        open fun log(level: Level, message: String, vararg args: Any?) =
            log(level = level, error = null, message = message, args = args)

        /** Log at `priority` an exception and a message with optional format args. */
        open fun log(level: Level, error: Throwable) =
            log(level = level, error = error, message = null, args = emptyArray())

        /** Log at `priority` an exception. */
        open fun log(level: Level, error: Throwable?, message: String?, vararg args: Any?) =
            prepareLog(level = level, error = error, message = message, args = args)
        //endregion

        /**
         * Determines if a message at a given level should be logged.
         */
        protected abstract fun isLoggable(tag: String?, level: Level): Boolean

        /**
         * Processes and logs a message.
         */
        protected abstract fun log(level: Level, tag: String?, message: String, error: Throwable?)

        private fun prepareLog(
            level: Level,
            error: Throwable?,
            message: String?,
            vararg args: Any?,
        ) {
            // Consume tag even when message is not loggable so that next message is correctly tagged.
            val tag = tag
            if (!isLoggable(tag, level) || quiet) return

            var formattedMessage = message
            if (formattedMessage.isNullOrEmpty()) {
                // Swallow message if it's null and there's no throwable.
                if (error == null) return
                formattedMessage = error.stackTraceToString()
            } else {
                if (args.isNotEmpty()) formattedMessage = formattedMessage.format(*args)
                if (error != null) formattedMessage += "\n\n" + error.stackTraceToString()
            }
            if (formattedMessage.length <= MAX_LOG_LENGTH) {
                log(level = level, tag = tag, message = formattedMessage, error = error)
            } else {
                formattedMessage.chunked(MAX_LOG_LENGTH)
                    .forEachIndexed { index, part ->
                        log(
                            level = level,
                            tag = tag?.let { "$it #$index" },
                            message = part,
                            error = error,
                        )
                    }
            }
        }
    }

    /**
     * Companion object acting as an aggregator for multiple "Oaks".
     *
     * This object allows for the management of multiple logging strategies in parallel.
     * When a log method is called, it dispatches the message to all planted Oaks sequentially.
     * This facilitates the coexistence of multiple logging mechanisms, such as:
     * - Console logging
     * - File logging
     * - Remote logging
     *
     * It also ensures thread safety when propagating logging settings (like tags and quiet mode)
     * to all individual Oak trees, by leveraging synchronized collections and thread-local storage.
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
            error(message = "This is a empty impl from Oak that distributes to other Oaks")

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
            trees.forEach { require(it !== this) { "Cannot plant Lumber itself." } }
            mutex.synchronized(trees) { this.trees.addAll(listOf(tree, *trees)) }
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
