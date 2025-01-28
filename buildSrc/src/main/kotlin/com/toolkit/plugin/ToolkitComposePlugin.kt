package com.toolkit.plugin

import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.libs
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
                    target.libs.version("androidx-compose-compiler")
            }
        }
        with(target.dependencies) {
            add("implementation", target.libs.findBundle("androidx-compose-release").get())
            add("debugImplementation", target.libs.findBundle("androidx-compose-debug").get())
        }
    }
}
