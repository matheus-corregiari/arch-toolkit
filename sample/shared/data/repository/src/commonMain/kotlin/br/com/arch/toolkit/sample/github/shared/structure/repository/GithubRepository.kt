package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.repository.model.PageRO
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.RepoRO

interface GithubRepository {
    suspend fun search(query: String, language: String, page: Int): PageRO
    suspend fun detail(owner: String, name: String): RepoRO
}

enum class GithubFailure { CONNECTION, RATE_LIMIT, NOT_FOUND, INVALID_RESPONSE }

class GithubException(val failure: GithubFailure) : Exception(failure.name)
