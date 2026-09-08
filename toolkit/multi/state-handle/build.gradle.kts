plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
    id("kotlin-parcelize")
    alias(libs.plugins.jetbrains.serialization)
}

android.namespace = "br.com.arch.toolkit.stateHandle"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

// Inform consumers without redirecting them to an incompatible package/API.
publishing.publications.withType<MavenPublication>().configureEach {
    pom.description.set(
        "Legacy State Handle. Migrated to " +
            "io.github.matheus-corregiari:event-observer-state:2.3.0 " +
            "(package br.com.arch.toolkit.eventObserver.state). " +
            "Migration guide: " +
            "https://matheus-corregiari.github.io/arch-event-observer/migration-state/. " +
            "Update dependencies and imports explicitly; this artifact is not redirected."
    )
}

kotlin {
    // Libraries
    sourceSets {
        // Common Setup
        commonMain.dependencies {
            implementation(libs.arch.lumber)
            implementation(libs.arch.event.observer)
            implementation(project(":toolkit:multi:splinter"))

            implementation(libs.jetbrains.coroutines.core)
            implementation(libs.jetbrains.serialization)
            implementation(libs.di.koin.core)
            implementation(libs.di.koin.composeViewModel)
        }
        commonTest.dependencies { implementation(libs.jetbrains.kotlin.test) }
    }
}

// Fixme - Make Tests
tasks.withType<AbstractTestTask>().configureEach { failOnNoDiscoveredTests = false }
