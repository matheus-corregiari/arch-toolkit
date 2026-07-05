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
                implementation(project(":sample:shared:structure:core"))

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
