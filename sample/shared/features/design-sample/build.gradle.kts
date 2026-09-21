plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
    alias(libs.plugins.jetbrains.serialization)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.easy.navigation)
}

android.namespace = "br.com.arch.toolkit.sample.feature.design"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":sample:shared:structure:core"))
                implementation(project(":sample:shared:structure:design:core"))
                implementation(project(":sample:shared:structure:design:widget"))
                implementation(project(":sample:shared:data:repository"))
                implementation(libs.easy.navigation)
                implementation("androidx.navigation3:navigation3-runtime:1.2.0-alpha04")
                implementation(libs.jetbrains.serialization)
                implementation(libs.di.koin.composeViewModel)
                implementation(compose.runtime)
                implementation(compose.runtimeSaveable)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.jetbrains.kotlin.test)
                implementation(libs.jetbrains.coroutines.test)
            }
        }
    }
}
