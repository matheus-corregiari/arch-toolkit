package br.com.arch.toolkit.sample.feature.githubList.ui.list.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import arch_toolkit.sample.shared.feature.github_list.generated.resources.Res
import arch_toolkit.sample.shared.feature.github_list.generated.resources.sample_github_list_empty_title
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.EmptyState
import br.com.arch.toolkit.sample.github.shared.designSystem.component.containerRadiusM
import org.jetbrains.compose.resources.stringResource

internal data object EmptyListState : ListState() {
    @Composable
    override fun Draw(modifier: Modifier) = Box(
        modifier = modifier.padding(horizontal = AppTheme.dimen.spacingM),
        contentAlignment = Alignment.Center
    ) {
        EmptyState(
            modifier = Modifier.padding(
                vertical = AppTheme.dimen.spacingG
            ).containerRadiusM().padding(AppTheme.dimen.spacingM),
            title = stringResource(Res.string.sample_github_list_empty_title)
        )
    }
}
