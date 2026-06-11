plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
    alias(libs.plugins.jetbrains.compose.kotlin)
    alias(libs.plugins.jetbrains.compose.compiler)
}

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies {
        implementation(libs.jetbrains.compose.runtime)
        implementation(libs.jetbrains.coroutines.core)
        implementation(libs.jetbrains.serialization)
    }
}

// Fixme - Make Tests
tasks.withType<AbstractTestTask>().configureEach { failOnNoDiscoveredTests = false }
