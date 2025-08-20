plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android.namespace = "br.com.arch.toolkit.sample.github.shared"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":samples:github-list-project:shared:structure:core"))
                implementation(project(":samples:github-list-project:shared:structure:designSystem"))
                implementation(project(":samples:github-list-project:shared:structure:repository"))

                // Arch Toolkit Dependencies
                implementation(project(":toolkit:multi:event-observer-compose"))

                // Compose
                implementation(compose.components.resources)
            }
        }

        androidMain {}
        jvmMain {}
        wasmJsMain {}
        jsMain {}
    }
}
