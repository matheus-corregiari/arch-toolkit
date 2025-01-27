plugins {
    id("toolkit-android-library")
    id("toolkit-android-publish")
}

android.namespace = "br.com.arch.toolkit.recyclerAdapter"

dependencies {
    // Libraries
    compileOnly(libs.jetbrains.stdlib.jdk8)
    compileOnly(libs.androidx.recycler)
}
