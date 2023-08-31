package old

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun configureKotlinOptions(target: Project) {
    with(target) {
        tasks.withType(KotlinCompile::class.java).configureEach {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
}
