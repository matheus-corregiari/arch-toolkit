package old.dependencies

import NeededDependencies
import old.getCatalog
import org.gradle.api.Project

internal class KotlinDependencies : NeededDependencies {
    override fun addDependencies(project: Project) {
        val kotlin = project.getCatalog().getKotlin().get()
        project.dependencies.implementation(kotlin.get())
    }
}
