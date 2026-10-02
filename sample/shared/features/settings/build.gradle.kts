plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.serialization)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.easy.navigation)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android.namespace = "br.com.arch.toolkit.sample.shared.feature.settings"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.di.koin.composeViewModel)
                implementation(libs.di.koin.compose)
                implementation(libs.di.koin.core)
                implementation(libs.jetbrains.datetime)
                implementation(libs.arch.storage.core)

                implementation(libs.jetbrains.compose.ui)
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.runtime.saveable)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.material.icons)
                implementation(libs.jetbrains.compose.material3.navigation.suite)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.animation)

                implementation(libs.easy.navigation)
                implementation("androidx.navigation3:navigation3-runtime:1.2.0-alpha04")
                implementation(project(":sample:shared:structure:core"))
                implementation(project(":sample:shared:structure:design:widget"))
                implementation(project(":sample:shared:data:repository"))

                // Arch Toolkit Dependencies

                // Compose
                implementation(libs.jetbrains.compose.resources)
            }
        }

        androidMain {}
        jvmMain {}
    }
}
