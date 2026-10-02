package br.com.arch.toolkit.sample.repository

import br.com.arch.toolkit.sample.data.remote.model.PageResponse
import br.com.arch.toolkit.sample.data.remote.model.PullRequestResponse
import br.com.arch.toolkit.sample.data.remote.model.RepoResponse
import br.com.arch.toolkit.sample.data.remote.model.UserResponse
import br.com.arch.toolkit.sample.repository.model.PageRO
import br.com.arch.toolkit.sample.repository.model.PullRequestRO
import br.com.arch.toolkit.sample.repository.model.RepoRO
import br.com.arch.toolkit.sample.repository.model.UserRO

internal fun UserResponse.toRepositoryObject() = UserRO(id, login, avatarUrl)

internal fun RepoResponse.toRepositoryObject() = RepoRO(
    id, name, fullName, description, updatedAt, language, stargazersCount,
    watchersCount, forksCount, openIssuesCount, topics, owner.toRepositoryObject()
)

internal fun PageResponse.toRepositoryObject() = PageRO(
    totalCount,
    incompleteResults,
    items.map { it.toRepositoryObject() },
    nextPage
)

internal fun PullRequestResponse.toRepositoryObject() = PullRequestRO(
    id,
    title,
    user.toRepositoryObject(),
    body
)
