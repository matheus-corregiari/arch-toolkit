package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.setupAndroidLibraryModule
import com.toolkit.plugin.util.applyPlugins
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-library")
        target.setupAndroidLibraryModule()

        target.plugins.apply("toolkit-multiplatform-base")
        target.plugins.apply("toolkit-lint")
        target.plugins.apply("toolkit-test")
    }
}
