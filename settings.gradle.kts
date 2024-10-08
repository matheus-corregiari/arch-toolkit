@file:Suppress("UnstableApiUsage")

pluginManagement {
    apply(from = "$rootDir/plugin/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)
}

dependencyResolutionManagement {
    apply(from = "$rootDir/plugin/repositories.gradle.kts")
    val repositoryList: RepositoryHandler.() -> Unit by extra
    repositories(repositoryList)

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    versionCatalogs {
        register("libraries") {
            from(files("$rootDir/tools/libs.versions.toml"))
        }
        register("pluginLibraries") {
            from(files("$rootDir/tools/plugin.versions.toml"))
        }
    }
}

// Root Project config
rootProject.name = "Arch Toolkit"

// Plugins --> The Dev just want to be happy
includeBuild("plugin")

// Toolkit Libraries
include(":toolkit:delegate")
include(":toolkit:event-observer")
include(":toolkit:foldable")
include(":toolkit:recycler-adapter")
include(":toolkit:splinter")
include(":toolkit:storage")
include(":toolkit:util")
include(":toolkit:statemachine")

// Samples
val isIdeBuild: Boolean = extra.properties["android.injected.invoked.from.ide"] == "true"
if (isIdeBuild) {
    include(":samples:github-list-project")
    include(":samples:playground")
    include(":samples:playground-android")
}
