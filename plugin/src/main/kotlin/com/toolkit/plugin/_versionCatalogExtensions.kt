package com.toolkit.plugin

import kotlin.jvm.optionals.getOrNull
import org.gradle.api.artifacts.VersionCatalog

internal fun VersionCatalog.version(alias: String) =
    findVersion(alias).getOrNull()?.requiredVersion
        ?: error("Unable to find version with alias: $alias")

internal val VersionCatalog.allDefinedDependencies: ArrayList<String>
    get() = arrayListOf(
        *libraryAliases.map(::findLibrary).mapNotNull {
            it.getOrNull()?.get()?.run {
                "${module.group}:${module.name}:${versionConstraint.requiredVersion}:"
            }
        }.toTypedArray()
    )
