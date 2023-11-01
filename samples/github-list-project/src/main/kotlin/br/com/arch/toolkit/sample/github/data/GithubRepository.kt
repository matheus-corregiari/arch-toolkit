package br.com.arch.toolkit.sample.github.data

import br.com.arch.toolkit.sample.github.data.remote.ApiProvider
import br.com.arch.toolkit.sample.github.data.remote.api.GithubApi

class GithubRepository internal constructor() {

    private val api: GithubApi by lazyOf(ApiProvider.githubApi)

    fun listRepositories() = api.searchRepositories(page = 1, perPage = 10)
}
