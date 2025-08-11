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

dependencies {
    // Arch Toolkit Dependencies
    implementation(project(":samples:github-list-project:shared"))
    implementation(project(":toolkit:multi:lumber"))

    // Jetbrains Compose Tools
    implementation(compose.runtime)
    implementation(compose.material3)

    // Other Dependencies
    implementation(libs.google.material)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.jetbrains.coroutines.android)
    implementation(libs.di.koin.android)
}
