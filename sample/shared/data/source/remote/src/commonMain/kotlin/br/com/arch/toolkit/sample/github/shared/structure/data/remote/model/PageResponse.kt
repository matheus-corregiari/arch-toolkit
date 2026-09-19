package br.com.arch.toolkit.sample.github.shared.structure.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PageResponse(
    @SerialName("total_count") val totalCount: Long,
    @SerialName("incomplete_results") val incompleteResults: Boolean,
    @SerialName("items") val items: List<RepoResponse>,
    @SerialName("next_page") var nextPage: Int? = null
)
