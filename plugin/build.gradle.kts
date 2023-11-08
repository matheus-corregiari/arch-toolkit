repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

plugins {
    id("java-gradle-plugin")
    alias(pluginLibraries.plugins.jvm)
}

kotlin { jvmToolchain(17) }

group = "com.toolkit.plugin"
version = "1.0.0"

dependencies {
    compileOnly(gradleApi())

    implementation(pluginLibraries.androidx.plugin)
    implementation(pluginLibraries.detekt)
    implementation(pluginLibraries.ktlint)
    implementation(pluginLibraries.jetbrains.plugin)
    implementation(pluginLibraries.jetbrains.extensions)
    implementation(pluginLibraries.jetbrains.kover)
}

sourceSets {
    main {
        java { srcDirs("src/main/java") }
        kotlin { srcDirs("src/main/kotlin") }
    }
}

gradlePlugin {
    plugins {
        create("toolkit-library") {
            id = "toolkit-library"
            displayName = "Toolkit Library Plugin"
            description =
                "Plug and play for modules those should be a exported library to the world!"
            implementationClass = "com.toolkit.plugin.ToolkitLibraryPlugin"
        }

        create("toolkit-sample") {
            id = "toolkit-sample"
            displayName = "Toolkit Sample Plugin"
            description = "Plug and play for modules to show the world the wonders of tomorrow!"
            implementationClass = "com.toolkit.plugin.ToolkitSamplePlugin"
        }

        create("toolkit-base") {
            id = "toolkit-base"
            displayName = "Toolkit Base Plugin"
            description = "All default config"
            implementationClass = "com.toolkit.plugin.ToolkitBasePlugin"
        }

        create("toolkit-compose") {
            id = "toolkit-compose"
            displayName = "Toolkit Compose Plugin"
            description = "Enables and configure compose for module"
            implementationClass = "com.toolkit.plugin.ToolkitComposePlugin"
        }

        create("toolkit-lint") {
            id = "toolkit-lint"
            displayName = "Toolkit Lint Plugin"
            description = "Enables and configure lint for module"
            implementationClass = "com.toolkit.plugin.ToolkitLintPlugin"
        }

        create("toolkit-test") {
            id = "toolkit-test"
            displayName = "Toolkit Test Plugin"
            description = "Enables and configure test for module"
            implementationClass = "com.toolkit.plugin.ToolkitTestPlugin"
        }

        create("toolkit-group") {
            id = "toolkit-group"
            displayName = "Toolkit Group Plugin"
            description = "Enables and configure group for module"
            implementationClass = "com.toolkit.plugin.ToolkitGroupPlugin"
        }

        create("toolkit-publish") {
            id = "toolkit-publish"
            displayName = "Toolkit Publish Plugin"
            description = "Enables and configure publish for module"
            implementationClass = "com.toolkit.plugin.ToolkitPublishPlugin"
        }
    }
}
