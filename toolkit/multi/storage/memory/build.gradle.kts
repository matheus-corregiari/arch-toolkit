plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

android.namespace = "br.com.arch.toolkit.storage.memory"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        // Common Setup (all targets)
        commonMain.dependencies {
            // Other Arch-Toolkit Dependencies
            implementation(project(":toolkit:multi:lumber"))
            implementation(project(":toolkit:multi:storage:core"))

            // Jetbrains
            implementation(libs.jetbrains.coroutines.core)
        }
    }
}
