package com.toolkit.plugin.android

import com.android.build.api.dsl.LibraryExtension
import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.version
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog

internal fun Project.setupAndroidLibraryModule() = with(androidLibrary) {
    // Common Setup
    commonSetup()

    // Setup Android Version support
    setupVersion(libs)

    // Exclusive Library Configurations
    defaultConfig {
        if (project.file("consumer-proguard-rules.pro").exists()) {
            consumerProguardFiles("consumer-proguard-rules.pro")
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

private fun LibraryExtension.setupVersion(libraries: VersionCatalog) {
    val sdk = libraries.version("build-sdk-compile").split('.')
    compileSdk {
        version = release(sdk[0].toInt()) {
            minorApiLevel = sdk.getOrElse(1) { "0" }.toInt()
        }
    }
    buildToolsVersion = libraries.version("build-tools")

    defaultConfig {
        minSdk = libraries.version("build-sdk-min-toolkit").toInt()
    }
}
