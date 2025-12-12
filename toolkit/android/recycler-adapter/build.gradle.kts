plugins {
    id("toolkit-android-library")
    id("toolkit-android-publish")
}

android.namespace = "br.com.arch.toolkit.recyclerAdapter"

dependencies {
    // Libraries
    compileOnly(libs.jetbrains.stdlib)
    compileOnly(libs.androidx.recycler)
}

// Fixme - Make Tests
tasks.withType<AbstractTestTask>().configureEach { failOnNoDiscoveredTests = false }
