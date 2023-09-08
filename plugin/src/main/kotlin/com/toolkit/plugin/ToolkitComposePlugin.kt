package com.toolkit.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitComposePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        if (target.plugins.hasPlugin("toolkit-sample").not()) {
            error("To use sample-compose plugin you must implement toolkit-sample plugin")
        }
        with(target.androidApplication) {
            buildFeatures {
                compose = true
            }
            composeOptions {
                kotlinCompilerExtensionVersion =
                    target.libraries.version("androidx-compose-compiler")
            }
        }
        with(target.dependencies) {

            add("implementation", target.libraries.findBundle("androidx-compose-release").get())
            add("debugImplementation", target.libraries.findBundle("androidx-compose-debug").get())
        }
    }
}
