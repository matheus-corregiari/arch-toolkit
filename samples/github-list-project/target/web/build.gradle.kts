@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id("toolkit-web-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

kotlin {

    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport { enabled = true }
                outputFileName = "bacate.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.rootDir.path)
                        add(project.projectDir.path)
                    }
                }
            }
        }
    }

    wasmJs {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport { enabled = true }
                outputFileName = "bacate.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.rootDir.path)
                        add(project.projectDir.path)
                    }
                }
            }
        }
    }

    sourceSets.commonMain.dependencies {
        // Sample Modules
        implementation(project(":samples:github-list-project:shared"))

        // Arch Toolkit Dependencies
        implementation(project(":toolkit:multi:lumber"))
        implementation(project(":toolkit:multi:splinter"))
        implementation(project(":toolkit:multi:event-observer"))
        implementation(project(":toolkit:multi:event-observer-compose"))

        implementation(compose.runtime)
        implementation(compose.material3)
        implementation(compose.ui)
        implementation(compose.foundation)
        implementation(compose.components.resources)
    }
    sourceSets.jsMain.dependencies {
        implementation(compose.html.core)
        implementation(compose.material)
        implementation(compose.components.uiToolingPreview)
        implementation(compose.runtime)
    }
}
