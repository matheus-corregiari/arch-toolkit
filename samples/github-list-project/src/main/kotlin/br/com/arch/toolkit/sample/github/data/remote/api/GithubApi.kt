package br.com.arch.toolkit.sample.github.data.remote.api

import br.com.arch.toolkit.livedata.response.ResponseLiveData
import br.com.arch.toolkit.sample.github.data.remote.model.PageDTO
import br.com.arch.toolkit.sample.github.data.remote.model.PullRequestDTO
import br.com.arch.toolkit.sample.github.data.remote.model.RepoDTO
import br.com.arch.toolkit.splinter.factory.SplinterConfig
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface GithubApi {

    @GET("search/repositories")
    @SplinterConfig("Search Repositories")
    fun searchRepositories(
        @Query("q") query: String = "language:Java",
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): ResponseLiveData<PageDTO<RepoDTO>>

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