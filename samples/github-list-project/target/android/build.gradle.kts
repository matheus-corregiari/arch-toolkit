plugins {
    id("toolkit-android-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android {
    namespace = "br.com.arch.toolkit.sample.github.android"
    defaultConfig {
        applicationId = "br.com.arch.toolkit.sample.github.android"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies{
    // Regular Dependencies
    implementation(libs.androidx.compose.activity)
    implementation(libs.jetbrains.coroutines.android)
    implementation(compose.runtime)
    implementation(compose.material3)
}
