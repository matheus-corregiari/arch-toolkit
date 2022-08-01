import dependencies.KotlinDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinModulePlugin : Plugin<Project>, NeededDependencies by KotlinDependencies() {
    override fun apply(target: Project) {
        target.plugins.apply("kotlin")

        configureKotlinOptions(target)
        addDependencies(target)
    }
}
