package br.com.arch.toolkit.sample.github.shared.structure.data.remote.api

import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.PageDTO
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.PullRequestDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@Suppress("LongParameterList")
interface GithubApi {

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String = "language:Java",
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PageDTO

    @GET("repos/{creator}/{repo}/pulls")
    suspend fun listPullRequest(
        @Path("creator") creator: String,
        @Path("repo") repo: String,
        @Query("state") state: String? = null,
        @Query("head") head: String? = null,
        @Query("base") base: String? = null,
        @Query("sort") sort: String? = null,
        @Query("direction") order: String? = null
    ): List<PullRequestDTO>
}
