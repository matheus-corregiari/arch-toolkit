package br.com.arch.toolkit.sample.github.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserDTO(
    @Json(name = "id") val id: String,
    @Json(name = "login") val login: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "score") val score: Int = 0
)