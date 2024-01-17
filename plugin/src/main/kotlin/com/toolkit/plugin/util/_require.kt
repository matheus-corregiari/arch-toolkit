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

internal fun Project.requireAll(currentPluginName: String, vararg names: String) {
    names.forEach { pluginName ->
        if (plugins.hasPlugin(pluginName).not()) {
            error("To use $currentPluginName plugin you must implement $pluginName plugin")
        }
    }
}

internal fun Project.requireAny(currentPluginName: String, vararg names: String) {
    if (names.none { pluginName -> plugins.hasPlugin(pluginName).not() }) {
        error("To use $currentPluginName plugin you must implement one of $names plugin")
    }
}
