plugins { id("toolkit-multiplatform-sample") }

android.namespace = "br.com.arch.toolkit.sample.core"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain { dependencies { implementation(libs.jetbrains.datetime) } }
        commonTest { dependencies { implementation(libs.jetbrains.kotlin.test) } }
    }
}
