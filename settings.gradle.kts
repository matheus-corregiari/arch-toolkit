@file:Suppress("UnstableApiUsage")

pluginManagement {
    apply(from = "$rootDir/gradle/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    apply(from = "$rootDir/gradle/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

// Root Project config
rootProject.name = "arch-toolkit"

// Toolkit Libraries
include(":toolkit:multi:splinter")
include(":toolkit:multi:storage-core")
include(":toolkit:multi:storage-datastore")
include(":toolkit:multi:storage-memory")
include(":toolkit:multi:state-handle")
include(":toolkit:multi:test")

// Samples
val includeSamples =
    extra.properties["android.injected.invoked.from.ide"] == "true" ||
        providers.gradleProperty("includeSamples").orNull == "true"
if (includeSamples) {
    // Shared Modules with KMP Code to use in Targets
    include(":sample:shared:app")
    include(":sample:shared:feature:github-list")
    include(":sample:shared:feature:settings")
    include(":sample:shared:structure:repository")
    include(":sample:shared:structure:designSystem")
    include(":sample:shared:structure:core")

    // Targets
    include(":sample:target:android")
    include(":sample:target:desktop")
    include(":sample:target:web")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
