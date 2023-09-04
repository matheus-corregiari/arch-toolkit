package com.toolkit.plugin

import kotlin.jvm.optionals.getOrNull
import org.gradle.api.artifacts.VersionCatalog

internal fun VersionCatalog.version(alias: String) =
    findVersion("build-sdk-compile").getOrNull()?.requiredVersion
        ?: error("Unable to find version with alias: $alias")