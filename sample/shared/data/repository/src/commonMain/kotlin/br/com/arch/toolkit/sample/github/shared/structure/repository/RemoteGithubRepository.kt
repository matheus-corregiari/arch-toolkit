package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.data.remote.api.GithubApi
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.PageRO
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.RepoRO
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

class RemoteGithubRepository(private val api: GithubApi) : GithubRepository {
    override suspend fun search(query: String, language: String, page: Int): PageRO = request {
        require(page > 0)
        val search = buildString {
            append(query.trim().ifEmpty { "stars:>0" })
            if (language.isNotBlank()) append(" language:" + language.trim())
        }
        val response = api.searchRepositories(query = search, page = page, perPage = PAGE_SIZE)
        response.toRepositoryObject().apply {
            nextPage = if (response.items.size == PAGE_SIZE && page * PAGE_SIZE < minOf(response.totalCount, SEARCH_LIMIT)) {
                page + 1
            } else {
                null
            }
        }
    }

    override suspend fun detail(owner: String, name: String): RepoRO = request {
        require(owner.isNotBlank() && name.isNotBlank())
        api.repository(owner, name).toRepositoryObject()
    }

    private suspend fun <T> request(block: suspend () -> T): T = try {
        block()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: ResponseException) {
        throw GithubException(
            when (failure.response.status.value) {
                403, 429 -> GithubFailure.RATE_LIMIT
                404 -> GithubFailure.NOT_FOUND
                else -> GithubFailure.CONNECTION
            }
        )
    } catch (failure: SerializationException) {
        throw GithubException(GithubFailure.INVALID_RESPONSE)
    } catch (failure: Exception) {
        throw GithubException(GithubFailure.CONNECTION)
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val SEARCH_LIMIT = 1000L
    }
}
