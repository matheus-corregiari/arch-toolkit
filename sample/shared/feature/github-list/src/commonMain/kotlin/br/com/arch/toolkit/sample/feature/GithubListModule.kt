@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.feature

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.outlined.Code
import arch_toolkit.sample.shared.feature.github_list.generated.resources.Res
import arch_toolkit.sample.shared.feature.github_list.generated.resources.sample_github_list_description
import arch_toolkit.sample.shared.feature.github_list.generated.resources.sample_github_list_title
import br.com.arch.toolkit.sample.feature.githubList.ui.list.ListViewModel
import br.com.arch.toolkit.sample.feature.githubList.ui.list.RepositoryListScreen
import br.com.arch.toolkit.sample.github.shared.structure.core.ComposeContent
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureIcon
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureRegistry
import br.com.arch.toolkit.sample.github.shared.structure.core.featureRegistry
import br.com.arch.toolkit.sample.github.shared.structure.core.savedStateHandleCompat
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object GithubListModule {
    val module = module {
        // ViewModels
        viewModel { ListViewModel(get(), savedStateHandleCompat("list-view-model")) }

        // Features
        featureRegistry("repository-list-home") { repositoryList() }
    }

    private fun repositoryList(): List<FeatureRegistry> = listOf(
        FeatureRegistry(
            id = "sample_github_list",
            version = 1,
            title = Res.string.sample_github_list_title,
            description = Res.string.sample_github_list_description,
            icon = FeatureIcon(
                selected = Icons.Filled.Code,
                unselected = Icons.Outlined.Code
            ),
            content = listOf(ComposeContent { RepositoryListScreen() })
        )
    )

}
