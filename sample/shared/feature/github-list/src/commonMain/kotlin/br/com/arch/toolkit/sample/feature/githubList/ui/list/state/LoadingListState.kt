package br.com.arch.toolkit.sample.feature.githubList.ui.list.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.ShimmerRoundedM

internal data object LoadingListState : ListState() {
    @Composable
    override fun Draw(modifier: Modifier) = Column(
        modifier = modifier
            .padding(horizontal = AppTheme.dimen.spacingM)
            .padding(bottom = AppTheme.dimen.spacingG),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)
    ) {
        repeat(6) {
            ShimmerRoundedM(Modifier.fillMaxWidth().height(AppTheme.dimen.spacingG))
        }
    }
}
