package com.toolkit.plugin

import java.io.File
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("jetbrains-kotlin")

        val libraries = target.libraries
        val allDefinedLibraries = libraries.allDefinedDependencies

        target.configurations.configureEach { config ->
            config.resolutionStrategy { strategy ->
                strategy.failOnVersionConflict()
                strategy.preferProjectModules()

                strategy.setForcedModules(allDefinedLibraries)
            }
        }

        with(target.androidBase) {

            compileSdkVersion(libraries.version("build-sdk-compile").toInt())
            buildToolsVersion = libraries.version("build-tools")

            defaultConfig {
                it.minSdkVersion(libraries.version("build-sdk-min").toInt())
                it.targetSdkVersion(libraries.version("build-sdk-target").toInt())

                it.versionCode = libraries.version("build-version-code").toInt()
                it.versionName = libraries.version("build-version-name")

                it.resConfigs("en")

                it.testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
            }

            buildFeatures.buildConfig = true

            buildTypes.maybeCreate("debug").apply {
                enableAndroidTestCoverage = false
                enableUnitTestCoverage = false
            }

            aaptOptions {
                it.cruncherEnabled = false
            }

            compileOptions {
                it.sourceCompatibility(JavaVersion.VERSION_17)
                it.targetCompatibility(JavaVersion.VERSION_17)
            }

            lintOptions {
                it.isCheckReleaseBuilds = true

                it.isAbortOnError = true
                it.isIgnoreWarnings = false
                it.isAbsolutePaths = false
                it.isWarningsAsErrors = false

                it.htmlOutput =
                    File("${target.rootDir}/build/reports/lint/html/${target.name}-lint.html")
                it.xmlOutput =
                    File("${target.rootDir}/build/reports/lint/xml/${target.name}-lint.xml")
            }

            testOptions {
                it.unitTests.isIncludeAndroidResources = true
                it.unitTests.isReturnDefaultValues = true
                it.animationsDisabled = true
            }

            packagingOptions {
                it.exclude("META-INF/LICENSE")
                it.pickFirst("protobuf.meta")
                it.setDoNotStrip(setOf("*/mips/*.so", "*/mips64/*.so"))
            }

            sourceSets {
                it.maybeCreate("main").java.srcDirs("src/main/kotlin")
                it.maybeCreate("test").java.srcDirs("src/test/kotlin")
                it.maybeCreate("androidTest").java.srcDirs("src/androidTest/kotlin")
                it.maybeCreate("androidTest").resources.srcDirs("src/androidTest/res")
            }

        }
    }

}
