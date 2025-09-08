package com.toolkit.plugin.android

import com.android.build.api.dsl.ApplicationExtension
import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.version
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog

internal fun Project.setupAndroidApplicationModule() = with(androidApplication) {
    // Exclusive Application Configurations
    defaultConfig {
        proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        versionCode = libs.version("build-version-code").toInt()
        versionName = libs.version("build-version-name")

        @Suppress("UnstableApiUsage")
        androidResources.localeFilters.add("en")
    }
    buildTypes.maybeCreate("release").apply { isMinifyEnabled = true }
    buildFeatures.buildConfig = true

    // Setup Android Version support
    setupVersion(libs)

    // Common Setup
    commonSetup()
}

private fun ApplicationExtension.setupVersion(libraries: VersionCatalog) {
    compileSdk = libraries.version("build-sdk-compile").toInt()
    buildToolsVersion = libraries.version("build-tools")

    defaultConfig {
        minSdk = libraries.version("build-sdk-min-sample").toInt()
        targetSdk = libraries.version("build-sdk-target").toInt()
    }
}
