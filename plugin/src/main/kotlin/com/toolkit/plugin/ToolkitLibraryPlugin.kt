package com.toolkit.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ToolkitLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins(
            "android-library",
            "jetbrains-kotlin"
        )

        target.androidLibrary.defaultConfig {
            consumerProguardFiles("consumer-proguard-rules.pro")
        }
    }

}
