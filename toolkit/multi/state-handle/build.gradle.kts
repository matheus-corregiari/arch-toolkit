plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
    id("kotlin-parcelize")
    alias(libs.plugins.jetbrains.serialization)
}

android.namespace = "br.com.arch.toolkit.stateHandle"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets {
        // Common Setup
        commonMain.dependencies {
            implementation(project(":toolkit:multi:lumber"))
            implementation(project(":toolkit:multi:event-observer"))

            implementation(libs.jetbrains.coroutines.core)
            implementation(libs.jetbrains.serialization)
            implementation(libs.di.koin.core)
            implementation(libs.di.koin.composeViewModel)
        }
        commonTest.dependencies { implementation(libs.jetbrains.kotlin.test) }
    }
}
