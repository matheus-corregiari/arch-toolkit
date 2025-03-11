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
include(":toolkit:multi:event-observer")
include(":toolkit:multi:splinter")
include(":toolkit:multi:lumber")
include(":toolkit:android:delegate")
include(":toolkit:android:foldable")
include(":toolkit:android:recycler-adapter")
include(":toolkit:android:storage")
include(":toolkit:android:util")
include(":toolkit:android:statemachine")

// Samples
val isIdeBuild: Boolean = extra.properties["android.injected.invoked.from.ide"] == "true"
if (isIdeBuild) {
    include(":samples:github-list-project")
}
