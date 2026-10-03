package br.com.arch.toolkit.sample.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class PageRO(
    val totalCount: Long,
    val incompleteResults: Boolean,
    val items: List<RepoRO>,
    val nextPage: Int? = null
)
