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

internal fun String.format(vararg args: Any?): String {
    val matches = Regex("%[sd]").findAll(this).toList()
    val match = matches.firstOrNull() ?: return this
    if (matches.size > args.size) {
        error("Wrong number of arguments, expected ${matches.size}, actual ${args.size}")
    }
    val argument = args.firstOrNull()
    val formatted = when (match.value) {
        "%s" -> argument.toString()
        "%d" -> (argument as? Number).toString()
        else -> match.value
    }
    val replaced = replaceRange(
        range = match.range,
        replacement = formatted
    )
    return if (args.size <= 1) {
        replaced
    } else {
        replaced.format(args = args.drop(1).toTypedArray())
    }
}

internal fun String.camelcase(): String {
    val parts = trimStart().trimEnd().split(" ", "_", "-")
    return if (parts.size == 1) {
        parts.first()
    } else {
        parts.joinToString("") { part -> part.lowercase().replaceFirstChar { it.titlecase() } }
    }
}
