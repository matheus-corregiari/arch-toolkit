plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

android.namespace = "br.com.arch.toolkit.splinter"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies {
        implementation(project(":toolkit:multi:event-observer"))
        implementation(project(":toolkit:multi:lumber"))

        implementation(libs.jetbrains.coroutines.core)
        implementation(libs.androidx.lifecycle.runtime)
        implementation(libs.androidx.annotation)
    }
    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.lifecycle.livedata)

        // Support for Retrofit
        compileOnly(libs.square.retrofit.main)
    }
    sourceSets.jvmMain.dependencies {}
    sourceSets.commonTest.dependencies {
        implementation(libs.junit.test)
        implementation(libs.jetbrains.test.coroutines)
    }
}
