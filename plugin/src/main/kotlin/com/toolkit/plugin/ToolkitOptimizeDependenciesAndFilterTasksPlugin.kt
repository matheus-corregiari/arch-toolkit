@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin

import com.toolkit.plugin.util.allDefinedDependencies
import com.toolkit.plugin.util.applicationComponent
import com.toolkit.plugin.util.libraries
import com.toolkit.plugin.util.libraryComponent
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitOptimizeDependenciesAndFilterTasksPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val libraries = target.libraries
        val allDefinedLibraries = libraries.allDefinedDependencies
        target.configurations.configureEach { config ->
            config.resolutionStrategy { strategy ->
                strategy.failOnVersionConflict()
                strategy.preferProjectModules()

                strategy.setForcedModules(allDefinedLibraries)
            }
        }
        val component = kotlin.runCatching { target.libraryComponent }.getOrNull()
            ?: kotlin.runCatching { target.applicationComponent }.getOrNull()
        component?.finalizeDsl {
            target.tasks.configureEach { task ->
                val isRelease = task.name.contains("release", true)
                val isLint = task.name.contains("lint", true)
                val isTest = task.name.contains("test", true)
                val isKover = task.name.contains("kover", true)
                val mustDisable = isLint || isTest || isKover
                if (isRelease && mustDisable) {
                    task.enabled = false
                    task.group = "z-disabled"
                }
            }
        }
    }
}
