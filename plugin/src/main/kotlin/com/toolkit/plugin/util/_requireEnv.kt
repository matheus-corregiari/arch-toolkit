package com.toolkit.plugin.util

import org.gradle.api.Project

internal fun Project.missing(vararg name: String) =
    name.map(::containsEnv).any { it.not() }

internal fun Project.containsEnv(name: String): Boolean {
    val env = System.getenv("name") ?: (properties[name] as? String)
    if (env.isNullOrBlank()) {
        println("Missing Variable: $name")
    }
    return env.isNullOrBlank().not()
}
