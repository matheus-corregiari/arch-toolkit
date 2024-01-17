package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.setupAndroidApplicationModule
import com.toolkit.plugin.util.applyPlugins
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitSamplePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-application")
        target.plugins.apply("toolkit-multiplatform-base")

        target.setupAndroidApplicationModule()

        target.plugins.apply("toolkit-lint")
        target.plugins.apply("toolkit-test")
        target.plugins.apply("toolkit-optimize")
    }
}
