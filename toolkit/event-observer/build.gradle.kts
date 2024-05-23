plugins {
    id("toolkit-android-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.livedata"

dependencies {
    // Libraries
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.jetbrains.coroutines.core)
    implementation(libraries.jetbrains.coroutines.android)
    implementation(libraries.androidx.lifecycle.livedata)

    // Test Libraries
    testImplementation(libraries.androidx.lifecycle.runtime)
    testImplementation(libraries.androidx.test.core)
    testImplementation(libraries.jetbrains.test.coroutines)
    testImplementation(libraries.mockito.test.core)
    testImplementation(libraries.mockito.test.kotlin)
}
