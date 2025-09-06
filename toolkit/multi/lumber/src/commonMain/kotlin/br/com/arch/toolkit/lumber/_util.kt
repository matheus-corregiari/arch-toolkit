package br.com.arch.toolkit.lumber

import kotlinx.coroutines.sync.Mutex

internal val fqcnIgnore = setOfNotNull(
    Lumber::class,
    Lumber.Level::class,
    Lumber.OakWood::class,
    Lumber.Oak::class,
    DebugTree::class,
)

internal fun <T> Mutex.synchronized(key: Any, block: () -> T) = try {
    runCatching { tryLock(owner = key) }
    block()
} finally {
    runCatching { if (isLocked) unlock(owner = key) }
}

@Suppress("ReturnCount")
internal fun String.format(vararg args: Any?): String {
    // Ignore in case of no arguments, there is nothing to do
    if (args.isEmpty()) return this

    // Find all matches and verify if the number of matches is enough to format properly
    val matches = Regex("%[sd]").findAll(this).toList()
    val match = matches.firstOrNull() ?: return this
    if (matches.size > args.size) {
        error("Wrong number of arguments, expected ${matches.size}, actual ${args.size}")
    }

    // Get the argument and format it
    val argument = args.firstOrNull()
    val formatted = when (match.value) {
        "%s" -> argument.toString()
        "%d" -> (argument as? Number).toString()
        else -> match.value
    }

    // Replace the match and format the string
    val replaced = replaceRange(
        range = match.range,
        replacement = formatted
    )

    // Recursively format the rest of the string
    return replaced.format(args = args.drop(1).toTypedArray())
}

internal fun String.camelcase(): String {
    val parts = trimStart().trimEnd().split(" ", "_", "-")
    return if (parts.size == 1) {
        parts.first()
    } else {
        parts.joinToString("") { part -> part.lowercase().replaceFirstChar { it.titlecase() } }
    }
}
