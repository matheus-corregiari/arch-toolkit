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
include(":toolkit:multi:splinter")
include(":toolkit:multi:test")

// Samples
val isIdeBuild: Boolean = extra.properties["android.injected.invoked.from.ide"] == "true"
val includeSamples: Boolean = isIdeBuild || providers.gradleProperty("includeSamples").isPresent
if (includeSamples) {
    // Shared Modules with KMP Code to use in Targets
    include(":sample:shared:app")
    include(":sample:shared:features:github-sample")
    include(":sample:shared:features:settings")
    include(":sample:shared:features:toolkit-sample")
    include(":sample:shared:features:design-sample")
    include(":sample:shared:data:repository")
    include(":sample:shared:structure:design:widget")
    include(":sample:shared:structure:design:core")
    include(":sample:shared:structure:core")

    include(":sample:shared:data:source:remote")
    include(":sample:shared:data:source:local")
    include(":sample:shared:structure:http")

    // Targets
    include(":sample:target:android")
    include(":sample:target:desktop")
    //include(":sample:target:web")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
