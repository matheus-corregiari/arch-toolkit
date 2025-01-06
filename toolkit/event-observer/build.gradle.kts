import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.eventObserver"
android.buildFeatures.androidResources = false
android.buildFeatures.buildConfig = false

kotlin {
    androidTarget { publishLibraryVariants("release") }

    // Libraries
    sourceSets.commonMain.dependencies {
        implementation(libs.jetbrains.stdlib.jdk8)
        implementation(libs.jetbrains.coroutines.core)
        implementation(libs.androidx.lifecycle.runtime)
    }
    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.lifecycle.livedata)
        implementation(libs.jetbrains.coroutines.android)
    }

    // Test Libraries
    sourceSets.commonTest.dependencies {
        implementation(libs.androidx.test.core)
        implementation(libs.jetbrains.test.coroutines)
        implementation(libs.mockito.test.core)
        implementation(libs.mockito.test.kotlin)
        implementation(libs.mockk.test.agent)
    }
    sourceSets.androidUnitTest.dependencies {
        implementation(libs.mockk.test.android)
    }
}

afterEvaluate {
    val javadoc = tasks.withType(Javadoc::class)
    val sources = tasks.withType(Jar::class)
    val sign = tasks.withType(Sign::class)
    val androidSources = tasks.named("androidReleaseSourcesJar").get()
    tasks.assemble.dependsOn(
        (javadoc + sources + androidSources + sign).map { tasks.named(it.name) }
    )
}
