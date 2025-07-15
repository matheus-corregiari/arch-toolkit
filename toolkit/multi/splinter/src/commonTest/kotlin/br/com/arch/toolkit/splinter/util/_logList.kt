@file:Suppress("Filename")

package br.com.arch.toolkit.splinter.util

internal val logListAllDefault = listOf<Any>(
    "Info - test - [Execute] Creating a new job! - null",
    "Info - test - [Job] Job started! - null",
    "Info - test - [Job] Flow started! - null",
    "Info - test - \t[OneShot] Cache - Not set =( - null",
    "Info - test - \t[OneShot] Emit - Loading - null",
    "Info - test - \t\t[Context] - Emit - Snapshot! - aaa - null",
    ".*Error - test #0 - \t\t\\[Context] - Error\n\njava\\.lang\\.IllegalStateException.*"
        .toRegex(RegexOption.DOT_MATCHES_ALL),
    ".*Error - test #1 - .*"
        .toRegex(RegexOption.DOT_MATCHES_ALL),
    "Info - test - \t\t[Context] - Emit - Snapshot! - bbb - null",
    "Info - test - \t\t[Context] - Snapshot info - null",
    "Info - test - \t[OneShot] Executed with success, data: ccc - null",
    "Info - test - \t\\[OneShot] Execution time \\d+ms, need to wait more \\d+ms - null"
        .toRegex(),
    "Info - test - \t[OneShot] Emit - Success Data! - ccc - null",
    "Info - test - [Job] Finished! - null",
)

internal val logListWithoutMinDuration = listOf<Any>(
    "Info - test - [Execute] Creating a new job! - null",
    "Info - test - [Job] Job started! - null",
    "Info - test - [Job] Flow started! - null",
    "Info - test - \t[OneShot] Cache - Not set =( - null",
    "Info - test - \t[OneShot] Emit - Loading - null",
    "Info - test - \t\t[Context] - Emit - Snapshot! - aaa - null",
    ".*Error - test #0 - \t\t\\[Context] - Error\n\njava\\.lang\\.IllegalStateException.*"
        .toRegex(RegexOption.DOT_MATCHES_ALL),
    ".*Error - test #1 - .*"
        .toRegex(RegexOption.DOT_MATCHES_ALL),
    "Info - test - \t\t[Context] - Emit - Snapshot! - bbb - null",
    "Info - test - \t\t[Context] - Snapshot info - null",
    "Info - test - \t[OneShot] Executed with success, data: ccc - null",
    "Info - test - \t\\[OneShot] Execution time \\d+ms - null"
        .toRegex(),
    "Info - test - \t[OneShot] Emit - Success Data! - ccc - null",
    "Info - test - [Job] Finished! - null",
)
