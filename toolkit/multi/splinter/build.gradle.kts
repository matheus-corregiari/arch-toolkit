plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-multiplatform-publish")
}

kotlin {
    // Libraries
    sourceSets.commonMain.dependencies {
        implementation(libs.arch.event.observer)
        implementation(libs.arch.lumber)

        implementation(libs.jetbrains.coroutines.core)
        implementation(libs.androidx.lifecycle.runtime)
        implementation(libs.androidx.annotation)
    }
    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.lifecycle.livedata)

        // Support for Retrofit
        compileOnly(libs.square.retrofit.main)
    }
    sourceSets.jvmMain.dependencies {}
    sourceSets.commonTest.dependencies {
        implementation(libs.jetbrains.kotlin.test)
        implementation(libs.jetbrains.coroutines.test)
    }
}
