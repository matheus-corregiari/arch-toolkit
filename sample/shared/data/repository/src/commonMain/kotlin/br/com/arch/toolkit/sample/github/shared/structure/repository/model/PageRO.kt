package br.com.arch.toolkit.sample.github.shared.structure.repository.model

import kotlinx.serialization.Serializable

@Serializable
class PageRO(
    val totalCount: Long,
    val incompleteResults: Boolean,
    val items: List<RepoRO>,
    var nextPage: Int? = null
)
