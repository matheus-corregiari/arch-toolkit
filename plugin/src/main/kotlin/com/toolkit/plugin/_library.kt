package com.toolkit.plugin

import com.android.build.api.dsl.LibraryExtension
import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.libraries
import com.toolkit.plugin.util.version
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog

internal fun Project.setupAndroidLibraryModule() = with(androidLibrary) {
    // Common Setup
    commonSetup()

    // Setup Android Version support
    setupVersion(libraries)

    // Exclusive Library Configurations
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

private fun LibraryExtension.setupVersion(libraries: VersionCatalog) {
    compileSdk = libraries.version("build-sdk-compile").toInt()
    buildToolsVersion = libraries.version("build-tools")

    defaultConfig {
        minSdk = libraries.version("build-sdk-min").toInt()
    }
}
