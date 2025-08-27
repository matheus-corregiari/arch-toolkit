package br.com.arch.toolkit.sample.feature.githubList.ui.list.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ForkLeft
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import arch_toolkit.sample.shared.feature.github_list.generated.resources.Res
import arch_toolkit.sample.shared.feature.github_list.generated.resources.sample_github_list_last_updated
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.containerRadiusM
import br.com.arch.toolkit.sample.github.shared.designSystem.component.containerRadiusXs
import br.com.arch.toolkit.sample.github.shared.structure.core.extension.abbreviate
import br.com.arch.toolkit.sample.github.shared.structure.core.extension.formatAsString
import br.com.arch.toolkit.sample.feature.githubList.ui.list.model.RepoVO
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import org.jetbrains.compose.resources.stringResource

internal class ManyListState(
    private val list: List<RepoVO>,
    private val padding: PaddingValues
) : ListState() {
    @Composable
    override fun Draw(modifier: Modifier) = LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding(),
            start = padding.calculateStartPadding(LocalLayoutDirection.current) + AppTheme.dimen.spacingM,
            end = padding.calculateEndPadding(LocalLayoutDirection.current) + AppTheme.dimen.spacingM,
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)
    ) {
        items(list) { item -> Item(Modifier.fillMaxWidth(), item) }
        item { Spacer(Modifier.size(AppTheme.dimen.spacingG)) }
    }

    @Composable
    private fun Item(modifier: Modifier, item: RepoVO) = Column(
        modifier = modifier.containerRadiusM().padding(AppTheme.dimen.spacingM),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs)
        ) {
            User(item)
            item.description?.let {
                Text(
                    text = it,
                    color = AppTheme.color.textSubtitle,
                    style = AppTheme.textStyle.paragraphCaptionS,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (item.topics.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXxs),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXxs),
                content = { for (topic in item.topics) Tag(topic) },
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingS),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs),
        ) {
            Badge(
                icon = Icons.Outlined.Star,
                text = item.stargazersCount.abbreviate()
            )
            Badge(
                icon = Icons.Filled.Visibility,
                text = item.watchersCount.abbreviate()
            )
            Badge(
                icon = Icons.Filled.ForkLeft,
                text = item.forksCount.abbreviate()
            )
            Badge(
                icon = Icons.Filled.BugReport,
                text = item.openIssuesCount.abbreviate()
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.language,
                style = AppTheme.textStyle.paragraphCaptionXs
            )
            Spacer(Modifier.size(AppTheme.dimen.spacingXxs))
            Text(
                text = stringResource(
                    Res.string.sample_github_list_last_updated, item.updatedAt.formatAsString()
                ),
                style = AppTheme.textStyle.paragraphCaptionXs
            )
        }
    }

    @Composable
    private fun User(repoDTO: RepoVO) = Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoadImage(
            Modifier.clipToBounds().size(AppTheme.dimen.iconL).clip(CircleShape),
            repoDTO.owner.avatarUrl
        )
        Text(
            text = repoDTO.fullName,
            color = AppTheme.color.textLink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTheme.textStyle.subtitleXBold
        )
    }

    @Composable
    private fun LoadImage(modifier: Modifier, url: String) {
        val context = LocalPlatformContext.current
        val request = remember(url) {
            ImageRequest.Builder(context).data(url)
                .memoryCacheKey("$url-memory")
                .diskCacheKey("$url-disk").build()
        }
        AsyncImage(
            modifier = modifier,
            contentScale = ContentScale.FillBounds,
            model = request,
            contentDescription = null,
            onSuccess = { },
            onError = { }
        )
    }

    @Composable
    private fun Badge(icon: ImageVector, text: String) = Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXxxs)
    ) {
        Icon(
            modifier = Modifier.size(AppTheme.dimen.iconM),
            imageVector = icon,
            tint = AppTheme.color.iconPositive,
            contentDescription = null
        )
        Text(
            text = text,
            color = AppTheme.color.textSubtitle,
            style = AppTheme.textStyle.paragraphCaptionS
        )
    }

    @Composable
    private fun Tag(text: String) = Text(
        text = text,
        modifier = Modifier.containerRadiusXs()
            .padding(vertical = AppTheme.dimen.spacingXxs, horizontal = AppTheme.dimen.spacingXs),
        color = AppTheme.color.textLink,
        style = AppTheme.textStyle.paragraphCaptionXs
    )
}
