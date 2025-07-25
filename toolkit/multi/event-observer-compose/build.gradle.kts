@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android.namespace = "br.com.arch.toolkit.eventObserver.compose"
android.androidResources { enable = false }
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies {
        // Other Arch-Toolkit Dependencies
        implementation(project(":toolkit:multi:event-observer"))

        // Libraries
        implementation(libs.jetbrains.coroutines.core)
        implementation(libs.androidx.compose.lifecycle)
        implementation(compose.runtime)
        implementation(compose.animation)
    }
    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.lifecycle.livedata)
    }

    // Test Libraries
    sourceSets.commonTest.dependencies {
        implementation(libs.jetbrains.test.coroutines)
        implementation(libs.junit.test)
        implementation(libs.mockk.test.agent)
        implementation(compose.material3)
        implementation(compose.uiTest)
    }
    sourceSets.jvmTest.dependencies {
        implementation(compose.desktop.currentOs)
        implementation(compose.desktop.uiTestJUnit4)
    }
}
