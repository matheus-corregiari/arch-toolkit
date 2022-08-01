import com.android.build.api.dsl.BuildFeatures
import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

internal class AndroidProjectConfig {

    fun config(
        target: Project,
        buildBaseExtensionBlock: (BaseExtension.() -> Unit)?,
        buildFeaturesBlock: (BuildFeatures) -> Unit
    ) {
        target.getAndroidExtension()?.apply {
            compileSdkVersion(AndroidConfig.compileSdk)
            defaultConfig {
                targetSdkVersion(AndroidConfig.targetSdk)
                minSdkVersion(AndroidConfig.minSdk)

                testInstrumentationRunner = AndroidConfig.instrumentationTestRunner

                buildFeaturesBlock(buildFeatures)

                configureCompileOptions(buildBaseExtensionBlock)

                sourceSets {
                    this["main"].java {
                        this.srcDir("src/main/kotlin")
                    }
                    this["test"].java {
                        this.srcDir("src/test/kotlin")
                    }
                }
            }
        }

        configureKotlinOptions(target)
    }

    private fun BaseExtension.configureCompileOptions(block: ((BaseExtension) -> Unit)?) {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        block?.invoke(this)
    }

    private fun Project.getAndroidExtension() = extensions.findByType(BaseExtension::class.java)
}
