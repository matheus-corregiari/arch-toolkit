plugins {
    id("toolkit-android-library")
    id("toolkit-android-publish")
}

android.namespace = "br.com.arch.toolkit.delegate"

dependencies {
    // Libraries
    implementation(libs.jetbrains.stdlib)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    compileOnly(libs.androidx.recycler)
}
