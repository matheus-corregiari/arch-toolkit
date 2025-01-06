plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.jvm)
}

kotlin { jvmToolchain(17) }

group = "com.toolkit.plugin"
version = "1.0.0"

dependencies {
    compileOnly(gradleApi())

    implementation(libs.androidx.plugin)
    implementation(libs.detekt)
    implementation(libs.ktlint)
    implementation(libs.jetbrains.plugin)
    implementation(libs.jetbrains.extensions)
    implementation(libs.jetbrains.kover)
}

sourceSets {
    main {
        java { srcDirs("src/main/java") }
        kotlin { srcDirs("src/main/kotlin") }
    }
}

gradlePlugin {
    plugins {
        //region Android
        create("toolkit-android-library") {
            id = "toolkit-android-library"
            displayName = "Toolkit Library Plugin"
            description =
                "Plug and play for modules those should be a exported library to the world!"
            implementationClass = "com.toolkit.plugin.android.ToolkitLibraryPlugin"
        }

        create("toolkit-android-sample") {
            id = "toolkit-android-sample"
            displayName = "Toolkit Sample Plugin"
            description = "Plug and play for modules to show the world the wonders of tomorrow!"
            implementationClass = "com.toolkit.plugin.android.ToolkitSamplePlugin"
        }

        create("toolkit-android-base") {
            id = "toolkit-android-base"
            displayName = "Toolkit Base Plugin"
            description = "All default config"
            implementationClass = "com.toolkit.plugin.android.ToolkitBasePlugin"
        }
        //endregion

        //region Multiplatform
        create("toolkit-multiplatform-library") {
            id = "toolkit-multiplatform-library"
            displayName = "Toolkit Library Plugin"
            description =
                "Plug and play for modules those should be a exported library to the world!"
            implementationClass = "com.toolkit.plugin.multiplatform.ToolkitLibraryPlugin"
        }

        create("toolkit-multiplatform-sample") {
            id = "toolkit-multiplatform-sample"
            displayName = "Toolkit Sample Plugin"
            description = "Plug and play for modules to show the world the wonders of tomorrow!"
            implementationClass = "com.toolkit.plugin.multiplatform.ToolkitSamplePlugin"
        }

        create("toolkit-multiplatform-base") {
            id = "toolkit-multiplatform-base"
            displayName = "Toolkit Base Plugin"
            description = "All default config"
            implementationClass = "com.toolkit.plugin.multiplatform.ToolkitBasePlugin"
        }
        //endregion

        //region Generic
        create("toolkit-optimize") {
            id = "toolkit-optimize"
            displayName = "Toolkit Optimization Plugin"
            description = "Optimize dependencies"
            implementationClass =
                "com.toolkit.plugin.ToolkitOptimizeDependenciesAndFilterTasksPlugin"
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
        //endregion
    }
}
