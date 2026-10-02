package br.com.arch.toolkit.sample.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRO(
    val id: Long,
    val login: String,
    val avatarUrl: String
)
