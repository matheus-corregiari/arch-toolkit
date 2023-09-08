repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

val androidBuildVersion: String = "8.1.0"
val kotlinVersion: String = "1.9.10"

plugins {
    id("java-gradle-plugin")
    kotlin("jvm").version("1.9.10") // <-- Also Kotlin version!
}

kotlin { jvmToolchain(11) }

group = "com.toolkit.plugin"
version = "1.0.0"

dependencies {
    compileOnly(gradleApi())

    implementation("com.android.tools.build:gradle:$androidBuildVersion")
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("android-extensions", kotlinVersion))
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
    }
}
