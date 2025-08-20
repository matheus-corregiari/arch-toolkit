plugins {
    id("toolkit-multiplatform-sample")
    id("kotlin-parcelize")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
    alias(libs.plugins.jetbrains.serialization)
}

android.namespace = "br.com.arch.toolkit.sample.github.shared.structure.repository"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":samples:github-list-project:shared:structure:core"))

                // Arch Toolkit Dependencies
                api(project(":toolkit:multi:event-observer"))
                implementation(project(":toolkit:multi:splinter"))

                implementation(libs.jetbrains.serialization)

                // Http
                implementation(libs.ktorfit)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.logging)
            }
        }

        androidMain.dependencies {
            // Arch Toolkit Dependencies
            implementation(project(":toolkit:android:util"))
            implementation(project(":toolkit:multi:storage:datastore"))
        }

        jvmMain.dependencies {
            // Arch Toolkit Dependencies
            implementation(project(":toolkit:multi:storage:datastore"))
        }

        wasmJsMain {
            dependencies {
                // Arch Toolkit Dependencies
                implementation(project(":toolkit:multi:storage:memory"))
            }
        }
        jsMain {
            dependencies {
                // Arch Toolkit Dependencies
                implementation(project(":toolkit:multi:storage:memory"))
            }
        }
    }
}
