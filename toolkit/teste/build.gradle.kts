import com.toolkit.plugin.multiplatform.androidMain

plugins {
    id("toolkit-multiplatform-library")
}

android.namespace = "br.com.arch.toolkit.teste"
android.testNamespace = "br.com.arch.toolkit.teste.test"

kotlin {
    sourceSets {
        androidMain.configure {
            kotlin.srcDirs.removeIf { true }
            println(

                this.kotlin.srcDirs
            )
            dependencies {
                /**/
            }
        }
    }
}
