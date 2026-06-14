package com.toolkit.plugin.util

import org.gradle.api.artifacts.VersionCatalog
import kotlin.jvm.optionals.getOrNull

internal fun VersionCatalog.version(alias: String) =
    findVersion(alias).getOrNull()?.requiredVersion
        ?: error("Unable to find version with alias: $alias")

internal val VersionCatalog.allDefinedDependencies: Set<String>
    get() = libraryAliases
        .asSequence()
        .map(::findLibrary)
        .mapNotNull {
            it.getOrNull()?.get()
                ?.run { "${module.group}:${module.name}:${versionConstraint.requiredVersion}:" }
        }.toSet()
