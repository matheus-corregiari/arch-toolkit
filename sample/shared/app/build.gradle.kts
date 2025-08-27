plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android.namespace = "br.com.arch.toolkit.sample.shared.app"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                // Structure
                api(project(":sample:shared:structure:core"))
                api(project(":sample:shared:structure:designSystem"))
                implementation(project(":sample:shared:structure:repository"))

                // Features
                implementation(project(":sample:shared:feature:github-list"))
                implementation(project(":sample:shared:feature:settings"))

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
