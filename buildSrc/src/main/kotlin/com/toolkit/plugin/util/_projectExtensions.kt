package com.toolkit.plugin.util

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.gradle.plugins.signing.SigningExtension
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

internal val Project.libs: VersionCatalog
    @Throws(IllegalStateException::class)
    get() {
        return extensions.findByType(VersionCatalogsExtension::class.java)?.named("libs")
            ?: error("Cannot find libraries in version catalog!")
    }

internal val Project.ktLint: KtlintExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(KtlintExtension::class.java)
        ?: error("Project do not implement ktlint plugin!")

internal val Project.multiplatform: KotlinMultiplatformExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(KotlinMultiplatformExtension::class.java)
        ?: error("Project do not implement kotlin-multiplatform plugin!")

internal val Project.detekt: DetektExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(DetektExtension::class.java)
        ?: error("Project do not implement detekt plugin!")

internal val Project.jacoco: JacocoPluginExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(JacocoPluginExtension::class.java)
        ?: error("Project do not implement jacoco plugin!")

internal val Project.kover: KoverProjectExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(KoverProjectExtension::class.java)
        ?: error("Project do not implement kover plugin!")

internal val Project.androidLibrary: LibraryExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(LibraryExtension::class.java)
        ?: error("Project do not implement android-library plugin!")

internal val Project.androidApplication: ApplicationExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(ApplicationExtension::class.java)
        ?: error("Project do not implement android-application plugin!")

internal val Project.android: BaseExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(BaseExtension::class.java)
        ?: error("Project do not implement android plugin!")

internal val Project.libraryComponent: LibraryAndroidComponentsExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(LibraryAndroidComponentsExtension::class.java)
        ?: error("Project do not implement android-library plugin!")

internal val Project.applicationComponent: ApplicationAndroidComponentsExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(ApplicationAndroidComponentsExtension::class.java)
        ?: error("Project do not implement android-application plugin!")

internal val Project.publishing: PublishingExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(PublishingExtension::class.java)
        ?: error("Project do not implement maven-publish plugin!")

internal val Project.sign: SigningExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(SigningExtension::class.java)
        ?: error("Project do not implement signing plugin!")

internal fun Project.applyPlugins(vararg id: String) =
    id.forEach { plugins.apply(libs.findPlugin(it).get().get().pluginId) }

internal fun Project.hasPlugins(vararg id: String) =
    id.all { plugins.hasPlugin(libs.findPlugin(it).get().get().pluginId) }

internal fun Project.attachAllTasksIntoAssembleRelease() = afterEvaluate { project ->
    val all = project.tasks.filter { task ->
        when (task) {
            is Jar, is Javadoc -> when {
                task.name.contains("debug", true) -> false
                else -> true
            }

            else -> false
        }
    }.map { project.tasks.named(it.name) }
    project.tasks.findByName("assembleRelease")?.dependsOn(all)
}
