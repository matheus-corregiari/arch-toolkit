plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

android.namespace = "br.com.arch.toolkit.lumber"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets {
        // Common Setup
        commonMain.dependencies { implementation(libs.jetbrains.coroutines.core) }
        commonTest.dependencies { implementation(libs.jetbrains.kotlin.test) }

        // Custom source Setup
        val javaMain by creating { dependsOn(commonMain.get()) }
        val kotlinMain by creating { dependsOn(commonMain.get()) }

        // Target Setup
        androidMain { dependsOn(javaMain) }
        jvmMain {
            dependsOn(javaMain)
            dependencies { implementation(libs.ajalt.mordant) }
        }
        wasmJsMain { dependsOn(kotlinMain) }
    }
}
