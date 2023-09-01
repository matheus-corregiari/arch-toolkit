package com.toolkit.plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import kotlin.jvm.Throws
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

internal val Project.libraries: VersionCatalog
    @Throws(IllegalStateException::class)
    get() {
        return extensions.findByType(VersionCatalogsExtension::class.java)?.named("libraries")
            ?: error("Cannot find libraries in version catalog!")
    }

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

internal fun Project.applyPlugins(vararg id: String) {
    id.forEach {
        plugins.apply(libraries.findPlugin(it).get().get().pluginId)
    }
}
