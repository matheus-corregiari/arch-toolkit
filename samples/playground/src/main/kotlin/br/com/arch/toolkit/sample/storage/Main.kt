@file:Suppress("LongMethod", "MagicNumber")
@file:OptIn(Experimental::class)

package br.com.arch.toolkit.sample.storage

import br.com.arch.toolkit.annotation.Experimental
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONArray

fun main() {
    assert(set("key", 0) == "key" to 0)

    val list = set("key", listOf(0))
    assert(list.first == "json|key")
    assert(list.second == "[0]")
}

inline fun <reified T> set(key: String, value: T) = when (value) {
    is String? -> key to value
    is String -> key to value
    is Set<*>? -> key to value?.mapNotNull { it?.toString() }?.toSet()
    is Set<*> -> key to value.mapNotNull { it?.toString() }.toSet()
    is Int -> key to value
    is Boolean -> key to value
    is Float -> key to value
    is Long -> key to value

    is List<*> -> {
        "json|$key" to JSONArray(value)
    }
    is Map<*, *> -> "json|$key" to Json.encodeToString(value)

    else -> throw UnsupportedOperationException("Not yet implemented: $value")
}
