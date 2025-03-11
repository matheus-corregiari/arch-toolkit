plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

android.namespace = "br.com.arch.toolkit.lumber"
android.buildFeatures.androidResources = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies { implementation(libs.slf4j.core) }
    sourceSets.androidMain.dependencies { implementation(libs.slf4j.nop) }
    sourceSets.jvmMain.dependencies { implementation(libs.slf4j.simple) }
}
