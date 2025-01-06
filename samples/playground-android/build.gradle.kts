plugins { id("toolkit-android-sample") }

android.namespace = "br.com.arch.toolkit.sample.playground"
android.defaultConfig.applicationId("br.com.arch.toolkit.sample.playground")

androidComponents.beforeVariants {
    it.enable = it.name == "debug"
}

dependencies {
    // Other Modules
    implementation(project(":toolkit:event-observer"))
    implementation(project(":toolkit:util"))
    implementation(project(":toolkit:storage"))
    implementation(project(":toolkit:splinter"))
    implementation(project(":toolkit:recycler-adapter"))
    implementation(project(":toolkit:statemachine"))
    implementation(project(":toolkit:delegate"))

    // Libraries
    implementation(libs.jetbrains.stdlib.jdk8)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recycler)
    implementation(libs.androidx.constraint)
    implementation(libs.square.timber)
    implementation(libs.google.gson)
}
