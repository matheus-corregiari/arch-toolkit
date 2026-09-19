plugins {
    id("toolkit-multiplatform-sample")
    id("kotlin-parcelize")
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

                implementation(project(":sample:shared:data:source:remote"))

                // Http
            }
        }

        javaMain {
            dependencies {
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
