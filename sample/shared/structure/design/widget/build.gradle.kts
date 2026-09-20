plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android.namespace = "br.com.arch.toolkit.sample.github.shared.structure.designSystem"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.runtimeSaveable)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.material3AdaptiveNavigationSuite)
                implementation(compose.foundation)
                implementation(compose.animation)

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
                api(libs.haze.materials)
            }
        }

        androidMain {}
        jvmMain {}
        wasmJsMain { }
        jsMain { }
    }
}
