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
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.androidx.annotation)
    implementation(libraries.androidx.appcompat)
    implementation(libraries.androidx.recycler)
    implementation(libraries.androidx.constraint)
    implementation(libraries.square.timber)
    implementation(libraries.google.gson)
}
