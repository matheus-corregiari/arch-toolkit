package br.com.arch.toolkit.sample.github.shared.structure.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PullRequestDTO(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("user") val user: UserDTO,
    @SerialName("body") val body: String?,
)
