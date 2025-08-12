plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

android.namespace = "br.com.arch.toolkit.lumber"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    @Suppress("unused")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.jetbrains.coroutines.core)
            }
        }
        val javaMain by creating {
            dependsOn(commonMain)
        }
        val kotlinMain by creating {
            dependsOn(commonMain)
        }
        val androidMain by getting {
            dependsOn(javaMain)
        }
        val jvmMain by getting {
            dependsOn(javaMain)
            dependencies {
                implementation(libs.slf4j.core)
                implementation(libs.slf4j.simple)
                implementation(libs.ajalt.mordant)
            }
        }
        val wasmJsMain by getting { dependsOn(kotlinMain) }
    }
}
