package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.data.remote.api.GithubApi
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.RepoRO
import br.com.arch.toolkit.splinter.splinterExecuteRequest

class GithubRepository(
    private val api: GithubApi
) {

    fun lisRepositories() = splinterExecuteRequest(
        id = "List Repositories",
        request = { api.searchRepositories(page = 1, perPage = 10).toRepositoryObject() }
    ).liveColdFlow

    fun pullRequestsFrom(repo: RepoRO) = splinterExecuteRequest(
        id = "Pull Requests - ${repo.id}",
        request = {
            api.listPullRequest(
                creator = repo.owner.login,
                repo = repo.name
            ).map { it.toRepositoryObject() }
        }
    ).liveFlow
}
