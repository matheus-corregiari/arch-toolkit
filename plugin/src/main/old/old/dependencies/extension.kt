package old.dependencies

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.dsl.DependencyHandler

internal fun DependencyHandler.implementation(any: Any) {
    add("implementation", any)
}

internal fun DependencyHandler.testImplementation(any: Any) {
    add("testImplementation", any)
}

internal fun DependencyHandler.kapt(any: Any) {
    add("kapt", any)
}

internal fun DependencyHandler.testKapt(any: Any) {
    add("testKapt", any)
}

internal fun VersionCatalog.getKotlin() = findLibrary("kotlinStdlib")
