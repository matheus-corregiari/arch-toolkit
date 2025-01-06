val repositoryList: RepositoryHandler.() -> Unit = {
    google()
    mavenCentral()
    gradlePluginPortal()
}

extra["repositoryList"] = repositoryList
