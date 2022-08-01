import org.gradle.api.Project

internal interface NeededDependencies {

    fun addDependencies(project: Project)
}
