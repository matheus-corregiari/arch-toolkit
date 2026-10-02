@file:Suppress("UnstableApiUsage")

pluginManagement {
    apply(from = "$rootDir/repositories.gradle.kts")
    val repositoryList = extra["repositoryList"] as RepositoryHandler.() -> Unit
    repositories(repositoryList)
}

dependencyResolutionManagement {
    apply(from = "$rootDir/repositories.gradle.kts")
    val repositoryList = extra["repositoryList"] as RepositoryHandler.() -> Unit
    repositories(repositoryList)
    versionCatalogs { register("libs") { from(files("$rootDir/../gradle/libs.versions.toml")) } }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}
