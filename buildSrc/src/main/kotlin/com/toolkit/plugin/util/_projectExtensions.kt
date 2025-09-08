package com.toolkit.plugin.util

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
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

internal val Project.vanniktechPublish: MavenPublishBaseExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(MavenPublishBaseExtension::class.java)
        ?: error("Project do not implement vanniktech-publish plugin!")

internal fun Project.applyPlugins(vararg id: String) =
    id.map { libs.findPlugin(it).get().get().pluginId }
        .forEach { if (plugins.hasPlugin(it).not()) plugins.apply(it) }
