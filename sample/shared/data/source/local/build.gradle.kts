plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.room)
}

android.namespace = "br.com.arch.toolkit.sample.data.source.local"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.arch.storage.core)
                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation(libs.jetbrains.coroutines.core)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.arch.storage.datastore)
            }
        }
        jvmTest.dependencies {
            implementation(libs.jetbrains.kotlin.test)
            implementation(libs.jetbrains.coroutines.test)
        }
        jvmMain { dependencies { implementation(libs.arch.storage.datastore) } }
        appleMain { dependencies { implementation(libs.arch.storage.datastore) } }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room { schemaDirectory("$projectDir/schemas") }
