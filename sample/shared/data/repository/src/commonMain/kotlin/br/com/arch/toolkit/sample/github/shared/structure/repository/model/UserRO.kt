package br.com.arch.toolkit.sample.github.shared.structure.repository.model

import kotlinx.serialization.Serializable

@Serializable
class UserRO(
    val id: Long,
    val login: String,
    val avatarUrl: String
)
