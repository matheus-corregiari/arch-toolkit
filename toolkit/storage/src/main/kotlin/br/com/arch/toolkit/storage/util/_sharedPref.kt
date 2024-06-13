package br.com.arch.toolkit.storage.util

import android.content.SharedPreferences
import kotlin.reflect.KClass

/**
 * This Kotlin extension function provides a more concise way to edit a SharedPreferences object.
 *
 * ## Purpose:
 * The code defines an extension function named edit on the SharedPreferences class. This function simplifies the process of modifying values within shared preferences.
 *
 * ## How it Works:
 * - edit(): It calls the edit() method on the SharedPreferences instance to obtain a SharedPreferences.Editor object, which is used to make changes to the preferences.
 * - apply(func): It takes a lambda function (func) as an argument. This lambda is executed within the context of the SharedPreferences.Editor, allowing you to perform modifications using the editor's methods (like putInt, putString, etc.).
 * - apply(): Finally, it calls apply() on the editor to asynchronously save the changes to the shared preferences.
 *
 * ## Usage Example:
 * > In this example, the edit extension function makes the code cleaner by eliminating the need to explicitly call apply() after making changes.
 * ```kotlin
 * val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
 *
 * sharedPreferences.edit {
 *     putInt("user_age", 30)
 *     putString("user_name", "Alice")
 * }
 * ```
 *
 * ## Benefits:
 * - Improved Readability: The code becomes more concise and easier to read.
 * - Reduced Boilerplate: It eliminates the need for repetitive apply() calls.
 * - Type Safety: The lambda function ensures that you only use methods available on the SharedPreferences.Editor.
 */
fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    edit().apply(func).apply()
}

/**
 * This code defines an extension function named set for the SharedPreferences class. It provides a convenient way to store different data types in SharedPreferences using the operator overloading feature of Kotlin.
 *
 * ## Type Handling
 * > The code handles several data types:
 * - String? and String: Stores string values using putString.
 * - Set<*>? and Set<*>: Stores sets of strings using putStringSet. It maps the elements of the set to strings and handles nullable sets.
 * - Int, Boolean, Float, Long: Stores primitive data types using their respective put methods.
 *
 * ## Unsupported Types
 * > If the value is of a type not handled by the when expression, it throws an UnsupportedOperationException with a message indicating that the type is not yet implemented.
 *
 * ## Example Usage:
 * ```kotlin
 * val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
 * sharedPrefs.set("username", "JohnDoe") // Stores a string
 * sharedPrefs.set("age", 30) // Stores an integer
 * ```
 * > This extension function simplifies the process of storing various data types in SharedPreferences by providing a concise and type-safe way to do so.
 */
@Throws(UnsupportedOperationException::class)
operator fun <T : Any?> SharedPreferences.set(key: String, value: T) =
    when (value) {
        null -> edit { remove(key) }

        // Primitive data types
        is String? -> edit { putString(key, value) }
        is String -> edit { putString(key, value) }
        is Boolean? -> edit { putBoolean(key, value) }
        is Boolean -> edit { putBoolean(key, value) }
        is Int? -> edit { putInt(key, value) }
        is Int -> edit { putInt(key, value) }
        is Float? -> edit { putFloat(key, value) }
        is Float -> edit { putFloat(key, value) }
        is Double? -> edit { putString(key, value.toString()) }
        is Double -> edit { putString(key, value.toString()) }
        is Long? -> edit { putLong(key, value) }
        is Long -> edit { putLong(key, value) }

        else -> throw UnsupportedOperationException("Not yet implemented: $value")
    }

@Suppress("UNCHECKED_CAST")
operator fun <T : Any> SharedPreferences.get(key: String) = when {
    contains(key) -> all[key] as? T
    else -> null
}

internal fun <T : Any> KClass<T>.isPrimitiveForSharedPref() = when (this) {
    // Primitive data types
    String::class,
    Int::class,
    Boolean::class,
    Float::class,
    Double::class,
    Long::class -> true

    else -> false
}
