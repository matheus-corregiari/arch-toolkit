plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
    alias(libs.plugins.jetbrains.serialization)
}

android.namespace = "br.com.arch.toolkit.sample.shared.feature.githubSample"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":sample:shared:structure:core"))
                implementation(project(":sample:shared:structure:design:widget"))
                implementation(project(":sample:shared:data:repository"))

                // Arch Toolkit Dependencies
                implementation(libs.arch.event.observer.compose)

                // Compose
                implementation(compose.components.resources)
            }
        }

        androidMain {}
        jvmMain {}
        jvmTest.dependencies {
            implementation(libs.jetbrains.kotlin.test)
            implementation(libs.jetbrains.coroutines.test)
            implementation(libs.mockk.test.agent)
        }
        wasmJsMain {}
        jsMain {}
    }
}
