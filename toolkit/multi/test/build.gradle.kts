plugins {
    id("toolkit-multiplatform-library")
}

android.namespace = "br.com.arch.toolkit.test"
android.androidResources { enable = false }
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies {
        api(libs.jetbrains.kotlin.test)
    }
    sourceSets.androidMain.dependencies {
        api(libs.androidx.compose.testManifest)
        api(libs.androidx.test.junit)
        api(libs.robolectric.test)
    }
}

// Fixme - Make Tests
tasks.withType<AbstractTestTask>().configureEach { failOnNoDiscoveredTests = false }
