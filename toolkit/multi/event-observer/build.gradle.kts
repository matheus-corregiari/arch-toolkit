plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

android.namespace = "br.com.arch.toolkit.eventObserver"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies {
        implementation(libs.jetbrains.coroutines.core)
        implementation(libs.androidx.lifecycle.runtime)
    }
    sourceSets.androidMain.dependencies {
        implementation(libs.jetbrains.coroutines.android)
        implementation(libs.androidx.lifecycle.livedata)
    }
    sourceSets.jvmMain.dependencies {
        implementation(libs.jetbrains.coroutines.jvm)
    }

    // Test Libraries
    sourceSets.commonTest.dependencies {
        implementation(libs.jetbrains.coroutines.test)
        implementation(libs.junit.test)
        implementation(libs.mockito.test.core)
        implementation(libs.mockito.test.kotlin)
    }
    sourceSets.androidUnitTest.dependencies {
        implementation(libs.mockk.test.agent)
        implementation(libs.mockk.test.android)
        implementation(libs.androidx.test.core)
    }
}
