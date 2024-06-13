plugins {
    id("toolkit-android-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.util"

dependencies {
    // Other Modules
    implementation(project(":toolkit:storage"))

    // JetBrains
    implementation(libraries.jetbrains.coroutines.android)

    // Androidx
    implementation(libraries.androidx.startup)
    implementation(libraries.androidx.lifecycle.livedata)
}
