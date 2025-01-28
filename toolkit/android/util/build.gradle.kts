plugins {
    id("toolkit-android-library")
    id("toolkit-android-publish")
}

android.namespace = "br.com.arch.toolkit.util"

dependencies {
    // Other Modules
    implementation(project(":toolkit:android:storage"))

    // JetBrains
    implementation(libs.jetbrains.coroutines.android)

    // Androidx
    implementation(libs.androidx.startup)
    implementation(libs.androidx.lifecycle.livedata)
}
