package br.com.arch.toolkit.sample.github.shared.structure.repository.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
@Suppress("LongParameterList")
class RepoRO(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val updatedAt: LocalDateTime,
    val language: String? = null,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
    val topics: List<String>,
    val owner: UserRO
)
