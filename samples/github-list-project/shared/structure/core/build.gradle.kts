plugins {
    id("toolkit-multiplatform-sample")
    id("kotlin-parcelize")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android.namespace = "br.com.arch.toolkit.sample.github.shared.structure.core"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // Arch Toolkit Dependencies
                api(project(":toolkit:multi:lumber"))
                api(project(":toolkit:multi:storage:core"))

                // Jetbrains Compose Tools
                api(compose.ui)
                api(compose.uiUtil)
                api(compose.runtime)
                api(compose.runtimeSaveable)
                api(compose.components.resources)
                api(compose.materialIconsExtended)
                api(compose.material3)
                api(compose.material3AdaptiveNavigationSuite)
                api(compose.foundation)
                api(compose.animation)

                // Jetbrains
                api(libs.jetbrains.datetime)

                // Dependency Injection
                api(libs.di.koin.core)
                api(libs.di.koin.compose)
                api(libs.di.koin.composeViewModel)
            }
        }

        androidMain {
            dependencies {
                api(libs.jetbrains.coroutines.android)
            }
        }

        jvmMain {
            dependencies {
                api(libs.jetbrains.coroutines.jvm)
            }
        }
        wasmJsMain { }
        jsMain { }
    }
}
