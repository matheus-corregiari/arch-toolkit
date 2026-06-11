plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":sample:shared:structure:core"))
                implementation(project(":sample:shared:structure:designSystem"))
                implementation(project(":sample:shared:structure:repository"))

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
