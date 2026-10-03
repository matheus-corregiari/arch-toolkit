plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android.namespace = "br.com.arch.toolkit.sample.design.widget"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
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

                api(project(":sample:shared:structure:design:core"))

                // Other Tools
                implementation(libs.androidx.compose.material3.window)
                implementation(libs.androidx.compose.material3.adaptive)

                // Image Loader
                api(libs.coil.core)
                api(libs.coil.network)

                // Blur
                api(libs.haze.core)
                api(libs.haze.blur)
            }
        }

        androidMain {}
        jvmMain {}
    }
}
