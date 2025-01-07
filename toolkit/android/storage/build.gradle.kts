plugins {
    id("toolkit-android-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.storage"

dependencies {
    // JetBrains
    implementation(libs.jetbrains.stdlib.jdk8)
    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.jetbrains.reflect)

    // Androidx
    implementation(libs.androidx.security)
    implementation(libs.androidx.startup)

    // Tools
    implementation(libs.square.timber)
}
