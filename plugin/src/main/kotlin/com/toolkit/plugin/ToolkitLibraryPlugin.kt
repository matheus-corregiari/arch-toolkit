package com.toolkit.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-library")
        target.plugins.apply("toolkit-base")

        with(target.androidLibrary) {

            defaultConfig {
                consumerProguardFiles("consumer-proguard-rules.pro")
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            buildTypes.maybeCreate("release").minifyEnabled(false)
        }
    }

}
