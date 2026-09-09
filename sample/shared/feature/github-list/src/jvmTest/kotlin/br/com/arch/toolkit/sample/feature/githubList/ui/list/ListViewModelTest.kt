package br.com.arch.toolkit.sample.feature.githubList.ui.list

import androidx.lifecycle.SavedStateHandle
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.PageDTO
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.RepoDTO
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.UserDTO
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubRepository
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultSuccess
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {

    private val repository = mockk<GithubRepository>()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun loadAndReloadMapSerializableModelsAndKeepTheObservedFlow() = runTest {
        every { repository.lisRepositories() } returnsMany listOf(
            flowOf(dataResultSuccess(page(1))),
            flowOf(dataResultSuccess(page(2)))
        )
        val model = ListViewModel(repository, SavedStateHandle())
        val observed = model.lastPageState.flow()

        model.loadRepositories().join()
        assertEquals(DataResultStatus.SUCCESS, observed.value.status)
        assertEquals(1L, observed.value.data?.single()?.id)
        model.reload().join()

        assertSame(observed, model.lastPageState.flow())
        assertEquals(2L, observed.value.data?.single()?.id)
        verify(exactly = 2) { repository.lisRepositories() }
    }

    @Test
    fun failureRetainsTheLastSuccessfulPayload() = runTest {
        val failure = IllegalStateException("offline")
        every { repository.lisRepositories() } returnsMany listOf(
            flowOf(dataResultSuccess(page(1))),
            flowOf(dataResultError(failure))
        )
        val model = ListViewModel(repository, SavedStateHandle())
        model.loadRepositories().join()
        model.reload().join()

        val result = model.lastPageState.flow().value
        assertEquals(DataResultStatus.ERROR, result.status)
        assertSame(failure, result.error)
        assertEquals(1L, result.data?.single()?.id)
    }

    @Test
    fun restoresJsonPayloadAndLeavesTheLegacySnapshotUntouched() = runTest {
        val legacy = "legacy native snapshot"
        val handle = SavedStateHandle(mapOf("lastPageState" to legacy))
        val model = ListViewModel(repository, handle)
        assertNull(model.lastPageState.get())
        every { repository.lisRepositories() } returns flowOf(dataResultSuccess(page(3)))
        model.loadRepositories().join()

        val encoded = assertNotNull(handle.get<String>("github-repositories-v2"))
        val restoredHandle = SavedStateHandle(mapOf("github-repositories-v2" to encoded))
        val restored = ListViewModel(repository, restoredHandle).lastPageState.flow().value
        assertEquals(DataResultStatus.SUCCESS, restored.status)
        val item = assertNotNull(restored.data).single()
        assertEquals(3L, item.id)
        assertEquals("owner", item.owner.login)
        assertEquals(LocalDateTime(2026, 9, 8, 12, 0), item.updatedAt)
        assertEquals(listOf("kotlin"), item.topics)
        assertEquals(legacy, handle.get<String>("lastPageState"))

        val roundTrip = Json.decodeFromString<PageDTO>(Json.encodeToString(page(3)))
        assertEquals(3L, roundTrip.items.single().id)
        verify(exactly = 1) { repository.lisRepositories() }
    }

    private fun page(id: Long) = PageDTO(
        totalCount = 1,
        incompleteResults = false,
        items = listOf(
            RepoDTO(
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
                owner = UserDTO(1, "owner", "avatar")
            )
        )
    )
}
