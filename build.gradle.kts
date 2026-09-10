plugins {
    id("jacoco")
    id("toolkit-ci")

    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.jetbrains.serialization) apply false
    alias(libs.plugins.dexcount) apply false
}

// Kover's generated artifact task reads Kotlin compiler outputs. Declare the relationship
// explicitly for Gradle's strict task validation (required by Gradle 9.6+).
subprojects {
    tasks.matching { it.name == "koverGenerateArtifact" }.configureEach {
        dependsOn(tasks.matching { it.name.startsWith("compile") && it.name.contains("Kotlin") })
    }

    tasks.matching { it.name == "jsBrowserProductionLibraryDistribution" }.configureEach {
        dependsOn(tasks.matching { it.name == "jsDevelopmentLibraryCompileSync" })
    }

    tasks.matching { it.name == "jsBrowserTest" }.configureEach {
        dependsOn(tasks.matching { it.name == "jsTestTestProductionExecutableCompileSync" })
    }

    tasks.matching { it.name == "wasmJsBrowserTest" }.configureEach {
        dependsOn(tasks.matching { it.name == "wasmJsTestTestProductionExecutableCompileSync" })
    }
}
