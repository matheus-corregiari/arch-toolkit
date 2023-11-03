package com.toolkit.plugin

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

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

        with(target.kotlinExtension) {
            jvmToolchain(17)
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
            }

            buildFeatures.buildConfig = true

            aaptOptions {
                it.cruncherEnabled = false
            }

            compileOptions {
                it.sourceCompatibility(JavaVersion.VERSION_17)
                it.targetCompatibility(JavaVersion.VERSION_17)
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

        target.plugins.apply("toolkit-lint")
        target.plugins.apply("toolkit-test")

        val component = kotlin.runCatching { target.libraryComponent }.getOrNull()
            ?: kotlin.runCatching { target.applicationComponent }.getOrNull()
        component?.finalizeDsl {
            target.tasks.configureEach { task ->
                val isRelease = task.name.contains("release", true)
                val isLint = task.name.contains("lint", true)
                val isTest = task.name.contains("test", true)
                val isKover = task.name.contains("kover", true)
                val mustDisable = isLint || isTest || isKover
                if (isRelease && mustDisable) {
                    task.enabled = false
                    task.group = "z-disabled"
                }
            }
        }
    }
}
