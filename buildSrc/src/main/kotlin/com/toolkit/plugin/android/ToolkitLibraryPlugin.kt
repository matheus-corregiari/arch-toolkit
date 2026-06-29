package com.toolkit.plugin.android

import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.applyPlugins
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Configures an Android library module.
 *
 * The plugin applies Android library defaults plus shared lint, test, and dependency optimization
 * conventions used by publishable Android artifacts.
 */
internal class ToolkitLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-library")
        target.plugins.apply("toolkit-android-base")

        target.setupAndroidLibraryModule()
        target.androidLibrary.regularSourceSets()

        target.plugins.apply("toolkit-lint")
        target.plugins.apply("toolkit-test")
        target.plugins.apply("toolkit-optimize")
    }
}
