@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.jacoco
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.version
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitTestPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("jacoco")

        // Kover configuration
        with(target.jacoco) { toolVersion = target.libs.version("jacoco") }

        // Regular Test configuration
        kotlin.runCatching { setForApplication(target.androidApplication) }
        kotlin.runCatching { setForLibrary(target.androidLibrary) }
    }

    private fun setForApplication(android: ApplicationExtension) = with(android) {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes.maybeCreate("debug").apply {
            enableAndroidTestCoverage = false
            enableUnitTestCoverage = false
        }
        buildTypes.maybeCreate("release").apply {
            enableAndroidTestCoverage = false
            enableUnitTestCoverage = false
        }

        testOptions {
            unitTests.all { it.enabled = false }
            unitTests.isIncludeAndroidResources = true
            unitTests.isReturnDefaultValues = true
            animationsDisabled = true
        }
    }

    private fun setForLibrary(android: LibraryExtension) = with(android) {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes.maybeCreate("debug").apply {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
        buildTypes.maybeCreate("release").apply {
            enableAndroidTestCoverage = false
            enableUnitTestCoverage = false
        }

        testOptions {
            unitTests.isIncludeAndroidResources = true
            unitTests.isReturnDefaultValues = true
            animationsDisabled = true
        }
    }
}
