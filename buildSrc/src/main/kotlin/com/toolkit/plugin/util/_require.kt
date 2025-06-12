package com.toolkit.plugin.util

import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlin.reflect.KClass

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
    if (names.none { pluginName -> plugins.hasPlugin(pluginName) }) {
        error("To use $currentPluginName plugin you must implement one of ${names.toList()} plugin")
    }
}

internal fun Project.requireAny(currentPluginName: String, vararg names: KClass<out Plugin<out Any>>) {
    if (names.none { pluginName -> plugins.hasPlugin(pluginName.java) }) {
        error("To use $currentPluginName plugin you must implement one of ${names.toList()} plugin")
    }
}
