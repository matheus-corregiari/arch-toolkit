plugins {
    id("toolkit-android-library")
    id("toolkit-android-publish")
}

android.namespace = "br.com.arch.toolkit.delegate"

dependencies {
    // Libraries
    implementation(libs.jetbrains.stdlib)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    compileOnly(libs.androidx.recycler)
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
