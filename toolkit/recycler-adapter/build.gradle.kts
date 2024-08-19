plugins {
    id("toolkit-android-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.recyclerAdapter"

dependencies {
    // Libraries
    compileOnly(libraries.jetbrains.stdlib.jdk8)
    compileOnly(libraries.androidx.recycler)
}
