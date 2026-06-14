plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

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
