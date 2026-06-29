package com.toolkit.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

/**
 * Registers root CI lifecycle tasks used by GitHub Actions.
 *
 * This plugin only aggregates tasks exposed by modules. Library, lint, publication, target, and
 * sample-specific configuration stays in the dedicated Toolkit convention plugins or module build
 * files.
 */
internal class ToolkitCiPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val syncContributingDocs = target.tasks.register("syncContributingDocs", Copy::class.java) {
            it.description = "Syncs CONTRIBUTING.md into the MkDocs source tree."
            it.from(target.layout.projectDirectory.file("CONTRIBUTING.md"))
            it.into(target.layout.projectDirectory.dir("docs"))
            it.rename { "contributing.md" }
        }

        val ciLint = target.registerCiTask(
            name = "ciLint",
            description = "Runs lint checks for all modules that expose lint tasks.",
        )
        val ciDocs = target.registerCiTask(
            name = "ciDocs",
            description = "Generates API documentation inputs for the MkDocs site.",
        ) {
            it.dependsOn(syncContributingDocs)
        }
        val ciBuild = target.registerCiTask(
            name = "ciBuild",
            description = "Assembles all publishable modules.",
        )
        val ciTest = target.registerCiTask(
            name = "ciTest",
            description = "Runs all supported test tasks.",
        )
        val ciCoverage = target.registerCiTask(
            name = "ciCoverage",
            description = "Runs tests and verifies merged coverage.",
        ) {
            it.dependsOn(ciTest)
        }
        val ciPublishMavenCentral = target.registerCiTask(
            name = "ciPublishMavenCentral",
            description = "Publishes all publishable modules to Maven Central.",
        )
        val ciPublishGithubPackages = target.registerCiTask(
            name = "ciPublishGithubPackages",
            description = "Publishes all publishable modules to GitHub Packages.",
        )
        val ciPublishLocal = target.registerCiTask(
            name = "ciPublishLocal",
            description = "Publishes all publishable modules to the local Maven repository.",
        )

        target.gradle.projectsEvaluated {
            val lintedProjects = target.subprojects.filter {
                it.plugins.hasPlugin("toolkit-lint")
            }
            val publishableProjects = target.subprojects.filter {
                it.plugins.hasPlugin("toolkit-android-publish") ||
                    it.plugins.hasPlugin("toolkit-multiplatform-publish")
            }
            val testedProjects = target.subprojects.filter {
                it.plugins.hasPlugin("toolkit-test")
            }

            ciLint.configure {
                it.dependsOn(lintedProjects.mapNotNull { project -> project.taskPath("detekt") })
                it.dependsOn(lintedProjects.mapNotNull { project -> project.taskPath("ktlintCheck") })
            }
            ciDocs.configure {
                it.dependsOn(publishableProjects.mapNotNull { project -> project.taskPath("dokkaGenerate") })
                it.dependsOn(publishableProjects.mapNotNull { project -> project.taskPath("dokkaGeneratePublicationHtml") })
            }
            ciBuild.configure {
                it.dependsOn(publishableProjects.mapNotNull { project -> project.taskPath("assemble") })
            }
            ciTest.configure {
                it.dependsOn(testedProjects.mapNotNull { project -> project.taskPath("allTests") })
                it.dependsOn(testedProjects.mapNotNull { project -> project.taskPath("test") })
            }
            ciCoverage.configure {
                it.dependsOn(
                    listOfNotNull(
                        target.taskPath("koverXmlReport"),
                        target.taskPath("koverHtmlReport"),
                        target.taskPath("koverVerify"),
                    ),
                )
            }
            ciPublishMavenCentral.configure {
                it.dependsOn(
                    publishableProjects.mapNotNull { project ->
                        project.taskPath("publishAndReleaseToMavenCentral")
                    },
                )
            }
            ciPublishGithubPackages.configure {
                it.dependsOn(
                    publishableProjects.mapNotNull { project ->
                        project.taskPath("publishAllPublicationsToGithubRepository")
                    },
                )
            }
            ciPublishLocal.configure {
                it.dependsOn(publishableProjects.mapNotNull { project -> project.taskPath("publishToMavenLocal") })
            }
        }
    }

    private fun Project.registerCiTask(
        name: String,
        description: String,
        configure: (org.gradle.api.Task) -> Unit = {},
    ) = tasks.register(name) {
        it.group = "CI"
        it.description = description
        configure(it)
    }

    private fun Project.taskPath(name: String): String? = tasks.findByName(name)?.path
}
