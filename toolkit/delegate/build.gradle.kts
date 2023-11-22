plugins {
    id("toolkit-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.delegate"

dependencies {
    // Libraries
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.androidx.annotation)
    implementation(libraries.androidx.appcompat)
    compileOnly(libraries.androidx.recycler)
}
