package com.toolkit.plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

internal val Project.libraries: VersionCatalog
    @Throws(IllegalStateException::class)
    get() {
        return extensions.findByType(VersionCatalogsExtension::class.java)?.named("libraries")
            ?: error("Cannot find libraries in version catalog!")
    }

internal val Project.ktLint: KtlintExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(KtlintExtension::class.java)
        ?: error("Project do not implement ktlint plugin!")

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

internal val Project.koverReport: KoverReportExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(KoverReportExtension::class.java)
        ?: error("Project do not implement kover plugin!")

internal val Project.androidLibrary: LibraryExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(LibraryExtension::class.java)
        ?: error("Project do not implement android-library plugin!")

internal val Project.androidApplication: ApplicationExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(ApplicationExtension::class.java)
        ?: error("Project do not implement android-application plugin!")

internal val Project.androidBase: BaseExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(BaseExtension::class.java)
        ?: error("Project do not implement android-library neither android-application plugin!")

internal val Project.libraryComponent: LibraryAndroidComponentsExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(LibraryAndroidComponentsExtension::class.java)
        ?: error("Project do not implement android-library neither android-application plugin!")

internal val Project.applicationComponent: ApplicationAndroidComponentsExtension
    @Throws(IllegalStateException::class)
    get() = extensions.findByType(ApplicationAndroidComponentsExtension::class.java)
        ?: error("Project do not implement android-library neither android-application plugin!")

internal fun Project.applyPlugins(vararg id: String) {
    id.forEach {
        plugins.apply(libraries.findPlugin(it).get().get().pluginId)
    }
}
