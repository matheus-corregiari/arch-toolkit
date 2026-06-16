@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin

import com.toolkit.plugin.util.allDefinedDependencies
import com.toolkit.plugin.util.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies dependency resolution guardrails.
 *
 * The plugin prefers project modules, fails on dependency conflicts, and forces catalog-defined
 * versions to keep Toolkit builds reproducible.
 */
internal class ToolkitOptimizePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val libraries = target.libs
        val allDefinedLibraries = libraries.allDefinedDependencies
        target.configurations.configureEach { config ->
            config.resolutionStrategy { strategy ->
                strategy.failOnVersionConflict()
                strategy.preferProjectModules()

                strategy.setForcedModules(allDefinedLibraries)
            }
        }
    }
}
