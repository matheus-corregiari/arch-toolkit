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
}

// Fixme - Make Tests
tasks.withType<AbstractTestTask>().configureEach { failOnNoDiscoveredTests = false }

publishing.publications.withType(MavenPublication::class.java) {
    pom {
        distributionManagement {
            relocation {
                message.set("This library was moved from another repository")
                groupId.set("io.github.matheus-corregiari")
                artifactId.set("arch-android")
                version.set("1.0.0")
            }
        }
    }
}
