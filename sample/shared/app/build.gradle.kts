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
                api(project(":sample:shared:structure:design:widget"))
                implementation(project(":sample:shared:data:repository"))

                implementation(project(":sample:shared:data:source:remote"))
                implementation(project(":sample:shared:structure:http"))
                implementation(libs.ktorfit)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.jetbrains.serialization)

                // Features
                implementation(project(":sample:shared:features:github-sample"))
                implementation(project(":sample:shared:features:settings"))

                // Arch Toolkit Dependencies
                implementation(libs.arch.event.observer.compose)

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
