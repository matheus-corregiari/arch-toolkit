@file:Suppress("ReturnCount", "Filename")

package br.com.arch.toolkit.storage.delegate

import br.com.arch.toolkit.storage.Storage
import kotlin.reflect.KClass

inline fun <reified T : Any> keyValueStorage(name: String) = keyValueStorage(
    classToParse = T::class,
    name = { name },
)

inline fun <reified T : Any> keyValueStorage(noinline name: () -> String) = keyValueStorage(
    classToParse = T::class,
    name = name,
)

fun <T : Any> keyValueStorage(classToParse: KClass<out T>, name: String) = keyValueStorage(
    classToParse = classToParse,
    name = { name },
)

fun <T : Any> keyValueStorage(classToParse: KClass<out T>, name: () -> String) =
    OptionalStorageDelegate(
        name = name,
        storage = { Storage.Settings.keyValue },
        threshold = Storage.Settings.threshold,
        classToParse = classToParse
    )
