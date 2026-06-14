plugins {
    id("toolkit-multiplatform-library")
}

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
