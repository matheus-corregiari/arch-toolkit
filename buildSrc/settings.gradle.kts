@file:Suppress("UnstableApiUsage")

pluginManagement {
    apply(from = "$rootDir/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)
}

dependencyResolutionManagement {
    apply(from = "$rootDir/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)
    versionCatalogs { register("libs") { from(files("$rootDir/../gradle/libs.versions.toml")) } }
}
