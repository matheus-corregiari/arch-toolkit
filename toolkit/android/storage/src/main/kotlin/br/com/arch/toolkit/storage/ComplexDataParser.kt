package br.com.arch.toolkit.storage

import kotlin.reflect.KClass

interface ComplexDataParser {

    /** Parses a JSON payload into an instance of [classToParse]. */
    fun <T : Any> fromJson(json: String, classToParse: KClass<T>): T

    /** Serializes [data] into JSON. */
    fun <T : Any> toJson(data: T): String
}
