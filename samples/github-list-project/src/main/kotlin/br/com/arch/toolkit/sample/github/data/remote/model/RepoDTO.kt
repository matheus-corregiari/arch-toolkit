package br.com.arch.toolkit.sample.github.data.remote.model

class RepoDTO(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val forks: Long,
    val stargazersCount: Long,
    val owner: UserDTO
)