plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android.namespace = "br.com.arch.toolkit.sample.design.core"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonTest { dependencies { implementation(libs.jetbrains.kotlin.test) } }
        commonMain {
            dependencies {
                implementation(libs.jetbrains.compose.ui)
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.runtime.saveable)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.material.icons)
                implementation(libs.jetbrains.compose.material3.navigation.suite)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.animation)

                implementation(project(":sample:shared:structure:core"))
                implementation(libs.androidx.compose.material3.window)
                implementation(libs.androidx.compose.material3.adaptive)
            }
        }
    }
}
