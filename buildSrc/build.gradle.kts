plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

kotlin { jvmToolchain(21) }

group = "com.toolkit.plugin"
version = "1.0.0"

dependencies {
    compileOnly(gradleApi())

    implementation(libs.androidx.plugin) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    implementation(libs.detekt) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    implementation(libs.ktlint) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    implementation(libs.jetbrains.plugin) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    implementation(libs.jetbrains.extensions) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    implementation(libs.jetbrains.dokka) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    implementation(libs.vanniktech.publish) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
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
            description = "Plug and play for modules those should be a exported library!"
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

        create("toolkit-android-publish") {
            id = "toolkit-android-publish"
            displayName = "Toolkit Publish Plugin"
            description = "Enables and configure publish for module"
            implementationClass = "com.toolkit.plugin.android.ToolkitPublishPlugin"
        }
        //endregion

        //region Desktop
        create("toolkit-desktop-sample") {
            id = "toolkit-desktop-sample"
            displayName = "Toolkit Sample Plugin"
            description = "Plug and play for modules to show the world the wonders of tomorrow!"
            implementationClass = "com.toolkit.plugin.desktop.ToolkitSamplePlugin"
        }
        //endregion

        //region Wasm
        create("toolkit-web-sample") {
            id = "toolkit-web-sample"
            displayName = "Toolkit Sample Plugin"
            description = "Plug and play for modules to show the world the wonders of tomorrow!"
            implementationClass = "com.toolkit.plugin.web.ToolkitSamplePlugin"
        }
        //endregion

        //region Multiplatform
        create("toolkit-multiplatform-sample") {
            id = "toolkit-multiplatform-sample"
            displayName = "Toolkit Sample Plugin"
            description =
                "Plug and play for modules those should be a exported library to the world!"
            implementationClass = "com.toolkit.plugin.multiplatform.ToolkitSamplePlugin"
        }

        create("toolkit-multiplatform-library") {
            id = "toolkit-multiplatform-library"
            displayName = "Toolkit Library Plugin"
            description =
                "Plug and play for modules those should be a exported library to the world!"
            implementationClass = "com.toolkit.plugin.multiplatform.ToolkitLibraryPlugin"
        }

        create("toolkit-multiplatform-base") {
            id = "toolkit-multiplatform-base"
            displayName = "Toolkit Base Plugin"
            description = "All default config"
            implementationClass = "com.toolkit.plugin.multiplatform.ToolkitBasePlugin"
        }

        create("toolkit-multiplatform-publish") {
            id = "toolkit-multiplatform-publish"
            displayName = "Toolkit Publish Plugin"
            description = "Enables and configure publish for module"
            implementationClass = "com.toolkit.plugin.multiplatform.ToolkitPublishPlugin"
        }
        //endregion

        //region Generic
        create("toolkit-optimize") {
            id = "toolkit-optimize"
            displayName = "Toolkit Optimization Plugin"
            description = "Optimize dependencies"
            implementationClass = "com.toolkit.plugin.ToolkitOptimizePlugin"
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
        //endregion
    }
}

val normalize = (1..21)
    .mapNotNull { libs.create("x-normalize-x${it.toString().padStart(3, '0')}").orNull }
    .map { module -> "${module.group}:${module.name}:${module.versionConstraint.requiredVersion}:" }

configurations.configureEach {
    resolutionStrategy {
        failOnVersionConflict()
        preferProjectModules()
        setForcedModules(normalize)
    }
}
