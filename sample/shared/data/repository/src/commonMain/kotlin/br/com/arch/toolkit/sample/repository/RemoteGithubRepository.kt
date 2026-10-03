package br.com.arch.toolkit.sample.repository

import br.com.arch.toolkit.sample.data.remote.api.GithubApi
import br.com.arch.toolkit.sample.repository.model.PageRO
import br.com.arch.toolkit.sample.repository.model.RepoRO
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException
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
        response.toRepositoryObject().copy(
            nextPage =
            if (response.items.size == PAGE_SIZE &&
                page * PAGE_SIZE < minOf(response.totalCount, SEARCH_LIMIT)
            ) {
                page + 1
            } else {
                null
            }
        )
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
            when (failure.response.status) {
                HttpStatusCode.Forbidden, HttpStatusCode.TooManyRequests -> GithubFailure.RATE_LIMIT
                HttpStatusCode.NotFound -> GithubFailure.NOT_FOUND
                else -> GithubFailure.CONNECTION
            },
            failure
        )
    } catch (failure: SerializationException) {
        throw GithubException(GithubFailure.INVALID_RESPONSE, failure)
    } catch (failure: IOException) {
        throw GithubException(GithubFailure.CONNECTION, failure)
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val SEARCH_LIMIT = 1000L
    }
}
