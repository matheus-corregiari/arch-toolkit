package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppPage
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.design.component.GithubMark
import br.com.arch.toolkit.sample.design.text
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
enum class ToolkitLibrary(
    val title: String,
    val repository: String,
    val description: AppText,
    val usage: AppText
) {
    SPLINTER(
        "Arch Toolkit",
        "arch-toolkit",
        AppText.TOOLKIT_REPOSITORY_DESCRIPTION,
        AppText.TOOLKIT_REPOSITORY_USAGE
    ),
    ANDROID(
        "Arch Android",
        "arch-android",
        AppText.ANDROID_REPOSITORY_DESCRIPTION,
        AppText.ANDROID_REPOSITORY_USAGE
    ),
    OBSERVER(
        "Event Observer",
        "arch-event-observer",
        AppText.OBSERVER_REPOSITORY_DESCRIPTION,
        AppText.OBSERVER_REPOSITORY_USAGE
    ),
    LUMBER(
        "Lumber",
        "arch-lumber",
        AppText.LUMBER_REPOSITORY_DESCRIPTION,
        AppText.LUMBER_REPOSITORY_USAGE
    ),
    STORAGE(
        "Storage",
        "arch-storage",
        AppText.STORAGE_REPOSITORY_DESCRIPTION,
        AppText.STORAGE_REPOSITORY_USAGE
    );

    val githubUrl: String get() = "https://github.com/matheus-corregiari/$repository"
}

/** Five small cards share one page scroll, including the heading. */
@Composable
fun EcosystemContent(
    onOpenRepository: (String) -> Unit = LocalUriHandler.current::openUri,
    onOpenSample: (ToolkitLibrary) -> Unit = {}
) {
    AppPage(text(AppText.TOOLKIT), description = text(AppText.ECOSYSTEM_INTRO), scrollable = true) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val gap = AppTheme.dimen.spacingXl
            val minimum = AppTheme.dimen.sectionMinWidth * max(1f, LocalDensity.current.fontScale)
            val columns = if (maxWidth >= minimum * 2 + gap) 2 else 1
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap),
                verticalArrangement = Arrangement.spacedBy(gap),
                maxItemsInEachRow = columns
            ) {
                ToolkitLibrary.entries.forEach { library ->
                    AppSection(library.title, Modifier.weight(1f), text(library.description)) {
                        Text(text(library.usage), style = AppTheme.textStyle.body)
                        val sampleLabel = text(AppText.OPEN_SAMPLE)
                        AppButton(
                            sampleLabel,
                            { onOpenSample(library) },
                            modifier = Modifier.semantics {
                                contentDescription = "$sampleLabel: ${library.title}"
                            }
                        )
                        val githubLabel = text(AppText.OPEN_GITHUB)
                        AppButton(
                            githubLabel,
                            { onOpenRepository(library.githubUrl) },
                            modifier = Modifier.semantics {
                                contentDescription = "$githubLabel: ${library.title}"
                            },
                            style = AppButton.Style.Secondary,
                            leadingIcon = GithubMark
                        )
                    }
                }
            }
        }
    }
}
