@file:Suppress("Filename")

package br.com.arch.toolkit.util

/**
 * Extension function for a nullable [Pair] that converts it into a non-nullable [Pair], if both elements are non-null.
 * If either element is null, the function returns null.
 *
 * @param T The type of the first element in the pair.
 * @param R The type of the second element in the pair.
 * @return A non-nullable [Pair] if both elements are non-null, or null if either element is null.
 *
 * Example usage:
 * ```
 * val pair: Pair<String?, Int?> = "Hello" to 42
 * val nonNullPair: Pair<String, Int>? = pair.onlyWithValues()
 * nonNullPair?.let { (first, second) ->
 *     println("First: $first, Second: $second") // Output: First: Hello, Second: 42
 * }
 *
 * val nullablePair: Pair<String?, Int?> = null to 42
 * val result = nullablePair.onlyWithValues()
 * println(result) // Output: null
 * ```
 */
fun <T, R> Pair<T?, R?>.onlyWithValues(): Pair<T, R>? =
    runCatching { requireNotNull(first) to requireNotNull(second) }.getOrNull()
