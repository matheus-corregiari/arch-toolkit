package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.api.GithubApi
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.PageResponse
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.PullRequestResponse
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.RepoResponse
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.UserResponse
import br.com.arch.toolkit.storage.memory.MemoryStoreProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class RepositoryTest {
    @Test
    fun storageCrudAndSettingsRestoreFromSameProvider() = runTest {
        val storage = MemoryStoreProvider(database = mutableMapOf())
        val demo = ToolkitDemoRepository(storage)
        demo.save("hello", "world")
        assertEquals("world", ToolkitDemoRepository(storage).read("hello"))
        demo.save("hello", "updated")
        assertEquals("updated", demo.read("hello"))
        demo.delete("hello")
        assertNull(demo.read("hello"))
        SettingsRepository(storage).language.set(AppLanguage.PORTUGUESE_BRAZIL, this)
        testScheduler.advanceUntilIdle()
        assertEquals(AppLanguage.PORTUGUESE_BRAZIL, SettingsRepository(storage).language.current())
    }

    @Test
    fun lumberKeepsBoundedBufferAndUnregistersTree() {
        val demo = ToolkitDemoRepository(MemoryStoreProvider(database = mutableMapOf()))
        demo.start()
        try {
            repeat(120) { demo.writeLog() }
            assertEquals(100, demo.logs.value.size)
            demo.clear()
            assertEquals(emptyList(), demo.logs.value)
        } finally {
            demo.close()
        }
        demo.writeLog()
        assertEquals(emptyList(), demo.logs.value)
    }

    @Test
    fun searchMapsTransportAndRespectsGithubLimit() = runTest {
        val api = FakeApi()
        val repository = RemoteGithubRepository(api)
        val first = repository.search(" compose ", "Kotlin", 1)
        assertEquals("compose language:Kotlin", api.query)
        assertEquals(2, first.nextPage)
        assertEquals("owner/toolkit", first.items.first().fullName)
        assertEquals("owner", first.items.first().owner.login)
        assertNull(repository.search("", "", 50).nextPage)
        assertEquals("stars:>0", api.query)
    }

    @Test
    fun cancellationEscapesAndMalformedResponsesBecomeDomainFailures() = runTest {
        val api = FakeApi()
        val repository = RemoteGithubRepository(api)
        api.failure = CancellationException("cancelled")
        assertFailsWith<CancellationException> { repository.detail("a", "b") }
        api.failure = SerializationException("bad response")
        assertEquals(
            GithubFailure.INVALID_RESPONSE,
            assertFailsWith<GithubException> {
                repository.detail("a", "b")
            }.failure
        )
        api.failure = kotlinx.io.IOException("network")
        assertEquals(
            GithubFailure.CONNECTION,
            assertFailsWith<GithubException> {
                repository.detail("a", "b")
            }.failure
        )
    }

    private class FakeApi : GithubApi {
        var query = ""
        var failure: Exception? = null
        private val item = RepoResponse(
            1, "toolkit", "owner/toolkit", null,
            LocalDateTime(2026, 9, 20, 12, 0), "Kotlin", 1, 2, 3, 4,
            listOf("kotlin"), UserResponse(5, "owner", "avatar")
        )
        override suspend fun repository(owner: String, name: String): RepoResponse {
            failure?.let { throw it }
            return item
        }
        override suspend fun searchRepositories(
            query: String,
            sort: String?,
            order: String?,
            page: Int,
            perPage: Int
        ): PageResponse {
            this.query = query
            return PageResponse(2000, false, List(perPage) { item })
        }
        override suspend fun listPullRequest(
            creator: String,
            repo: String,
            state: String?,
            head: String?,
            base: String?,
            sort: String?,
            order: String?
        ): List<PullRequestResponse> = emptyList()
    }
}
