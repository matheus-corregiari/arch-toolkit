package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.android.setupAndroidLibraryModule
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.multiplatform
import com.toolkit.plugin.util.projectJavaVersionCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

internal class ToolkitSamplePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-library")
        target.setupAndroidLibraryModule()

        target.applyPlugins("jetbrains-multiplatform")
        target.kotlinExtension.jvmToolchain(projectJavaVersionCode)

        with(target.multiplatform) {
            androidTarget {}
            jvm {}
        }

        target.plugins.apply("toolkit-optimize")
    }
}
