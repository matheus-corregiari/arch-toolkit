plugins {
    id("toolkit-multiplatform-sample")
}

android.namespace = "br.com.arch.toolkit.sample.data.source.local"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies { implementation(libs.arch.storage.core) }
        }
        androidMain {
            dependencies {
                implementation(libs.arch.android)
                implementation(libs.arch.storage.datastore)
            }
        }
        jvmMain { dependencies { implementation(libs.arch.storage.datastore) } }
        appleMain { dependencies { implementation(libs.arch.storage.datastore) } }
    }
}
