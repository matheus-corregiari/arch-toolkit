package br.com.arch.toolkit.sample.github.data.remote.model

class PullRequestDTO(
    val id: Long = 0,
    val title: String = "",
    val user: UserDTO = UserDTO(),
    val body: String? = ""
)