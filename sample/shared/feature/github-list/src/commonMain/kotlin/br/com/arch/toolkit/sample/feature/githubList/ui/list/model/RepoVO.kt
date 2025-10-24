package br.com.arch.toolkit.sample.feature.githubList.ui.list.model

import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.RepoDTO
import br.com.arch.toolkit.sample.github.shared.structure.data.remote.model.UserDTO
import br.com.arch.toolkit.stateHandle.SavableObject
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
    val owner: UserDTO
) : SavableObject() {
    constructor(dto: RepoDTO) : this(
        id = dto.id,
        name = dto.name,
        fullName = dto.fullName,
        description = dto.description,
        updatedAt = dto.updatedAt,
        language = dto.language,
        stargazersCount = dto.stargazersCount,
        watchersCount = dto.watchersCount,
        forksCount = dto.forksCount,
        openIssuesCount = dto.openIssuesCount,
        topics = dto.topics,
        owner = dto.owner,
    )
}
