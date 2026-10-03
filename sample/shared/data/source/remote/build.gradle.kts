plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.jetbrains.serialization)
}

android.namespace = "br.com.arch.toolkit.sample.data.source.remote"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktorfit)
                implementation(libs.jetbrains.serialization)
                implementation(libs.jetbrains.datetime)
            }
        }
    }
}
