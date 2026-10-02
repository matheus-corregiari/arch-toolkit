package br.com.arch.toolkit.sample.feature.github.ui.list.state

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextOverflow
import br.com.arch.toolkit.sample.core.extension.localized
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.design.component.containerRadiusM
import br.com.arch.toolkit.sample.design.component.containerRadiusXs
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.repository.model.RepoRO
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

@Composable
internal fun RepositoryList(
    items: List<RepoRO>,
    modifier: Modifier = Modifier,
    onSelect: (RepoRO) -> Unit,
    footer: @Composable () -> Unit = {}
) = LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(bottom = AppTheme.dimen.spacingXl),
    verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)
) {
    items(items, key = { it.id }) { item ->
        RepositoryRow(
            Modifier.fillMaxWidth().containerRadiusM().clickable { onSelect(item) },
            item
        )
    }
    item(key = "pagination") { footer() }
}

@Composable
private fun RepositoryRow(modifier: Modifier, item: RepoRO) = Column(
    modifier = modifier.padding(AppTheme.dimen.spacingM),
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
                style = AppTheme.textStyle.metadata,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    if (item.topics.isNotEmpty()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXxs),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXxs),
            content = { for (topic in item.topics) Tag(topic) }
        )
    }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingS),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs)
    ) {
        Badge(
            icon = Icons.Outlined.Star,
            text = item.stargazersCount.localized(LocalAppLanguage.current)
        )
        Badge(
            icon = Icons.Filled.Visibility,
            text = item.watchersCount.localized(LocalAppLanguage.current)
        )
        Badge(
            icon = Icons.Filled.ForkLeft,
            text = item.forksCount.localized(LocalAppLanguage.current)
        )
        Badge(
            icon = Icons.Filled.BugReport,
            text = item.openIssuesCount.localized(LocalAppLanguage.current)
        )
    }
    RepositoryDetails(item)
}

@Composable
private fun RepositoryDetails(item: RepoRO) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingS),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXxs)
    ) {
        Text(
            text = item.language.orEmpty(),
            style = AppTheme.textStyle.metadata
        )
        Spacer(Modifier.size(AppTheme.dimen.spacingXxs))
        Text(
            text = "${text(
                AppText.UPDATED
            )}: ${item.updatedAt.localized(LocalAppLanguage.current)}",
            style = AppTheme.textStyle.metadata
        )
    }
}

@Composable
private fun User(repoDTO: RepoRO) = Row(
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
        style = AppTheme.textStyle.sectionHeading
    )
}

@Composable
private fun LoadImage(modifier: Modifier, url: String) {
    val context = LocalPlatformContext.current
    val request = remember(context, url) {
        ImageRequest.Builder(context).data(url)
            .memoryCacheKey("$url-memory")
            .diskCacheKey("$url-disk").build()
    }
    AsyncImage(
        modifier = modifier,
        contentScale = ContentScale.FillBounds,
        model = request,
        contentDescription = null
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
        style = AppTheme.textStyle.metadata
    )
}

@Composable
private fun Tag(text: String) = Text(
    text = text,
    modifier = Modifier.containerRadiusXs()
        .padding(vertical = AppTheme.dimen.spacingXxs, horizontal = AppTheme.dimen.spacingXs),
    color = AppTheme.color.textLink,
    style = AppTheme.textStyle.metadata
)
