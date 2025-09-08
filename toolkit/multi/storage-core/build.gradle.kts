plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
    alias(libs.plugins.jetbrains.compose.kotlin)
    alias(libs.plugins.jetbrains.compose.compiler)
}

android.namespace = "br.com.arch.toolkit.storage.core"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies {
        // Other Arch-Toolkit Dependencies
        implementation(project(":toolkit:multi:lumber"))

        implementation(compose.runtime)
        implementation(libs.jetbrains.coroutines.core)
        implementation(libs.jetbrains.serialization)
    }
}
