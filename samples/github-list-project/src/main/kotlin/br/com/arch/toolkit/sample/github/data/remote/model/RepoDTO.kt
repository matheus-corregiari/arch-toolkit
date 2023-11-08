package br.com.arch.toolkit.sample.github.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RepoDTO(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "description") val description: String?,
    @Json(name = "forks") val forks: Long,
    @Json(name = "stargazers_count") val stargazersCount: Long,
    @Json(name = "owner") val owner: UserDTO
)
