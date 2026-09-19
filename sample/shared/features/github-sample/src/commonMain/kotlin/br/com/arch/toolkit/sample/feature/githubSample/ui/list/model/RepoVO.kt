package br.com.arch.toolkit.sample.feature.githubSample.ui.list.model

import br.com.arch.toolkit.sample.github.shared.structure.repository.model.RepoRO
import br.com.arch.toolkit.sample.github.shared.structure.repository.model.UserRO
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
@Suppress("LongParameterList")
class RepoVO(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val updatedAt: LocalDateTime,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
    val topics: List<String>,
    val owner: UserRO
) {
    constructor(dto: RepoRO) : this(
        id = dto.id,
        name = dto.name,
        fullName = dto.fullName,
        description = dto.description,
        updatedAt = dto.updatedAt,
        language = dto.language.orEmpty(),
        stargazersCount = dto.stargazersCount,
        watchersCount = dto.watchersCount,
        forksCount = dto.forksCount,
        openIssuesCount = dto.openIssuesCount,
        topics = dto.topics,
        owner = dto.owner
    )
}
