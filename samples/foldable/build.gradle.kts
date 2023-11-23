plugins { id("toolkit-android-library") }

android.namespace = "br.com.arch.toolkit.sample.foldable"
android.defaultConfig.applicationId("br.com.arch.toolkit.sample.foldable")

dependencies {
    // Other Modules
    implementation(project(":toolkit:foldable"))
    implementation(project(":toolkit:delegate"))

    // Libraries
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.androidx.appcompat)
    implementation(libraries.androidx.constraint)
    implementation(libraries.androidx.window)
}

