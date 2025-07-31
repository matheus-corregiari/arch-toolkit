plugins {
    id("toolkit-multiplatform-library")
}

android.namespace = "br.com.arch.toolkit.sample.github.shared"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
//    sourceSets.commonMain.dependencies { implementation() }
}
