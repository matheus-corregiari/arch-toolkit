plugins {
    id("toolkit-android-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.storage"

dependencies {
    // JetBrains
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.jetbrains.coroutines.core)
    implementation(libraries.jetbrains.reflect)

    // Androidx
    implementation(libraries.androidx.security)
    implementation(libraries.androidx.startup)

    // Tools
    implementation(libraries.square.timber)
}
