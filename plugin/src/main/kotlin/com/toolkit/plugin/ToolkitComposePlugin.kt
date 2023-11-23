package com.toolkit.plugin

import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.libraries
import com.toolkit.plugin.util.requireAll
import com.toolkit.plugin.util.version
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitComposePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.requireAll("sample-compose", "toolkit-android-library")

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
