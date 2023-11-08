package br.com.arch.toolkit.sample.github.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PullRequestDTO(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "user") val user: UserDTO,
    @Json(name = "body") val body: String?,
)
