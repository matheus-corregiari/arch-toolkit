plugins {
    id("toolkit-android-library")
    id("toolkit-android-publish")
}

android.namespace = "br.com.arch.toolkit.storage"

dependencies {
    // JetBrains
    implementation(libs.jetbrains.stdlib)
    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.jetbrains.reflect)

    // Androidx
    implementation(libs.androidx.security)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.lifecycle.livedata)

    // Other Module depedencies
    implementation(project(":toolkit:multi:lumber"))
    implementation(project(":toolkit:android:util"))
}
