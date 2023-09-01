repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

plugins {
    id("java-gradle-plugin")
    kotlin("jvm").version("1.9.10")
}

kotlin { jvmToolchain(11) }

group = "com.toolkit.plugin"
version = "1.0.0"

dependencies {
    compileOnly(gradleApi())

    implementation("com.android.tools.build:gradle:8.1.0")
    implementation(kotlin("gradle-plugin", "1.9.10"))
    implementation(kotlin("android-extensions", "1.9.10"))
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
    }
}
