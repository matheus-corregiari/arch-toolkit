package br.com.arch.toolkit.lumber

/** Logging for lazy people. */
class Lumber private constructor() {
    init {
        throw AssertionError()
    }

    enum class Level { Verbose, Debug, Info, Warn, Error, Assert }

    /** A facade for handling logging calls. Install instances via [`Lumber.plant()`][.plant]. */
    abstract class Oak {
        internal val explicitTag = ThreadLocal<String>()
        internal val explicitQuiet = ThreadLocal<Boolean>()

        private val fqcnIgnore = listOfNotNull(
            Lumber::class.simpleName,
            OakWood::class.simpleName,
            Oak::class.simpleName,
            DebugTree::class.simpleName,
        )

        internal open val tag: String?
            get() {
                val tag = explicitTag.get()
                if (tag != null) explicitTag.remove()
                return tag ?: Throwable().stackTrace
                    .first { it.className !in fqcnIgnore }
                    .let(::createStackElementTag)
            }
        internal open val quiet: Boolean
            get() {
                val quiet = explicitQuiet.get()
                if (quiet != null) {
                    explicitQuiet.remove()
                }
                return quiet == true
            }

        //region Verbose
        /** Log a verbose message with optional format args. */
        open fun verbose(message: String?, vararg args: Any?) =
            verbose(error = null, message = message, args = args)

        /** Log a verbose exception. */
        open fun verbose(error: Throwable?) = verbose(error = error, message = null)

        /** Log a verbose exception and a message with optional format args. */
        open fun verbose(error: Throwable?, message: String?, vararg args: Any?) =
            log(level = Level.Verbose, error = error, message = message, args = args)
        //endregion

        //region Debug
        /** Log a debug message with optional format args. */
        open fun debug(message: String?, vararg args: Any?) =
            debug(error = null, message = message, args = args)

        /** Log a debug exception and a message with optional format args. */
        open fun debug(error: Throwable?) = debug(error = error, message = null)

        /** Log a debug exception. */
        open fun debug(error: Throwable?, message: String?, vararg args: Any?) =
            log(level = Level.Debug, error = error, message = message, args = args)
        //endregion

        //region Info
        /** Log an info message with optional format args. */
        open fun info(message: String?, vararg args: Any?) =
            info(error = null, message = message, args = args)

        /** Log an info exception and a message with optional format args. */
        open fun info(error: Throwable?) = info(error = error, message = null)

        /** Log an info exception. */
        open fun info(error: Throwable?, message: String?, vararg args: Any?) =
            log(level = Level.Info, error = error, message = message, args = args)
        //endregion

        //region Warn
        /** Log a warning message with optional format args. */
        open fun warn(message: String?, vararg args: Any?) =
            warn(error = null, message = message, args = args)

        /** Log a warning exception and a message with optional format args. */
        open fun warn(error: Throwable?) = warn(error = error, message = null)

        /** Log a warning exception. */
        open fun warn(error: Throwable?, message: String?, vararg args: Any?) =
            log(level = Level.Warn, error = error, message = message, args = args)
        //endregion

        //region Error
        /** Log an error message with optional format args. */
        open fun error(message: String?, vararg args: Any?) =
            error(error = null, message = message, args = args)

        /** Log an error exception and a message with optional format args. */
        open fun error(error: Throwable?) = error(error = error, message = null)

        /** Log an error exception. */
        open fun error(error: Throwable?, message: String?, vararg args: Any?) =
            log(level = Level.Error, error = error, message = message, args = args)
        //endregion

        //region Assert
        /** Log an assert message with optional format args. */
        open fun wtf(message: String?, vararg args: Any?) =
            wtf(error = null, message = message, args = args)

        /** Log an assert exception and a message with optional format args. */
        open fun wtf(error: Throwable?) = wtf(error = error, message = null)

        /** Log an assert exception. */
        open fun wtf(error: Throwable?, message: String?, vararg args: Any?) =
            log(level = Level.Assert, error = error, message = message, args = args)
        //endregion

        //region Raw Log
        /** Log at `priority` a message with optional format args. */
        open fun log(level: Level, message: String?, vararg args: Any?) =
            log(level = level, error = null, message = message, args = args)

        /** Log at `priority` an exception and a message with optional format args. */
        open fun log(level: Level, error: Throwable?) =
            log(level = level, error = error, message = null)

        /** Log at `priority` an exception. */
        open fun log(level: Level, error: Throwable?, message: String?, vararg args: Any?) =
            prepareLog(level = level, error = error, message = message, args = args)
        //endregion

        /** Return whether a message at `level` or `tag` should be logged. */
        protected abstract fun isLoggable(tag: String?, level: Level): Boolean

        /**
         * Write a log message to its destination. Called for all level-specific methods by default.
         *
         * @param level Log level. See [Level] for constants.
         * @param tag Explicit or inferred tag. May be `null`.
         * @param message Formatted log message.
         * @param error Accompanying exceptions. May be `null`.
         */
        protected abstract fun log(level: Level, tag: String?, message: String, error: Throwable?)

        private fun prepareLog(
            level: Level, error: Throwable?, message: String?, vararg args: Any?
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
                if (error != null) formattedMessage += "\n" + error.stackTraceToString()
            }
            if (formattedMessage.length <= MAX_LOG_LENGTH) {
                log(level, tag, formattedMessage, error)
            } else {
                formattedMessage.chunked(MAX_LOG_LENGTH).forEachIndexed { index, part ->
                    log(level, tag?.let { "$it #$index" }, part, error)
                }
            }
        }

        /**
         * Extract the tag which should be used for the message from the `element`. By default
         * this will use the class name without any anonymous class suffixes (e.g., `Foo$1`
         * becomes `Foo`).
         *
         * Note: This will not be called if a [manual tag][.tag] was specified.
         */
        private fun createStackElementTag(element: StackTraceElement): String {
            var tag = element.className.substringAfterLast('.')
            val matcher = ANONYMOUS_CLASS_PATTERN.matcher(tag)
            if (matcher.find()) tag = matcher.replaceAll("")
            tag = if (tag.length <= MAX_TAG_LENGTH) tag else tag.substring(0, MAX_TAG_LENGTH)
            return "$tag:${element.methodName}"
        }
    }

    /** A [Oak] for debug builds. Automatically infers the tag from the calling class. */

    companion object OakWood : Oak() {

        private val trees = ArrayList<Oak>()

        @Volatile
        private var treeArray = emptyArray<Oak>()

        val treeCount get() = treeArray.size

        override fun log(level: Level, error: Throwable?, message: String?, vararg args: Any?) {
            treeArray.forEach {
                it.log(level = level, error = error, message = message, args = args)
            }
        }

        override fun log(level: Level, tag: String?, message: String, error: Throwable?) =
            error("This Oak doesn't implement the log method itself")

        override fun isLoggable(tag: String?, level: Level) = true

        /**
         * A view into Lumber's planted trees as a tree itself. This can be used for injecting a logger
         * instance rather than using static methods or to facilitate testing.
         */
        fun asTree(): Oak = this

        /** Set a one-time tag for use on the next logging call. */
        fun tag(tag: String): OakWood {
            treeArray.forEach { it.explicitTag.set(tag) }
            return this
        }

        /** Set a one-time quiet flag for use on the next logging call. */
        fun quiet(quiet: Boolean): OakWood {
            treeArray.forEach { it.explicitQuiet.set(quiet) }
            return this
        }

        /** Add a new logging tree. */
        fun plant(tree: Oak): Oak {
            require(tree !== this) { "Cannot plant Lumber into itself." }
            synchronized(trees) {
                trees.add(tree)
                treeArray = trees.toTypedArray()
            }
            return this
        }

        /** Adds new logging trees. */
        fun plant(vararg trees: Oak?): Oak {
            for (tree in trees) {
                requireNotNull(tree) { "trees contained null" }
                require(tree !== this) { "Cannot plant Lumber into itself." }
            }
            synchronized(OakWood.trees) {
                OakWood.trees.addAll(trees.filterNotNull())
                treeArray = OakWood.trees.toTypedArray()
            }
            return this
        }

        /** Remove a planted tree. */
        fun uproot(tree: Oak) = synchronized(trees) {
            require(trees.remove(tree)) { "Cannot uproot tree which is not planted: $tree" }
            treeArray = trees.toTypedArray()
        }

        /** Remove all planted trees. */
        fun uprootAll() = synchronized(trees) {
            trees.clear()
            treeArray = emptyArray()
        }


        /** Return a copy of all planted [trees][Oak]. */
        fun forest(): List<Oak> = synchronized(trees) { trees.toList() }
    }
}
