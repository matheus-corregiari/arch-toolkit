@file:Suppress("UnstableApiUsage")

pluginManagement {
    apply(from = "$rootDir/buildSrc/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)
}

dependencyResolutionManagement {
    apply(from = "$rootDir/buildSrc/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

// Root Project config
rootProject.name = "arch-toolkit"

// Toolkit Libraries
include(":toolkit:multi:event-observer")
include(":toolkit:multi:event-observer-compose")
include(":toolkit:multi:splinter")
include(":toolkit:multi:storage-core")
include(":toolkit:multi:storage-datastore")
include(":toolkit:multi:storage-memory")
include(":toolkit:multi:state-handle")
include(":toolkit:multi:test")

// Samples
val isIdeBuild: Boolean = extra.properties["android.injected.invoked.from.ide"] == "true"
if (isIdeBuild) {
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
    //include(":sample:target:web")
}

plugins {
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.6"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

kover { enableCoverage() }
