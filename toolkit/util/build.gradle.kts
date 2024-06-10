plugins {
    id("toolkit-android-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.util"

dependencies {
    // Androidx
    implementation(libraries.androidx.startup)
}
