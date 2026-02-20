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
    implementation(libs.arch.lumber)
    implementation(project(":toolkit:android:util"))

    // Test Libraries
    testImplementation(libs.junit.test)
    testImplementation(libs.jetbrains.stdlib)
}

// Fixme - Make Tests
tasks.withType<AbstractTestTask>().configureEach { failOnNoDiscoveredTests = false }
