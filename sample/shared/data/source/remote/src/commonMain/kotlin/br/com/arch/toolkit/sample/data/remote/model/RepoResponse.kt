package br.com.arch.toolkit.sample.data.remote.model

import br.com.arch.toolkit.sample.data.remote.serializer.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("LongParameterList")
class RepoResponse(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("description") val description: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    @SerialName("updated_at") val updatedAt: LocalDateTime,
    @SerialName("language") val language: String? = null,
    @SerialName("stargazers_count") val stargazersCount: Long,
    @SerialName("watchers_count") val watchersCount: Long,
    @SerialName("forks_count") val forksCount: Long,
    @SerialName("open_issues_count") val openIssuesCount: Long,
    @SerialName("topics") val topics: List<String>,
    @SerialName("owner") val owner: UserResponse
)
