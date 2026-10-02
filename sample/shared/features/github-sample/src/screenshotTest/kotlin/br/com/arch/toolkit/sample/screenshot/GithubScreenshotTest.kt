package br.com.arch.toolkit.sample.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.arch.toolkit.sample.feature.githubSample.ui.detail.GithubDetailState
import br.com.arch.toolkit.sample.feature.githubSample.ui.detail.RepositoryDetailContent
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.GithubListState
import br.com.arch.toolkit.sample.feature.githubSample.ui.list.RepositoryListContent
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubFailure
import br.com.arch.toolkit.sample.github.shared.structure.repository.RecentRepositoryRO
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.RepoRO
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.UserRO
import br.com.arch.toolkit.sample.screenshot.ScreenshotEnvironment
import com.android.tools.screenshot.PreviewTest
import kotlinx.datetime.LocalDateTime

@PreviewTest
@Preview(
    name = "GithubListLoading",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListLoading() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(GithubListState(loading = true))
    }
}

@PreviewTest
@Preview(
    name = "GithubListEmpty",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListEmpty() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(GithubListState(query = "no matching repositories", nextPage = null))
    }
}

@PreviewTest
@Preview(
    name = "GithubListContent",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListContent() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(
            listFixture(),
            history = listOf(RecentRepositoryRO("sample", "arch-toolkit"))
        )
    }
}

@PreviewTest
@Preview(
    name = "GithubListPaginationLoading",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListPaginationLoading() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(listFixture().copy(loading = true))
    }
}

@PreviewTest
@Preview(
    name = "GithubListPaginationError",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListPaginationError() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(listFixture().copy(failure = GithubFailure.CONNECTION))
    }
}

@PreviewTest
@Preview(
    name = "GithubListLastPage",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListLastPage() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(listFixture().copy(nextPage = null))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailLoading",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubDetailLoading() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(loading = true))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailContent",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubDetailContent() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(item = repoFixture()))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailMissingMetadata",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubDetailMissingMetadata() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(item = repoFixture(missingMetadata = true)))
    }
}

@PreviewTest
@Preview(
    name = "GithubListConnection",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListConnection() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(GithubListState(failure = GithubFailure.CONNECTION))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailConnection",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubDetailConnection() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(failure = GithubFailure.CONNECTION))
    }
}

@PreviewTest
@Preview(
    name = "GithubListRateLimit",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListRateLimit() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(GithubListState(failure = GithubFailure.RATE_LIMIT))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailRateLimit",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubDetailRateLimit() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(failure = GithubFailure.RATE_LIMIT))
    }
}

@PreviewTest
@Preview(
    name = "GithubListNotFound",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListNotFound() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(GithubListState(failure = GithubFailure.NOT_FOUND))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailNotFound",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubDetailNotFound() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(failure = GithubFailure.NOT_FOUND))
    }
}

@PreviewTest
@Preview(
    name = "GithubListInvalidResponse",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListInvalidResponse() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(GithubListState(failure = GithubFailure.INVALID_RESPONSE))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailInvalidResponse",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubDetailInvalidResponse() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(failure = GithubFailure.INVALID_RESPONSE))
    }
}

@PreviewTest
@Preview(
    name = "GithubListDarkPortuguese",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun GithubListDarkPortuguese() {
    ScreenshotEnvironment(dark = true, portuguese = true, highContrast = false) {
        RepositoryListContent(listFixture())
    }
}

@PreviewTest
@Preview(
    name = "GithubListLargeFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 1.5f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun GithubListLargeFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        RepositoryListContent(listFixture())
    }
}

@PreviewTest
@Preview(
    name = "GithubListWide",
    widthDp = 840,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListWide() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryListContent(listFixture())
    }
}

@PreviewTest
@Preview(
    name = "GithubListHighContrast",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubListHighContrast() {
    ScreenshotEnvironment(dark = true, portuguese = false, highContrast = true) {
        RepositoryListContent(listFixture())
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailDarkPortuguese",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun GithubDetailDarkPortuguese() {
    ScreenshotEnvironment(dark = true, portuguese = true, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(item = repoFixture()))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailLargeFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 1.5f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun GithubDetailLargeFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(item = repoFixture()))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailWide",
    widthDp = 840,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubDetailWide() {
    ScreenshotEnvironment(dark = false, portuguese = false, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(item = repoFixture()))
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailHighContrast",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 35
)
@Composable
fun GithubDetailHighContrast() {
    ScreenshotEnvironment(dark = true, portuguese = false, highContrast = true) {
        RepositoryDetailContent(GithubDetailState(item = repoFixture()))
    }
}

private fun listFixture() = GithubListState(
    query = "compose",
    language = "Kotlin",
    nextPage = 2,
    items = listOf(repoFixture(), repoFixture(id = 2, missingMetadata = true))
)

private fun repoFixture(id: Long = 1, missingMetadata: Boolean = false) = RepoRO(
    id = id,
    name = "arch-toolkit",
    fullName = "sample/arch-toolkit-with-a-long-repository-name",
    description = if (missingMetadata) {
        null
    } else {
        "Reusable Kotlin Multiplatform components for Android, desktop and iOS. " +
            "A long description checks wrapping and truncation in repository cards."
    },
    updatedAt = LocalDateTime(2026, 9, 1, 12, 30),
    language = if (missingMetadata) null else "Kotlin",
    stargazersCount = 1234567, watchersCount = 23456, forksCount = 1234, openIssuesCount = 42,
    topics = if (missingMetadata) {
        emptyList()
    } else {
        listOf(
            "kotlin",
            "compose-multiplatform",
            "android",
            "open-source"
        )
    },
    // Empty URI is deliberate: no network-dependent avatar in the visual contract.
    owner = UserRO(1, "sample", "")
)

@PreviewTest
@Preview(
    name = "GithubListMaximumFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 2.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun GithubListMaximumFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        RepositoryListContent(listFixture())
    }
}

@PreviewTest
@Preview(
    name = "GithubDetailMaximumFont",
    widthDp = 320,
    heightDp = 800,
    fontScale = 2.0f,
    locale = "pt-rBR",
    apiLevel = 35
)
@Composable
fun GithubDetailMaximumFont() {
    ScreenshotEnvironment(dark = false, portuguese = true, highContrast = false) {
        RepositoryDetailContent(GithubDetailState(item = repoFixture()))
    }
}

@PreviewTest
@Preview(name = "GithubListTabletLandscape", widthDp = 1280, heightDp = 800, apiLevel = 35)
@Composable
fun GithubListTabletLandscape() {
    ScreenshotEnvironment {
        RepositoryListContent(listFixture())
    }
}

@PreviewTest
@Preview(name = "GithubListPhoneLandscape", widthDp = 800, heightDp = 360, apiLevel = 35)
@Composable
fun GithubListPhoneLandscape() {
    ScreenshotEnvironment {
        RepositoryListContent(listFixture())
    }
}
