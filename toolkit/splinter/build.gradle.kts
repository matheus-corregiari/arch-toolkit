plugins { id("toolkit-library") }

android.namespace = "br.com.arch.toolkit.splinter"

dependencies {
    // Other Modules
    implementation(project(":toolkit:event-observer"))

    // Libraries
    compileOnly(libraries.jetbrains.stdlib.jdk8)
    compileOnly(libraries.jetbrains.coroutines.core)
    compileOnly(libraries.jetbrains.coroutines.android)
    compileOnly(libraries.androidx.lifecycle.livedata)
    compileOnly(libraries.square.retrofit.main)
    compileOnly(libraries.square.timber)
}
