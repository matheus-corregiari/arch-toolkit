plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.jetbrains.compose.compiler) apply false
    alias(libs.plugins.jetbrains.compose.kotlin) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.jetbrains.multiplatform) apply false
    alias(libs.plugins.jetbrains.serialization) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.dexcount) apply false
    alias(libs.plugins.jetbrains.kover)
}

dependencies {
    subprojects
        .filter { it.path.startsWith(":toolkit:multi:") && it.name != "test" }
        .forEach { add("kover", project(it.path)) }
}

val syncContributingDocs by tasks.registering(Copy::class) {
    description = "Syncs CONTRIBUTING.md into the MkDocs source tree."
    from(layout.projectDirectory.file("CONTRIBUTING.md"))
    into(layout.projectDirectory.dir("docs"))
    rename { "contributing.md" }
}

val ciLint by tasks.registering {
    group = "CI"
    description = "Runs Detekt, ktlint, and Android lint for library modules."
}

val ciBuild by tasks.registering {
    group = "CI"
    description = "Assembles all publishable library modules."
}

val ciTest by tasks.registering {
    group = "CI"
    description = "Runs all supported library tests."
}

val ciCoverage by tasks.registering {
    group = "CI"
    description = "Generates merged coverage reports without enforcing a threshold."
    dependsOn(ciTest, "koverXmlReport", "koverHtmlReport")
}

val ciDocs by tasks.registering {
    group = "CI"
    description = "Generates API documentation for all publishable modules."
    dependsOn(syncContributingDocs)
}

val ciSamples by tasks.registering {
    group = "CI"
    description = "Assembles sample applications when -PincludeSamples=true is set."
}

val ciPublishMavenCentral by tasks.registering {
    group = "CI"
    description = "Publishes all publishable modules to Maven Central."
}

val ciPublishLocal by tasks.registering {
    group = "CI"
    description = "Publishes all publishable modules to Maven local."
}

gradle.projectsEvaluated {
    val libraries = subprojects.filter { it.path.startsWith(":toolkit:multi:") }
    val publishable = libraries.filter { it.plugins.hasPlugin("com.vanniktech.maven.publish") }
    val samples = subprojects.filter { it.path.startsWith(":sample:") }

    fun Project.taskPath(name: String): String? = tasks.findByName(name)?.path

    ciLint.configure {
        dependsOn(libraries.mapNotNull { it.taskPath("detekt") })
        dependsOn(libraries.mapNotNull { it.taskPath("ktlintCheck") })
        dependsOn(libraries.mapNotNull { it.taskPath("lint") })
    }
    ciBuild.configure {
        dependsOn(publishable.mapNotNull { it.taskPath("assemble") })
    }
    ciTest.configure {
        dependsOn(libraries.mapNotNull { it.taskPath("allTests") })
    }
    ciDocs.configure {
        dependsOn(publishable.mapNotNull { it.taskPath("dokkaGeneratePublicationHtml") })
    }
    ciSamples.configure {
        dependsOn(samples.mapNotNull { it.taskPath("assemble") })
    }
    ciPublishMavenCentral.configure {
        dependsOn(publishable.mapNotNull { it.taskPath("publishAndReleaseToMavenCentral") })
    }
    ciPublishLocal.configure {
        dependsOn(publishable.mapNotNull { it.taskPath("publishToMavenLocal") })
    }
}
