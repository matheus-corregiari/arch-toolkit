package com.toolkit.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("jetbrains-kotlin")

        val libraries = target.libraries
        with(target.androidBase) {

            compileSdkVersion = libraries.findVersion("build-sdk-compile").get().strictVersion
            buildToolsVersion = libraries.findVersion("build-tools").get().strictVersion

            defaultConfig {
                it.minSdkVersion(libraries.findVersion("build-sdk-min").get().strictVersion.toInt())
                it.targetSdkVersion(
                    libraries.findVersion("build-sdk-target").get().strictVersion.toInt()
                )

                it.versionCode =
                    libraries.findVersion("build-version-code").get().strictVersion.toInt()
                it.versionName = libraries.findVersion("build-version-name").get().strictVersion

                it.resConfigs("en")

                it.testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
            }

            buildTypes.maybeCreate("debug").apply {
                enableAndroidTestCoverage = false
                enableUnitTestCoverage = false
            }
        }
    }

}
