val repositoryList: RepositoryHandler.() -> Unit = {
    mavenLocal()
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
    exclusiveContent {
        forRepository {
            ivy {
                url = uri("https://github.com/WebAssembly/binaryen/releases/download")
                patternLayout {
                    artifact("version_[revision]/[artifact]-version_[revision]-[classifier].[ext]")
                }
                metadataSources { artifact() }
            }
        }
        filter { includeModule("com.github.webassembly", "binaryen") }
    }
}

extra["repositoryList"] = repositoryList
