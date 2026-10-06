import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.type.ArtifactTypeDefinition

val verifyShowcaseBoundaries = tasks.register("verifyShowcaseBoundaries") {
    group = "verification"
    description = "Checks the showcase project dependency graph and layer boundaries."
    doLast {
        val samples = rootProject.subprojects.filter { it.path.startsWith(":sample:") }
        val graph = samples.associate { project ->
            project.path to project.configurations.flatMap { configuration ->
                configuration.dependencies.withType<ProjectDependency>().map { it.path }
            }.filter { it != project.path }.toSet()
        }
        graph.forEach { (from, dependencies) ->
            dependencies.filter { it.startsWith(":sample:") }.forEach { to ->
                val allowed = when {
                    from.startsWith(":sample:target:") -> to == ":sample:shared:app" ||
                        rootProject.project(from).configurations.filter { configuration ->
                            configuration.dependencies.withType<ProjectDependency>().any { it.path == to }
                        }.all { it.name.contains("screenshotTest", ignoreCase = true) } &&
                        (to.startsWith(":sample:shared:features:") || to == ":sample:shared:data:repository")
                    from == ":sample:shared:app" -> true
                    from.startsWith(":sample:shared:features:") ->
                        to == ":sample:shared:data:repository" || to.startsWith(":sample:shared:structure:design:") ||
                            to == ":sample:shared:structure:core"
                    from == ":sample:shared:data:repository" ->
                        to.startsWith(":sample:shared:data:source:") || to == ":sample:shared:structure:core"
                    from.endsWith(":design:widget") -> to.endsWith(":design:core") || to == ":sample:shared:structure:core"
                    from.endsWith(":design:core") -> to == ":sample:shared:structure:core"
                    else -> false
                }
                check(allowed) { "Forbidden showcase dependency: $from -> $to" }
            }
        }
        fun visit(node: String, ancestors: Set<String>) {
            check(node !in ancestors) { "Showcase dependency cycle: $ancestors -> $node" }
            graph[node].orEmpty().forEach { visit(it, ancestors + node) }
        }
        graph.keys.forEach { visit(it, emptySet()) }
    }
}
tasks.named("ciLint") { dependsOn(verifyShowcaseBoundaries) }

subprojects {
    if (path.startsWith(":sample:shared:features:")) {
        configurations.matching {
            it.name == "debugCompileClasspath" || it.name == "releaseCompileClasspath"
        }.configureEach {
            // Navigation's generator reads this classpath directly; select classes, not AGP lint/manifests.
            attributes.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, "android-classes-jar")
        }
        val navigationGeneration = tasks.matching { it.name == "generateEasyNavigation" }
        navigationGeneration.configureEach {
            // These generated Kotlin roots are inputs to Navigation's source scanner.
            dependsOn(tasks.matching {
                it.name == "generateComposeResClass" || it.name.startsWith("generateResourceAccessors") ||
                    it.name.startsWith("generateExpectResourceCollectors") ||
                    it.name.startsWith("generateActualResourceCollectors")
            })
        }
        tasks.matching { it.name.startsWith("runKtlint") }.configureEach {
            mustRunAfter(navigationGeneration)
        }
    }
    // Navigation 1.2.0 still calls Lumber's Oak-returning tag ABI (changed in Lumber 1.2).
    // Scope the overrides to samples; published library dependencies retain their catalog versions.
    if (path.startsWith(":sample:")) {
        configurations.configureEach {
            resolutionStrategy.eachDependency {
                // Navigation's adaptive adapter calls V2 APIs missing from Adaptive 1.2.
                // Peer modules must use the same published Multiplatform release.
                if (requested.group == "org.jetbrains.compose.material3.adaptive") {
                    useVersion("1.3.0-rc01")
                    because("Align Adaptive peers required by Easy Navigation's Navigation3 adapter")
                }
                if (requested.group == "io.github.matheus-corregiari" &&
                    requested.name.startsWith("arch-lumber")) {
                    useVersion("1.1.0")
                    because("Easy Navigation 1.2.0 requires the Lumber tag ABI before 1.2")
                }
                if (requested.group == "io.github.matheus-corregiari" &&
                    requested.name.startsWith("storage-")) {
                    useVersion("2.0.0-rc16")
                    because("Storage 1.0.1 requires Lumber 1.4; rc16 shares Navigation's Lumber 1.1 ABI")
                }
            }
        }
    }
}
