package br.com.arch.toolkit.sample.github.shared.structure.data.remote.api

import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.RepoResponse
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.PageResponse
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.PullRequestResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@Suppress("LongParameterList")
interface GithubApi {
    @GET("repos/{owner}/{name}")
    suspend fun repository(@Path("owner") owner: String, @Path("name") name: String): RepoResponse


    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String = "language:Java",
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PageResponse

    @GET("repos/{creator}/{repo}/pulls")
    suspend fun listPullRequest(
        @Path("creator") creator: String,
        @Path("repo") repo: String,
        @Query("state") state: String? = null,
        @Query("head") head: String? = null,
        @Query("base") base: String? = null,
        @Query("sort") sort: String? = null,
        @Query("direction") order: String? = null
    ): List<PullRequestResponse>
}
