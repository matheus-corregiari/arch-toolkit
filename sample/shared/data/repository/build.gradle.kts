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
                implementation(project(":sample:shared:structure:core"))

                // Arch Toolkit Dependencies
                api(libs.arch.event.observer)
                implementation(project(":toolkit:multi:splinter"))

                implementation(libs.jetbrains.serialization)

                // Http
                implementation(libs.ktorfit)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.logging)
            }
        }

        javaMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.arch.android)
                implementation(libs.arch.storage.datastore)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.arch.storage.datastore)
            }
        }

        appleMain {
            dependencies {
                implementation(libs.arch.storage.datastore)
                implementation(libs.ktor.client.darwin)
            }
        }

        wasmJsMain {
            dependencies {
                implementation(libs.arch.storage.memory)
            }
        }
        jsMain {
            dependencies {
                implementation(libs.arch.storage.memory)
            }
        }
    }
}
