plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
    id("kotlin-parcelize")
    alias(libs.plugins.jetbrains.serialization)
}

android.namespace = "br.com.arch.toolkit.stateHandle"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

// Relocate each Maven publication to its matching Event Observer State variant.
publishing.publications.withType<MavenPublication>().configureEach {
    val publication = this
    pom.description.set("State Handle has moved to Event Observer State.")
    pom.withXml {
        val relocation = asNode().appendNode("distributionManagement").appendNode("relocation")
        relocation.appendNode("groupId", "io.github.matheus-corregiari")
        relocation.appendNode(
            "artifactId",
            publication.artifactId.replaceFirst("state-handle", "event-observer-state")
        )
        relocation.appendNode("version", "2.3.0")
        relocation.appendNode(
            "message",
            "Migrated to Event Observer State. Update imports to " +
                "br.com.arch.toolkit.eventObserver.state. Migration guide: " +
                "https://matheus-corregiari.github.io/arch-event-observer/migration-state/."
        )
    }
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
