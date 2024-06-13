package br.com.arch.toolkit.storage

import kotlin.reflect.KClass

interface ComplexDataParser {

    /**
     * Parses a json string into a data class
     */
    fun <T : Any> fromJson(json: String, classToParse: KClass<T>): T

    /**
     * Parses a data class into a json string
     */
    fun <T : Any> toJson(data: T): String
}
