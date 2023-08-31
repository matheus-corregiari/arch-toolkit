package old

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal const val COMPOSE_CATALOG_NAME = "compose"

internal fun Project.getCatalog() = extensions.getByType<VersionCatalogsExtension>()
    .named("libs")
