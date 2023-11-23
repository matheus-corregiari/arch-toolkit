plugins { id("toolkit-android-sample") }

android.namespace = "br.com.arch.toolkit.sample.recycler.adapter"
android.defaultConfig.applicationId("br.com.arch.toolkit.sample.recycler.adapter")

dependencies {
    // Other Modules
    implementation(project(":toolkit:recycler-adapter"))
    implementation(project(":toolkit:delegate"))

    // Libraries
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.androidx.annotation)
    implementation(libraries.androidx.appcompat)
    implementation(libraries.androidx.recycler)
}
