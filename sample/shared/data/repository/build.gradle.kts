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
                implementation(libs.arch.lumber)
                implementation(libs.jetbrains.datetime)
                implementation(libs.arch.storage.core)

                implementation(project(":sample:shared:structure:core"))

                // Arch Toolkit Dependencies

                implementation(libs.jetbrains.serialization)

                implementation(project(":sample:shared:data:source:remote"))
                implementation(project(":sample:shared:data:source:local"))
                implementation(libs.room.runtime)

                implementation(libs.ktor.content.negotiation)

                // Http
            }
        }

        commonTest.dependencies {
            implementation(libs.jetbrains.kotlin.test)
            implementation(libs.jetbrains.coroutines.test)
            implementation(libs.arch.storage.memory)
        }

        javaMain {
            dependencies {
            }
        }
        androidMain {
            dependencies {
            }
        }

        jvmMain {
            dependencies {
            }
        }

        appleMain {
            dependencies {
            }
        }

        wasmJsMain {
            dependencies {
            }
        }
        jsMain {
            dependencies {
            }
        }
    }
}
