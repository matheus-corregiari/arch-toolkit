plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.eventObserver"
android.buildFeatures.androidResources = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies {
        implementation(libraries.jetbrains.stdlib.jdk8)
        implementation(libraries.jetbrains.coroutines.core)
        implementation(libraries.androidx.lifecycle.livedata)
    }
    sourceSets.androidMain.dependencies { implementation(libraries.jetbrains.coroutines.android) }

    // Test Libraries
    sourceSets.androidUnitTest.dependencies {
        implementation(libraries.androidx.lifecycle.runtime)
        implementation(libraries.androidx.test.core)
        implementation(libraries.jetbrains.test.coroutines)
        implementation(libraries.mockito.test.core)
        implementation(libraries.mockito.test.kotlin)
        implementation(libraries.mockk.test.android)
        implementation(libraries.mockk.test.agent)
    }
}
