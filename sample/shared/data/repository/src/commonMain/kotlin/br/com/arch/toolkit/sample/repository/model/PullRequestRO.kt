package br.com.arch.toolkit.sample.repository.model

import kotlinx.serialization.Serializable

@Serializable
class PullRequestRO(
    val id: Long,
    val title: String,
    val user: UserRO,
    val body: String?
)
