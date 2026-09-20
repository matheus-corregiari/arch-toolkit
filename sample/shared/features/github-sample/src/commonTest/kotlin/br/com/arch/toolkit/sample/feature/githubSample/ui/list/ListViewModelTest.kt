package br.com.arch.toolkit.sample.feature.githubSample.ui.list

import androidx.lifecycle.SavedStateHandle
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubException
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubFailure
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubRepository
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.PageRO
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.RepoRO
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.UserRO
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {
    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun searchAndFilterRestartPagination() = runTest {
        val calls = mutableListOf<Triple<String, String, Int>>()
        val repository = fake { query, language, number ->
            calls += Triple(query, language, number)
            page(number.toLong(), if (number == 1) 2 else null)
        }
        val model = ListViewModel(repository, SavedStateHandle())
        model.search("compose")
        advanceUntilIdle()
        model.loadMore()
        advanceUntilIdle()
        assertEquals(listOf(1L, 2L), model.state.value.items.map { it.id })
        model.search("compose", "Kotlin")
        advanceUntilIdle()
        assertEquals(Triple("compose", "Kotlin", 1), calls.last())
        assertEquals(listOf(1L), model.state.value.items.map { it.id })
    }

    @Test
    fun oldNonCooperativeResponseCannotReplaceNewSearch() = runTest {
        val oldResponse = CompletableDeferred<PageRO>()
        val repository = fake { query, _, _ ->
            if (query == "old") withContext(NonCancellable) { oldResponse.await() } else page(2)
        }
        val model = ListViewModel(repository, SavedStateHandle())
        model.search("old")
        runCurrent()
        model.search("new")
        runCurrent()
        oldResponse.complete(page(1))
        advanceUntilIdle()
        assertEquals("new", model.state.value.query)
        assertEquals(2L, model.state.value.items.single().id)
    }

    @Test
    fun pageFailurePreservesItemsAndRetriesSamePage() = runTest {
        var fail = true
        val pages = mutableListOf<Int>()
        val repository = fake { _, _, number ->
            pages += number
            if (number == 2 && fail) throw GithubException(GithubFailure.CONNECTION)
            page(number.toLong(), if (number == 1) 2 else null)
        }
        val model = ListViewModel(repository, SavedStateHandle())
        model.loadMore()
        advanceUntilIdle()
        model.loadMore()
        advanceUntilIdle()
        assertEquals(listOf(1L), model.state.value.items.map { it.id })
        assertEquals(GithubFailure.CONNECTION, model.state.value.failure)
        fail = false
        model.loadMore()
        advanceUntilIdle()
        assertEquals(listOf(1, 2, 2), pages)
        assertEquals(listOf(1L, 2L), model.state.value.items.map { it.id })
    }

    @Test
    fun restoresResultsAndFiltersWithoutReloadingOrRestoringLoading() = runTest {
        val saved = SavedStateHandle()
        var requests = 0
        val repository = fake { _, _, _ -> requests++; page(3) }
        val first = ListViewModel(repository, saved)
        first.search("storage", "Kotlin")
        advanceUntilIdle()
        val restored = ListViewModel(repository, saved)
        restored.loadIfNeeded()
        advanceUntilIdle()
        assertEquals(1, requests)
        assertEquals("storage", restored.state.value.query)
        assertEquals("Kotlin", restored.state.value.language)
        assertEquals(3L, restored.state.value.items.single().id)
        assertFalse(restored.state.value.loading)
    }

    private fun fake(search: suspend (String, String, Int) -> PageRO) = object : GithubRepository {
        override suspend fun search(query: String, language: String, page: Int) = search.invoke(query, language, page)
        override suspend fun detail(owner: String, name: String): RepoRO = error("not used")
    }

    private fun page(id: Long, next: Int? = null) = PageRO(
        totalCount = 40,
        nextPage = next,
        incompleteResults = false,
        items = listOf(
            RepoRO(
                id = id,
                name = "toolkit",
                fullName = "owner/toolkit",
                description = null,
                updatedAt = LocalDateTime(2026, 9, 8, 12, 0),
                language = "Kotlin",
                stargazersCount = 1,
                watchersCount = 2,
                forksCount = 3,
                openIssuesCount = 4,
                topics = listOf("kotlin"),
                owner = UserRO(1, "owner", "avatar")
            )
        )
    )
}
