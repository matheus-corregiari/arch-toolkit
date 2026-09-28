plugins {
    id("toolkit-android-sample")
    alias(libs.plugins.compose.screenshot)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android {
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
    namespace = "br.com.arch.toolkit.sample.github.android"
    defaultConfig {
        applicationId = "br.com.arch.toolkit.sample.github.android"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    "screenshotTestImplementation"(libs.compose.screenshot.validation)
    "screenshotTestImplementation"(compose.uiTooling)
    "screenshotTestImplementation"(compose.preview)
    "screenshotTestImplementation"(compose.foundation)

    implementation(libs.arch.lumber)

    // Arch Toolkit Dependencies
    implementation(project(":sample:shared:app"))

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
