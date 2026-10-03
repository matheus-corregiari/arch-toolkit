plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

android.namespace = "br.com.arch.toolkit.sample.shared.app"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Showcase"
            isStatic = true
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.arch.lumber)

                implementation(libs.di.koin.composeViewModel)
                implementation(libs.di.koin.compose)
                implementation(libs.di.koin.core)
                implementation(libs.jetbrains.datetime)
                implementation(libs.arch.storage.core)

                implementation(libs.jetbrains.compose.ui)
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.runtime.saveable)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.material.icons)
                implementation(libs.jetbrains.compose.material3.navigation.suite)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.animation)

                // Structure
                api(project(":sample:shared:structure:core"))
                api(project(":sample:shared:structure:design:widget"))
                implementation(project(":sample:shared:data:repository"))

                implementation(project(":sample:shared:data:source:remote"))
                implementation(project(":sample:shared:structure:http"))
                implementation(libs.ktorfit)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.jetbrains.serialization)

                implementation(project(":sample:shared:data:source:local"))
                implementation(libs.room.runtime)

                implementation(libs.easy.navigation)
                implementation(libs.androidx.compose.material3.adaptive)
                implementation(libs.navigation3.ui)
                implementation(libs.navigation3.runtime)

                implementation(project(":sample:shared:features:toolkit-sample"))
                implementation(project(":sample:shared:features:design-sample"))

                // Features
                implementation(project(":sample:shared:features:github-sample"))
                implementation(project(":sample:shared:features:settings"))

                // Arch Toolkit Dependencies

                // Compose
                implementation(libs.jetbrains.compose.resources)
            }
        }

        commonTest.dependencies {
            implementation(libs.jetbrains.kotlin.test)
        }
        androidMain {
            dependencies {
                implementation(libs.arch.storage.datastore)
                implementation(libs.androidx.lifecycle.process)
                implementation(libs.google.material)
            }
        }
        jvmTest.dependencies {
            implementation(libs.jetbrains.compose.ui.test.junit4)
            implementation(libs.arch.storage.memory)
            implementation(compose.desktop.currentOs)
        }
        jvmMain { dependencies { implementation(libs.arch.storage.datastore) } }
        appleMain { dependencies { implementation(libs.arch.storage.datastore) } }
    }
}
