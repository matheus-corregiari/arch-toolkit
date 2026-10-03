package com.toolkit.plugin

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/** Shares generated-class exclusions between the library and root coverage reports. */
internal class ToolkitCoveragePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("org.jetbrains.kotlinx.kover")
        target.extensions.configure(KoverProjectExtension::class.java) { extension ->
            extension.reports { reports ->
                reports.filters { filters ->
                    filters.excludes { excludes ->
                        // No numerical floor is configured in this repository.
                        excludes.classes("*.BuildConfig", "*.R", "*.R$*")
                    }
                }
            }
        }
    }
}
