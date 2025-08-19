plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

android.namespace = "br.com.arch.toolkit.storage.datastore"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {

        // Common Setup (all targets)
        val commonMain by getting {
            dependencies {
                // Other Arch-Toolkit Dependencies
                implementation(project(":toolkit:multi:lumber"))
                implementation(project(":toolkit:multi:storage:core"))
            }
        }

        // Custom sources setup
        val opMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Storage
                api(libs.androidx.datastore.core)
                api(libs.androidx.datastore.preferences)

                // Jetbrains
                implementation(libs.jetbrains.coroutines.core)
            }
        }
        val noopMain by creating {
            dependsOn(commonMain)
        }

        // Target Setup
        androidMain { dependsOn(opMain) }
        jvmMain { dependsOn(opMain) }
        wasmJsMain { dependsOn(noopMain) }
    }
}
