val repositoryList: RepositoryHandler.() -> Unit = {
    google()
    mavenCentral()
    gradlePluginPortal()
    exclusiveContent {
        forRepository {
            ivy {
                url = uri("https://nodejs.org/dist")
                metadataSources { artifact() }
                patternLayout { artifact("v[revision]/[artifact]-v[revision]-[classifier].[ext]") }
            }
        }
        filter { includeModule("org.nodejs", "node") }
    }
    exclusiveContent {
        forRepository {
            ivy {
                url = uri("https://github.com/yarnpkg/yarn/releases")
                metadataSources { artifact() }
                patternLayout { artifact("download/v[revision]/[artifact]-v[revision].[ext]") }
            }
        }
        filter { includeModule("com.yarnpkg", "yarn") }
    }
}

extra["repositoryList"] = repositoryList
