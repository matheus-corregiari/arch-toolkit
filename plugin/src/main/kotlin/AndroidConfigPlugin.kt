import org.gradle.api.Plugin
import org.gradle.api.Project

internal class AndroidConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) = Unit
}

object AndroidConfig {

    const val applicationId = "dev.tigrao.github"

    const val compileSdk = 31
    const val minSdk = 21
    const val targetSdk = compileSdk

    const val instrumentationTestRunner = "androidx.test.runner.AndroidJUnitRunner"
}
