package old

import NeededDependencies
import old.dependencies.KotlinDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class AndroidModulePlugin : Plugin<Project>, NeededDependencies by KotlinDependencies() {

    private val androidProjectConfig by lazy { AndroidProjectConfig() }

    override fun apply(target: Project) {
        target.plugins.apply("com.android.library")
        target.plugins.apply("kotlin-android")
        target.plugins.apply("kotlin-parcelize")

        configureAndroid(target)
        addDependencies(target)
    }

    private fun configureAndroid(target: Project) {
        androidProjectConfig.config(
            target = target,
            buildBaseExtensionBlock = null
        ) { buildFeatures ->
            buildFeatures.viewBinding = true
        }
    }
}
