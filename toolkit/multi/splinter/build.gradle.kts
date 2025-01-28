plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

android.namespace = "br.com.arch.toolkit.splinter"
android.buildFeatures.androidResources = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies {
        implementation(project(":toolkit:multi:event-observer"))

        implementation(libs.jetbrains.coroutines.core)
        implementation(libs.androidx.lifecycle.runtime)
        implementation(libs.androidx.annotation)
    }
    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.lifecycle.livedata)
        implementation(libs.jetbrains.coroutines.android)
        implementation(libs.square.timber)
        compileOnly(libs.square.okhttp.core)
        compileOnly(libs.square.retrofit.main)
    }
}
