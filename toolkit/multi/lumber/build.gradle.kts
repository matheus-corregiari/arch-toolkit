plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

android.namespace = "br.com.arch.toolkit.lumber"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    // Libraries
    sourceSets {
        // Common Setup
        commonMain.dependencies { implementation(libs.jetbrains.coroutines.core) }
        commonTest.dependencies { implementation(libs.jetbrains.kotlin.test) }

        // Custom source Setup
        val kotlinMain by creating { dependsOn(commonMain.get()) }

        // Target Setup
        androidMain { }
        jvmMain { dependencies { implementation(libs.ajalt.mordant) } }
        appleMain { dependencies { implementation(libs.ajalt.mordant) } }
        wasmJsMain { dependsOn(kotlinMain) }
        jsMain { dependsOn(kotlinMain) }
    }
}

publishing.publications.withType(MavenPublication::class.java) {
    pom {
        distributionManagement {
            relocation {
                message.set("This library was moved from another repository")
                groupId.set("io.github.matheus-corregiari")
                artifactId.set("arch-lumber")
                version.set("1.0.0")
            }
        }
    }
}

// Fixme - Make Tests
tasks.withType<AbstractTestTask>().configureEach { failOnNoDiscoveredTests = false }
