plugins {
    id("toolkit-android-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.splinter"

dependencies {
    // Other Modules
    implementation(project(":toolkit:multi:event-observer"))

    // Libraries
    compileOnly(libs.jetbrains.stdlib.jdk8)
    compileOnly(libs.jetbrains.coroutines.core)
    compileOnly(libs.jetbrains.coroutines.android)
    compileOnly(libs.androidx.lifecycle.livedata)
    compileOnly(libs.square.retrofit.main)
    compileOnly(libs.square.timber)
}
