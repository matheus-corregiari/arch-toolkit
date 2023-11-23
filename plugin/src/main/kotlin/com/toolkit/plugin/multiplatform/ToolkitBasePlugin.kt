@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.multiplatform
import com.toolkit.plugin.util.projectJavaVersionCode
import com.toolkit.plugin.util.projectJavaVersionName
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal class ToolkitBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("jetbrains-multiplatform")
        with(target.kotlinExtension) {
            jvmToolchain(projectJavaVersionCode)
        }

        with(target.multiplatform) {
            androidTarget {
                compilations.all { compilation ->
                    compilation.kotlinOptions {
                        jvmTarget = projectJavaVersionName
                    }
                }
            }
        }

        target.plugins.apply("toolkit-optimize")
    }
}

val NamedDomainObjectContainer<KotlinSourceSet>.androidMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidMain")

val NamedDomainObjectContainer<KotlinSourceSet>.androidUnitTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidUnitTest")

val NamedDomainObjectContainer<KotlinSourceSet>.androidInstrumentedTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidInstrumentedTest")
