plugins {
    id("toolkit-multiplatform-sample")
    id("kotlin-parcelize")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
    alias(libs.plugins.jetbrains.serialization)
}

android.namespace = "br.com.arch.toolkit.sample.github.shared"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Arch Toolkit Dependencies
            implementation(project(":toolkit:multi:lumber"))
            implementation(project(":toolkit:multi:splinter"))
            implementation(project(":toolkit:multi:event-observer"))
            implementation(project(":toolkit:multi:event-observer-compose"))

            // Jetbrains Compose Tools
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.materialIconsExtended)
            implementation(compose.uiUtil)
            implementation(compose.animation)
            implementation(compose.components.resources)
            implementation(compose.uiTooling)
            implementation(compose.material3AdaptiveNavigationSuite)

            // Other Tools
            implementation(libs.androidx.compose.material3.window)
            implementation(libs.androidx.compose.material3.adaptive)
            implementation(libs.androidx.compose.navigation)
            implementation(libs.jetbrains.serialization)
            implementation(libs.jetbrains.datetime)
            implementation(libs.coil.core)
            implementation(libs.coil.network)
            implementation(libs.haze.core)
            implementation(libs.haze.materials)

            // Dependency Injection
            implementation(libs.di.koin.core)
            implementation(libs.di.koin.compose)
            implementation(libs.di.koin.composeViewModel)

            // Http
            implementation(libs.ktorfit)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.logging)

            // Storage
            api(libs.androidx.datastore.core)
            api(libs.androidx.datastore.preferences)
        }

        androidMain.dependencies {
            implementation(project(":toolkit:android:util"))
            implementation(libs.jetbrains.coroutines.android)
        }

        jvmMain.dependencies {
            implementation(libs.jetbrains.coroutines.jvm)
        }
    }
}
