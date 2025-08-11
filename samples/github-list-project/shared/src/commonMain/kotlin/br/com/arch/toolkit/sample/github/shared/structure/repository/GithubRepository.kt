package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.data.remote.api.GithubApi
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.RepoDTO
import br.com.arch.toolkit.splinter.splinterExecuteRequest

class GithubRepository internal constructor(
    private val api: GithubApi
) {

    fun lisRepositories() = splinterExecuteRequest(
        id = "List Repositories",
        request = { api.searchRepositories(page = 1, perPage = 10) }
    ).liveColdFlow

    fun pullRequestsFrom(repo: RepoDTO) = splinterExecuteRequest(
        id = "Pull Requests - ${repo.id}",
        request = { api.listPullRequest(creator = repo.owner.login, repo = repo.name) }
    ).liveFlow
}
