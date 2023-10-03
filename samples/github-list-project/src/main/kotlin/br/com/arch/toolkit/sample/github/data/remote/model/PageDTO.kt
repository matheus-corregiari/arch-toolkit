package br.com.arch.toolkit.sample.github.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PageDTO(
    @Json(name = "total_count") val totalCount: Long,
    @Json(name = "incomplete_results") val incompleteResults: Boolean,
    @Json(name = "items") val items: List<RepoDTO>,
    @Json(name = "next_page") var nextPage: Int? = null
)