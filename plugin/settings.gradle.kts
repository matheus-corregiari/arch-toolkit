@file:Suppress("UnstableApiUsage")

// Setting up all repository plugins
pluginManagement {
    apply(from = "$rootDir/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)
}

dependencyResolutionManagement {
    apply(from = "$rootDir/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)

    versionCatalogs {
        register("libraries") {
            from(files("$rootDir/../tools/libs.versions.toml"))
        }
        register("pluginLibraries") {
            from(files("$rootDir/../tools/plugin.versions.toml"))
        }
    }
}
