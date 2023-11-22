plugins { id("toolkit-sample") }

android.namespace = "br.com.arch.toolkit.sample.statemachine"
android.defaultConfig.applicationId("br.com.arch.toolkit.sample.statemachine")

dependencies {
    // Other Modules
    implementation(project(":toolkit:delegate"))
    implementation(project(":toolkit:statemachine"))

    // Libraries
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.androidx.annotation)
    implementation(libraries.androidx.appcompat)
}
