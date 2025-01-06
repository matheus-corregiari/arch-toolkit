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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

// Root Project config
rootProject.name = "Arch Toolkit"

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
}
