package com.toolkit.plugin.android

import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.applyPlugins
import org.gradle.api.Plugin
import org.gradle.api.Project

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
