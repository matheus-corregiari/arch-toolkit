package old

import NeededDependencies
import dependencies.ComposeDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ComposePlugin : Plugin<Project>, NeededDependencies by ComposeDependencies() {

    private val androidProjectConfig by lazy { AndroidProjectConfig() }

    override fun apply(target: Project) {
        target.plugins.apply("com.android.library")
        target.plugins.apply("kotlin-android")
        target.plugins.apply("kotlin-parcelize")

        configureAndroid(target)
        addDependencies(target)
    }

    private fun configureAndroid(target: Project) {
        val composeVersion = target.getCatalog().findVersion(COMPOSE_CATALOG_NAME)

        androidProjectConfig.config(
            target = target,
            buildBaseExtensionBlock = {
                composeOptions {
                    kotlinCompilerExtensionVersion = composeVersion.get().requiredVersion
                }
            }
        ) { buildFeatures ->
            buildFeatures.compose = true
        }
    }
}
